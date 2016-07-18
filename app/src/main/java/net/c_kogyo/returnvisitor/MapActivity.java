package net.c_kogyo.returnvisitor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.internal.CollectionMapper;
import com.facebook.internal.LoginAuthorizationType;
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
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MapActivity extends AppCompatActivity
                            implements OnMapReadyCallback,
                                        GoogleApiClient.OnConnectionFailedListener,
                                        FacebookCallback<LoginResult>,
                                        OnCompleteListener<AuthResult>{


    static final String MAP_DEBUG ="map_debug";

    // Shared Preferences用のタグ類
    static final String RETURN_VISITOR_SHARED_PREFS = "return_visitor_shared_prefs";
    static final String ZOOM_LEVEL = "zoom_level";
    static final String LATITUDE = "latitude";
    static final String LONGITUDE = "longitude";

    MapView mMapView;
    GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFirebaseAuth();
        initGoogleSignIn();
        initFacebookLogin();

        setContentView(R.layout.activity_map);

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        createToolBar();
        createDrawer();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

        mMapView.getMapAsync(this);


    }

    @Override
    protected void onPause() {
        super.onPause();

        saveCameraPosition();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
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
//        toolbar.inflateMenu(R.menu.return_visitor_menu);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {

        }

        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // xmlでの指定の方法が分からん
        mMap.setPadding(0, 35, 0, 0);

        loadCameraPosition();

        Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(mMap.getCameraPosition().target)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_blue)));

    }

    private DrawerLayout navDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private void createDrawer() {

        navDrawer = (DrawerLayout) findViewById(R.id.drawer);
        // toolBarを設定するコンストラクタを使用する必要がある
        mDrawerToggle = new ActionBarDrawerToggle(this, navDrawer, toolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        createLoginOutButton();

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

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private void initFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
                    setLogOutButton();

                    String name = firebaseAuth.getCurrentUser().getDisplayName();
                    if (name == null) {
                        name = firebaseAuth.getCurrentUser().getEmail();
                    }

                    Toast.makeText(MapActivity.this, name, Toast.LENGTH_SHORT).show();

                } else {
                    setLoginButton();
                }
            }
        };
    }

    private Button loginOutButton;
    private void createLoginOutButton() {

         loginOutButton = (Button) findViewById(R.id.login_button);
        
        if (mAuth == null) {

            setLoginButton();

        } else {
            if (mAuth.getCurrentUser() == null) {

                setLoginButton();

            } else {

                setLogOutButton();

            }
        }



    }

    private void setLoginButton() {
        loginOutButton.setText(R.string.login);
        loginOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginSelectDialog.newInstance(MapActivity.this,
                        new OnGoogleSignInClickListener(),
                        new OnFBLoinClickListener(),
                        new EmailLoginClickListener())
                        .show(getFragmentManager(), "Login_dialog");
            }
        });
    }

    private void setLogOutButton() {

        String name = "";
        try {
            name = mAuth.getCurrentUser().getDisplayName();

            if (name == null) {
                name = mAuth.getCurrentUser().getEmail();
            }

        } catch (NullPointerException e) {

        }
        String logOutText = getString(R.string.logged_in_as, name);
        loginOutButton.setText(logOutText);

        loginOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutFromFirebaseAuth();
                navDrawer.closeDrawer(Gravity.LEFT);

            }
        });
    }

    private void signOutFromFirebaseAuth() {

        new AlertDialog.Builder(this).setTitle(R.string.log_out_text)
                .setMessage(R.string.log_out_message)
                .setPositiveButton(R.string.log_out_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        setLoginButton();
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
        } else if (requestCode == FB_LOG_IN_RC) {

            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Email Login

    private String mEmail;
    private String mPassword;

    private static final String EMAIL_LOG_IN_TAG = "email_login_tag";
    class EmailLoginClickListener {

        void onClick(String email, String password) {
//            Toast.makeText(MapActivity.this, "Email Sign in Clicked", Toast.LENGTH_SHORT).show();
            navDrawer.closeDrawer(Gravity.LEFT);

            mEmail = email;
            mPassword = password;

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(MapActivity.this, MapActivity.this);

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
                    mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(MapActivity.this, MapActivity.this);
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

    private static final int GOOGLE_SIGN_IN_RC = 716;
    class OnGoogleSignInClickListener {

        void onClick() {
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
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(FIREBASE_TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(FIREBASE_TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MapActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        } else {
                            setLogOutButton();
                        }

                        // ...
                    }
                });
    }


    // Facebook Login

    private static final int FB_LOG_IN_RC = 64206;

    class OnFBLoinClickListener {

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
        mAuth.signInWithCredential(credential)
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

                        } else {
                            setLogOutButton();
                        }

                        // ...
                    }
                });
    }



}

