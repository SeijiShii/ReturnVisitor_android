package net.c_kogyo.returnvisitor.activity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.Manifest.permission;

import net.c_kogyo.returnvisitor.data.Place;
import net.c_kogyo.returnvisitor.data.RVData;
import net.c_kogyo.returnvisitor.data.Visit;
import net.c_kogyo.returnvisitor.data.Work;
import net.c_kogyo.returnvisitor.dialog.AddWorkDialog;
import net.c_kogyo.returnvisitor.dialog.MarkerDialog;
import net.c_kogyo.returnvisitor.dialog.UserDataDialog;
import net.c_kogyo.returnvisitor.enums.AddressTextLanguage;
import net.c_kogyo.returnvisitor.R;
import net.c_kogyo.returnvisitor.dialog.LoginSelectDialog;
import net.c_kogyo.returnvisitor.service.TimeCountService;
import net.c_kogyo.returnvisitor.util.AdMobHelper;
import net.c_kogyo.returnvisitor.util.MailReport;
import net.c_kogyo.returnvisitor.view.CollapseButton;
import net.c_kogyo.returnvisitor.view.HeightChangeFrameLayout;

import static net.c_kogyo.returnvisitor.activity.Constants.LogInCode.GOOGLE_SIGN_IN_RC;
import static net.c_kogyo.returnvisitor.activity.Constants.SharedPrefTags.LATITUDE;
import static net.c_kogyo.returnvisitor.activity.Constants.SharedPrefTags.LONGITUDE;
import static net.c_kogyo.returnvisitor.activity.Constants.SharedPrefTags.RETURN_VISITOR_SHARED_PREFS;
import static net.c_kogyo.returnvisitor.activity.Constants.SharedPrefTags.ZOOM_LEVEL;
import static net.c_kogyo.returnvisitor.util.DateTimeText.getDurationString;

