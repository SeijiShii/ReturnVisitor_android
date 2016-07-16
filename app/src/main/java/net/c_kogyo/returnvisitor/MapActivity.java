package net.c_kogyo.returnvisitor;

import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ZoomControls;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    static final String MAP_DEBUG ="map_debug";

    MapView mMapView;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        createToolBar();
        createDrawer();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

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

        LatLng latLng = new LatLng(20.694882,-101.369367);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        Log.d(MAP_DEBUG, latLng.latitude + ", " + latLng.longitude);


//        LatLng latLng = mMap.getCameraPosition().target;
//        Log.d(MAP_DEBUG, latLng.latitude + ", " + latLng.longitude);
//
//        mMap.moveCamera(CameraUpdateFactory.zoomTo(13f));
//        Log.d(MAP_DEBUG, "Zoom Level = " + mMap.getCameraPosition().zoom);
//
//
//        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//            @Override
//            public void onCameraChange(CameraPosition cameraPosition) {
//                Log.d(MAP_DEBUG, "Camera Position Changed!");
//                Log.d(MAP_DEBUG, "Zoom Level = " + mMap.getCameraPosition().zoom);
//                LatLng latLng = cameraPosition.target;
//                Log.d(MAP_DEBUG, latLng.latitude + ", " + latLng.longitude);

//            }
//        });


    }

    private DrawerLayout navDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private void createDrawer() {

        navDrawer = (DrawerLayout) findViewById(R.id.drawer);
        // toolBarを設定するコンストラクタを使用する必要がある
        mDrawerToggle = new ActionBarDrawerToggle(this, navDrawer, toolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

    }


}