public class MapActivity extends AppCompatActivity
                            implements OnMapReadyCallback,
                                        GoogleApiClient.OnConnectionFailedListener,
                                        FacebookCallback<LoginResult>,
                                        OnCompleteListener<AuthResult>{


    static final String MAP_DEBUG ="map_debug";


    static public boolean isInForeground;
    private static final float alphaLevel = 0.3f;

    private MapView mMapView;
    private GoogleMap mMap;
    private boolean isMapReady;
//    private boolean isDataReady;

    private Handler markerHandler, mapListenerHandler;

    public static AddressTextLanguage addressTextLang;

    private MABroadCastReceiver broadcastReceiver;

    // 起動時間タイマー
    private static long timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timer = Calendar.getInstance().getTimeInMillis();

//        isDataReady = false;
        isMapReady = false;

        markerHandler = new Handler();
        mapListenerHandler = new Handler();

        addressTextLang = AddressTextLanguage.USE_DEVICE_LOCALE;

        broadcastReceiver = new MABroadCastReceiver();


        initFirebaseAuth();
        initGoogleSignIn();
        initFacebookLogin();

        // ログアウト状態でアプリを起動したら墜ちた
        initDateIfAuthed();

        setContentView(R.layout.map_activity);
        AdMobHelper.setAdView(this);

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        createToolBar();
        initGuideText();
        initDrawer();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

        mMapView.getMapAsync(this);

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver, new IntentFilter(TimeCountService.TIME_COUNTING_ACTION));
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver, new IntentFilter(TimeCountService.STOP_TIME_COUNT_ACTION));

        enableTimeCount(TimeCountService.isTimeCounting());

        timeFrame.changeHeight(TimeCountService.isTimeCounting(), false, null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);

        saveCameraPosition();

        try {

            mMap.setMyLocationEnabled(false);
        } catch (SecurityException e) {
            //
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
        isInForeground = true;

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }

        isInForeground = false;


    }

    private void initDateIfAuthed() {

        if (firebaseAuth.getCurrentUser() != null) {

            RVData.getInstance().initWithListenersAndLoad(
                    this,
                    new RVData.OnDataLoadedListener() {
                          @Override
                          public void onDataLoaded() {

                              showTimeLog("Data loaded");
                              showMarkers(firebaseAuth.getCurrentUser() != null, markerHandler);
                              showTimeLog("showMarker called in RVDataCallback");
                          }
                      },
                    new RVData.OnDataChangedListener() {
                        @Override
                        public void onDataChanged(Class clazz) {

                        }
                    });

        }

    }

    private void loadCameraPosition() {
        SharedPreferences prefs = getSharedPreferences(RETURN_VISITOR_SHARED_PREFS, MODE_PRIVATE);
        float zoomLevel = prefs.getFloat(ZOOM_LEVEL, 0f);
        double lat = Double.valueOf(prefs.getString(LATITUDE, "1000"));
        double lng = Double.valueOf(prefs.getString(LONGITUDE, "1000"));

        if (lat >= 1000 || lng >= 1000) return;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoomLevel));
    }

    private void saveCameraPosition() {

        SharedPreferences prefs = getSharedPreferences(RETURN_VISITOR_SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        float zoomLevel = mMap.getCameraPosition().zoom;
        String latSt = String.valueOf(mMap.getCameraPosition().target.latitude);
        String lngSt = String.valueOf(mMap.getCameraPosition().target.longitude);

        editor.putFloat(ZOOM_LEVEL, zoomLevel);
        editor.putString(LATITUDE, latSt);
        editor.putString(LONGITUDE, lngSt);

        editor.apply();
    }

    // ドロワーを開閉するにはToolBarが必要。
    // AppThemeはNoActionBarに指定しないとWindow Decorと衝突する
    private Toolbar toolbar;
    private void createToolBar(){

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.app_name);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    //ログインしていない状態でデータにアクセスさせない備え
    //ログアウトしたらマーカー類が消えるように

    private static final String MY_LOCATION_TAG = "my_location";
    @Override
    public void onMapReady(GoogleMap googleMap) {

        isMapReady = true;

        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Permissionの扱いが変化するため
            try {
                mMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {

                Log.d(MY_LOCATION_TAG,e.getMessage());
            }
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            if (Build.VERSION.SDK_INT >= 23) {

                setMyLocationButton();
            }

        // xmlでの指定の方法が分からん
        mMap.setPadding(0, 40, 0, 0);

        loadCameraPosition();
        setMapListeners(firebaseAuth.getCurrentUser() != null);

        showTimeLog("showMarker called in onMapReady");
        showMarkers(firebaseAuth.getCurrentUser() != null, markerHandler);

    }

    private void setMapListeners(final boolean set) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (!isMapReady) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        //
                    }
                }
                mapListenerHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (set) {

                            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                                @Override
                                public void onMapLongClick(LatLng latLng) {

                                    // MapActivity長押し時に地図がスクロールするようにと思ったがタイミング的に無理みたい

                                    if (firebaseAuth.getCurrentUser() != null) {

                                        startRecordVisitActivityByPosition(latLng);

                                    } else {

                                        Toast.makeText(MapActivity.this, R.string.login_needed, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {

                                    Place place = RVData.getInstance().placeList.getByMarkerId(marker.getId());

                                    if (place != null) {

                                        MarkerDialog.newInstance(place,
                                                new MarkerDialog.OnVisitRecordClickListener() {
                                                    @Override
                                                    public void onVisitRecordClick(Place place) {
                                                        startRecordVisitByPlaceId(place.getId());
                                                    }
                                                },
                                                new MarkerDialog.OnPlaceRemoveListener() {
                                                    @Override
                                                    public void onPlaceRemoved(Place place) {
                                                        showTimeLog("showMarker called in onMarkerClick");
                                                        showMarkers(firebaseAuth.getCurrentUser() != null, markerHandler);
                                                    }
                                                }).show(getFragmentManager(), null);
                                    } else {

                                        // マーカーをクリックしたものの該当する場所が見つからない場合はマーカーを削除
                                        marker.remove();
                                    }

                                    return false;
                                }
                            });

                        } else {

                            mMap.setOnMarkerClickListener(null);
                            mMap.setOnMapLongClickListener(null);

                        }
                    }
                });
            }
        }).start();

    }

    private void startRecordVisitActivityByPosition(LatLng latLng) {

        Intent recordVisitIntent = new Intent(this, RecordVisitActivity.class);
        recordVisitIntent.setAction(Constants.RecordVisitActions.NEW_PLACE_ACTION);
        if (latLng != null) {

            recordVisitIntent.putExtra(LATITUDE, latLng.latitude);
            recordVisitIntent.putExtra(LONGITUDE, latLng.longitude);
        }

        startActivity(recordVisitIntent);
    }

    private void startRecordVisitByPlaceId(String id) {
        Intent recordVisitIntent = new Intent(this, RecordVisitActivity.class);
        recordVisitIntent.setAction(Constants.RecordVisitActions.NEW_VISIT_ACTION_WITH_PLACE);
        recordVisitIntent.putExtra(Place.PLACE, id);
        startActivity(recordVisitIntent);
    }

//    private DatabaseReference reference;
//    private void initFirebaseDatabase() {
//        reference = FirebaseDatabase.newInstance().getReference();
//
//    }

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 717;
    private void setMyLocationButton() {

        if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.d(MY_LOCATION_TAG, "Permissions yet given.");

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permission.ACCESS_COARSE_LOCATION)) {

                Log.d(MY_LOCATION_TAG, "Should show Explanation.");
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.


                ActivityCompat.requestPermissions(this,
                        new String[]{permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);

                // MY_PERMISSIONS_REQUEST_ACCESS_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Log.d(MY_LOCATION_TAG, "YES! permissions given.");

                    try {
                        mMap.setMyLocationEnabled(true);
                    } catch (SecurityException e) {
                        Log.d(MY_LOCATION_TAG,e.getMessage());
                    }
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private DrawerLayout navDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private void initDrawer() {

        navDrawer = (DrawerLayout) findViewById(R.id.drawer);
        // toolBarを設定するコンストラクタを使用する必要がある
        mDrawerToggle = new ActionBarDrawerToggle(this, navDrawer, toolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();

        initLoginButton();
        initAnonymousLoginButton();
        initLogoutButton();
        initTimeFrame();
        initWorkButton();
        initAddWorkButton();
        initAddVisitButton();
        initCalendarButton();
        initUserDialogButton();
        initTermButton();
        initReportMailButton();

    }

    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private void initGoogleSignIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private void initFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                    initDateIfAuthed();

                    String name = firebaseAuth.getCurrentUser().getDisplayName();
                    if (name == null) {
                        name = firebaseAuth.getCurrentUser().getEmail();
                    }

                    if (name == null) {
                        name = getString(R.string.no_name);
                    }

                    String loginText = getString(R.string.logged_in_as, name);
                    Toast.makeText(MapActivity.this, loginText, Toast.LENGTH_SHORT).show();

                } else {
                // ログアウト時にデータがすべて消去されるようにする

                    RVData.getInstance().clearFromLocal();

                }
                animateLoginButton(user == null || loggedInAnonymously());
                animateAnonymousLoginButton(user == null);
                animateLogoutButton(user != null);

                showTimeLog("showMarker called in onAuthStateChange");
                showMarkers(user != null, markerHandler);

                setMapListeners(user != null);
                enableTimeCount(user != null);
                initGuideText();

                updateWorkButton();
                updateAddWorkButton();
                updateAddVisitButton();
                updateReportMailButton();
            }
        };
    }

    private CollapseButton loginButton;
    private View loginBorder;
    private void initLoginButton() {

        loginButton = (CollapseButton) findViewById(R.id.login_button);
        loginBorder = findViewById(R.id.login_border);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        // ログインボタンを表示する基準はログインしていないか無名ログインしているか
        if (user == null || loggedInAnonymously()) {

            loginButton.setHeight(false);
            loginBorder.setVisibility(View.VISIBLE);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginSelectDialog.newInstance(
                            new OnGoogleSignInClickListener(),
                            new OnFBLoinClickListener(),
                            new EmailLoginClickListener())
                            .show(getFragmentManager(), "Login_dialog");
                }
            });
        } else {
            loginButton.setHeight(true);
            loginButton.setOnClickListener(null);
            loginBorder.setVisibility(View.INVISIBLE);
        }

    }

    private void animateLoginButton(boolean extract) {

        if (extract) {

            loginButton.setHeight(false);
            loginBorder.setVisibility(View.VISIBLE);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginSelectDialog.newInstance(
                            new OnGoogleSignInClickListener(),
                            new OnFBLoinClickListener(),
                            new EmailLoginClickListener())
                            .show(getFragmentManager(), "Login_dialog");
                }
            });
        } else {
            loginButton.setHeight(true);
            loginButton.setOnClickListener(null);
            loginBorder.setVisibility(View.INVISIBLE);
        }
        loginButton.animateHeight(extract);
    }

    private CollapseButton anonymousLoginButton;
    private View anonymousBorder;
    private void initAnonymousLoginButton() {

        anonymousLoginButton = (CollapseButton) findViewById(R.id.anonymous_login_button);
        anonymousBorder = findViewById(R.id.anonymous_login_border);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        anonymousLoginButton.setHeight(user != null);
        if (user == null) {

            anonymousBorder.setVisibility(View.VISIBLE);
            anonymousLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    confirmAnonymousLogin();
                }
            });

        } else {

            anonymousBorder.setVisibility(View.INVISIBLE);
            anonymousLoginButton.setOnClickListener(null);

        }
    }

    private void animateAnonymousLoginButton(boolean extract) {

        if (extract) {

            anonymousBorder.setVisibility(View.VISIBLE);
            anonymousLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    confirmAnonymousLogin();
                }
            });

        } else {

            anonymousBorder.setVisibility(View.INVISIBLE);
            anonymousLoginButton.setOnClickListener(null);
        }

        anonymousLoginButton.animateHeight(extract);
    }

    private void confirmAnonymousLogin() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.anonymous_title);
        builder.setMessage(R.string.anonymous_message);
        builder.setNegativeButton(R.string.cancel_text, null);
        builder.setNeutralButton(R.string.login, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LoginSelectDialog.newInstance(
                        new OnGoogleSignInClickListener(),
                        new OnFBLoinClickListener(),
                        new EmailLoginClickListener())
                        .show(getFragmentManager(), "Login_dialog");
            }
        });
        builder.setPositiveButton(R.string.anonymous_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                anonymousLogin();
            }
        });
        builder.create().show();
    }

    private boolean loggedInAnonymously() {

        FirebaseUser user = firebaseAuth.getCurrentUser();

        return user != null && user.getDisplayName() == null && user.getEmail() == null;

    }

    private void anonymousLogin() {

        firebaseAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Log.w("AnonymousLogin", "signInAnonymously", task.getException());
                    Toast.makeText(MapActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private CollapseButton logoutButton;
    private View logoutBorder;
    private void initLogoutButton() {

        logoutButton = (CollapseButton) findViewById(R.id.log_out_button);
        logoutBorder = findViewById(R.id.log_out_border);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

            String name = firebaseAuth.getCurrentUser().getDisplayName();
            if (name == null) {
                name = firebaseAuth.getCurrentUser().getEmail();
            }

            if (name == null) {
                name = getString(R.string.no_name);
            }

            String logoutText = getString(R.string.log_out, name);
            logoutButton.setText(logoutText);

            logoutButton.setHeight(false);
            logoutBorder.setVisibility(View.VISIBLE);

            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    signOutFromFirebaseAuth();
                }
            });

        } else {

            logoutButton.setHeight(true);
            logoutBorder.setVisibility(View.INVISIBLE);

            logoutBorder.setOnClickListener(null);
        }
    }

    private void animateLogoutButton(boolean extract) {

        if (extract) {

            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                String name = firebaseAuth.getCurrentUser().getDisplayName();
                if (name == null) {
                    name = firebaseAuth.getCurrentUser().getEmail();
                }

                if (name == null) {
                    name = getString(R.string.no_name);
                }

                String logoutText = getString(R.string.log_out, name);
                logoutButton.setText(logoutText);
                logoutBorder.setVisibility(View.VISIBLE);

                logoutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        signOutFromFirebaseAuth();
                    }
                });
            }
        } else {

            logoutButton.setOnClickListener(null);
            logoutBorder.setVisibility(View.INVISIBLE);

        }
        logoutButton.animateHeight(extract);
    }

    private void signOutFromFirebaseAuth() {

        new AlertDialog.Builder(this).setTitle(R.string.log_out_text)
                .setMessage(R.string.log_out_message)
                .setPositiveButton(R.string.log_out_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                    }
                })
                .setNegativeButton(R.string.cancel_text, null)
                .create()
                .show();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN_RC) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else if (requestCode == Constants.RecordVisitActions.NEW_VISIT_REQUEST_CODE) {

            if (resultCode == Constants.RecordVisitActions.VISIT_ADDED_RESULT_CODE) {

                Intent workActivityIntent = new Intent(MapActivity.this, WorkPagerActivity.class);

                // Intentにvisitの日付を仕込む
                String visitId = data.getStringExtra(Visit.VISIT);
                if (visitId != null) {
                    Visit visit = RVData.getInstance().visitList.getById(visitId);

                    if (visit != null) {
                        Calendar date = visit.getStart();
                        workActivityIntent.putExtra(Constants.DATE_LONG, date.getTimeInMillis());
                    }
                }

                startActivity(workActivityIntent);
            }

        } else {

            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Email Login
    private String mEmail;
    private String mPassword;
    private static final String EMAIL_LOG_IN_TAG = "email_login_tag";
    public class EmailLoginClickListener {

        public void onClick(String email, String password) {
//            Toast.makeText(MapActivity.this, "Email Sign in Clicked", Toast.LENGTH_SHORT).show();
            navDrawer.closeDrawer(Gravity.LEFT);

            mEmail = email;
            mPassword = password;

            if (firebaseAuth.getCurrentUser() == null) {

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MapActivity.this, MapActivity.this);
            } else if (loggedInAnonymously()) {

                AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                firebaseAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(FIREBASE_TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(FIREBASE_TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MapActivity.this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        Log.d(EMAIL_LOG_IN_TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

        // If sign in fails, display a message to the user. If sign in succeeds
        // the auth state listener will be notified and logic to handle the
        // signed in user can be handled in the listener.
        if (!task.isSuccessful()) {

            FirebaseAuthException e = (FirebaseAuthException) task.getException();
            if (e != null) {

                Log.d(EMAIL_LOG_IN_TAG, e.getErrorCode());
//                Toast.makeText(MapActivity.this, e.getErrorCode(), Toast.LENGTH_SHORT).show();

                if (e.getErrorCode().equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                    firebaseAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(MapActivity.this, MapActivity.this);
                } else if (e.getErrorCode().equals("ERROR_WRONG_PASSWORD")) {
                    Toast.makeText(MapActivity.this, R.string.wrong_password, Toast.LENGTH_SHORT).show();
                } else if (e.getErrorCode().equals("ERROR_WEAK_PASSWORD")) {
                    Toast.makeText(MapActivity.this, R.string.weak_password, Toast.LENGTH_SHORT).show();
                }

            }


        } else {
            mEmail = null;
            mPassword = null;
        }

        // ...
    }

    // Google Sign in
    public class OnGoogleSignInClickListener {

        public void onClick() {
//            Toast.makeText(MapActivity.this, "Google Sign in Clicked", Toast.LENGTH_SHORT).show();
            navDrawer.closeDrawer(Gravity.LEFT);
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN_RC);
        }
    }

    private static final String GOOGLE_SIGN_IN_TAG = "google_sign_in_tag";
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(GOOGLE_SIGN_IN_TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);

        } else {
            // Signed out, show unauthenticated UI.
//            updateUI(false);
        }
    }

    private static final String FIREBASE_TAG = "firebase_tag";
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(FIREBASE_TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        if (firebaseAuth.getCurrentUser() == null) {
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(FIREBASE_TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(FIREBASE_TAG, "signInWithCredential", task.getException());
                                Toast.makeText(MapActivity.this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        } else if (loggedInAnonymously()) {

            firebaseAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Log.w(FIREBASE_TAG, "signInWithCredential", task.getException());
                        Toast.makeText(MapActivity.this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }


    }

    public class OnFBLoinClickListener {

        public void onClick() {
            navDrawer.closeDrawer(Gravity.LEFT);
            performFBLogin();
        }
    }

    private CallbackManager mCallbackManager;
    private void initFacebookLogin() {

        FacebookSdk.sdkInitialize(this);
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(mCallbackManager, this);
    }

    private void performFBLogin() {

        LoginManager loginManager = LoginManager.getInstance();
        List<String> permissions = Arrays.asList("email", "public_profile");

        loginManager.logInWithReadPermissions(this, permissions);

    }

    private static final String FB_TAG = "facebook_tag";

    @Override
    public void onSuccess(LoginResult loginResult) {

        Log.d(FB_TAG, "facebook:onSuccess:" + loginResult);
        handleFacebookAccessToken(loginResult.getAccessToken());
    }

    @Override
    public void onCancel() {

        Log.d(FB_TAG, "facebook:onCancel");
        // ...

    }

    @Override
    public void onError(FacebookException error) {

        Log.d(FB_TAG, "facebook:onError", error);
        // ...
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(FB_TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        if (firebaseAuth.getCurrentUser() == null ) {
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(FB_TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {

                                if (task.getException() instanceof FirebaseAuthException) {
                                    if (((FirebaseAuthException)task.getException()).getErrorCode().equals("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL")){
                                        Toast.makeText(MapActivity.this, R.string.different_credential, Toast.LENGTH_SHORT).show();
                                    }
                                }

                                Log.w(FB_TAG, "signInWithCredential", task.getException());

                            }

                            // ...
                        }
                    });

        } else if (loggedInAnonymously()) {

            firebaseAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {
                        Toast.makeText(MapActivity.this, getString(R.string.auth_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }


    }

//    ArrayList<Marker> markers;
    private void showMarkers(final boolean show, final Handler markerHandler) {

        showTimeLog("Show marker");

        if (show) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!isMapReady) {

                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            //
                        }
                    }
                    markerHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            mMap.clear();
                            showTimeLog("Marker cleared");

                            showTimeLog("Place count: " + RVData.getInstance().placeList.getList().size());

                            // 起動時、データを読み込んだ後に表示するよう調整
                            for ( Place place : RVData.getInstance().placeList ) {

                                MarkerOptions options = new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(Constants.markerRes[place.getInterest().num()]))
                                        .position(place.getLatLng());

                                Marker marker = mMap.addMarker(options);
                                place.setMarkerId(marker.getId());

                                showTimeLog("Marker Added ID: " + marker.getId());
                            }
                        }
                    });

                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {



                    while (!isMapReady) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            //
                        }
                    }

                    markerHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            mMap.clear();
                        }
                    });
                }
            }).start();
        }
    }

    // 時間管理ボタンの実装
    // ログイン、ログアウトで時間ボタンのアクセシビリティが変わる
    private HeightChangeFrameLayout timeFrame;
    private TextView startTimeText, durationText;
    private void initTimeFrame() {

        timeFrame = (HeightChangeFrameLayout) findViewById(R.id.time_frame);
        timeFrame.setHeight(TimeCountService.isTimeCounting());

        initTimeCountButton();

        startTimeText = (TextView) findViewById(R.id.start_time_text);
        durationText = (TextView) findViewById(R.id.duration_text);

    }

    // Login, off
    private void enableTimeCount(boolean enable) {

        timeCountButton.setText(R.string.time_count_button);
        timeCountButton.setBackgroundResource(R.drawable.trans_green_selector);
        timeCountButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        if (enable) {

            timeCountButton.setAlpha(1f);
            timeCountButton.setClickable(true);

        } else {

            if (TimeCountService.isTimeCounting()) {

                // 実際のカウントストップを実装
                TimeCountService.stopTimeCount();
            }

            timeCountButton.setAlpha(alphaLevel);
            timeCountButton.setClickable(false);

            timeFrame.changeHeight(false, true, null);
        }
        timeCountButton.requestLayout();
    }

    private Button timeCountButton;
    private void initTimeCountButton() {

        timeCountButton = (Button) findViewById(R.id.time_count_button);

        timeCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TimeCountService.isTimeCounting()) {
                    TimeCountService.stopTimeCount();

                } else {

                    Intent startTimeCountIntent = new Intent(MapActivity.this, TimeCountService.class);

                    // システム側にサービスを止められるためこちらでWorkを生成する。
                    Work work = new Work(Calendar.getInstance());
                    RVData.getInstance().workList.addOrSet(work);
                    String workId = work.getId();
                    startTimeCountIntent.putExtra(Work.WORK, workId);

                    startService(startTimeCountIntent);

                }
            }
        });

    }


    //Guide barの実装
    private void initGuideText() {

        TextView guideText = (TextView) findViewById(R.id.guide_text);

        if (firebaseAuth.getCurrentUser() == null) {
            guideText.setText(R.string.must_login);
        } else {
            guideText.setText(R.string.long_click_on_map);
        }

    }

    private Calendar startCal;
    class MABroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TimeCountService.TIME_COUNTING_ACTION)) {

                if (TimeCountService.isTimeCounting()) {
                    timeCountButton.setText(R.string.stop_time_count);
                    timeCountButton.setBackgroundResource(R.drawable.trans_orange_selector);
                    timeCountButton.setTextColor(ContextCompat.getColor(MapActivity.this, R.color.colorAccent));

                    timeFrame.changeHeight(TimeCountService.isTimeCounting(), true, null);

                    startTimeText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new TimePickerDialog(MapActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int i, int i1) {

                                    Calendar setTime = Calendar.getInstance();
                                    setTime.set(Calendar.HOUR_OF_DAY, i);
                                    setTime.set(Calendar.MINUTE, i1);

                                    Calendar now = Calendar.getInstance();
                                    if (setTime.before(now)) {

                                        Intent startChangeIntent = new Intent(TimeCountService.START_CHANGE_ACTION);
                                        startChangeIntent.putExtra(TimeCountService.START_TIME, setTime.getTimeInMillis());

                                        LocalBroadcastManager.getInstance(MapActivity.this).sendBroadcast(startChangeIntent);
                                    }
                                }
                            },
                                    startCal.get(Calendar.HOUR_OF_DAY),
                                    startCal.get(Calendar.MINUTE),
                                    true).show();
                        }
                    });

                    long duration = intent.getLongExtra(TimeCountService.DURATION, 0);
                    long startTime = intent.getLongExtra(TimeCountService.START_TIME, 0);

                    if (duration != 0 && startTime != 0) {

                        SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm", Locale.getDefault());
                        startCal = Calendar.getInstance();
                        startCal.setTimeInMillis(startTime);
                        String startTimeString = MapActivity.this.getResources().getString(R.string.start_time_text, timeFormat.format(startCal.getTime()));
                        startTimeText.setText(startTimeString);

                        String durationTimeString = MapActivity.this.getResources().getString(R.string.duration_text, getDurationString(duration, true));
                        durationText.setText(durationTimeString);
                    }
                }

            } else if (intent.getAction().equals(TimeCountService.STOP_TIME_COUNT_ACTION)) {

                timeFrame.changeHeight(TimeCountService.isTimeCounting(), true, null);

                startTimeText.setOnClickListener(null);

                timeCountButton.setText(R.string.time_count_button);
                timeCountButton.setBackgroundResource(R.drawable.trans_green_selector);
                timeCountButton.setTextColor(ContextCompat.getColor(MapActivity.this, R.color.colorPrimaryDark));

            }
        }
    }

    private Button workButton;
    private void initWorkButton() {

        workButton = (Button) findViewById(R.id.work_button);
        updateWorkButton();
    }

    private void updateWorkButton() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            workButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent workActivityIntent = new Intent(MapActivity.this, WorkPagerActivity.class);
                    startActivity(workActivityIntent);
                }
            });
            workButton.setAlpha(1f);
            workButton.setClickable(true);
        } else {
            workButton.setClickable(false);
            workButton.setAlpha(alphaLevel);
            workButton.setOnClickListener(null);
        }

    }

    private Button addWorkButton;
    private void initAddWorkButton() {

        addWorkButton = (Button) findViewById(R.id.add_work_button);
        updateAddWorkButton();

    }

    private void updateAddWorkButton() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            addWorkButton.setAlpha(1f);
            addWorkButton.setClickable(true);

            addWorkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AddWorkDialog.newInstance(Calendar.getInstance(),
                            new AddWorkDialog.OnWorkSetListener() {
                                @Override
                                public void onWorkSet(Work work) {

                                    RVData.getInstance().workList.addOrSet(work);

                                    // WorkActivityに遷移
                                    Intent workActivityIntent = new Intent(MapActivity.this, WorkPagerActivity.class);

                                    // Intentにworkの日付を仕込む

                                    workActivityIntent.putExtra(Constants.DATE_LONG, work.getStart().getTimeInMillis());

                                    startActivity(workActivityIntent);
                                }
                            }).show(getFragmentManager(), null);
                }
            });
        } else {

            addWorkButton.setAlpha(alphaLevel);
            addWorkButton.setClickable(false);
            addWorkButton.setOnClickListener(null);
        }
    }

    private Button addVisitButton;
    private void initAddVisitButton() {

        addVisitButton = (Button) findViewById(R.id.add_visit_button);
        updateAddVisitButton();
    }

    private void updateAddVisitButton() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            addVisitButton.setAlpha(1f);
            addVisitButton.setClickable(true);
            addVisitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent addVisitIntent = new Intent(MapActivity.this, RecordVisitActivity.class);
                    addVisitIntent.setAction(Constants.RecordVisitActions.NEW_VISIT_ACTION_NO_PLACE);

                    startActivityForResult(addVisitIntent, Constants.RecordVisitActions.NEW_VISIT_REQUEST_CODE);
                    // onActivityResult内にWorkActivityへの遷移を仕込む
                }
            });

        } else {
            addVisitButton.setAlpha(alphaLevel);
            addVisitButton.setClickable(false);
            addVisitButton.setOnClickListener(null);

        }

    }

    private void initCalendarButton() {

        final Button calendarButton = (Button) findViewById(R.id.calendar_button);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent calendarIntent = new Intent(MapActivity.this, CalendarActivity.class);
                calendarIntent.setAction(Constants.CalendarActions.START_CALENDAR_FROM_MAP_ACTION);
                calendarIntent.putExtra(Constants.DATE_LONG, Calendar.getInstance().getTimeInMillis());
                startActivity(calendarIntent);

            }
        });
    }

    private void initUserDialogButton() {

        Button userDialogButton = (Button) findViewById(R.id.user_data_button);
        userDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserDataDialog.newInstance().show(getFragmentManager(), null);

            }
        });

    }

    private void initTermButton() {

        Button termButton = (Button) findViewById(R.id.term_of_use_button);
        termButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showTermDialog();
            }
        });
    }

    private void showTermDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.term_of_use_title);

        TextView textView = new TextView(this);
        textView.setText(R.string.term_of_use_message);

        textView.setLinkTextColor(Color.BLUE);

        Linkify.addLinks(textView, Linkify.WEB_URLS);

        textView.setPadding(10, 10, 10, 10);

        builder.setView(textView);

        builder.setPositiveButton(R.string.ok_text, null);

        builder.create().show();

    }

    private Button reportMailButton;
    private void initReportMailButton() {

        reportMailButton = (Button) findViewById(R.id.report_mail_button);
        updateReportMailButton();
    }

    private void updateReportMailButton() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            reportMailButton.setAlpha(1f);
            reportMailButton.setClickable(true);
            reportMailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MailReport.exportToMail(MapActivity.this, Calendar.getInstance());
                }
            });

        } else {

            reportMailButton.setAlpha(alphaLevel);
            reportMailButton.setClickable(false);
            reportMailButton.setOnClickListener(null);
        }
    }

    private static final String APP_TIMER_TAG = "AppTimer";
    public static void showTimeLog(String message) {

        long now = Calendar.getInstance().getTimeInMillis();

        Log.d(APP_TIMER_TAG, message + ", time: " + (now - timer));

    }
}


