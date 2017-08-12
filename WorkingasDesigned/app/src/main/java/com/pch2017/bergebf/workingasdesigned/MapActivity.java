package com.pch2017.bergebf.workingasdesigned;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GeoJsonLayer layerArterial;
    private DrawerLayout mDrawerLayout;
    private String[] mDrawerTitles;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean[] layerToggles = new boolean[4];
    private GeoJsonLayer[] layers = new GeoJsonLayer[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDrawerTitles = getResources().getStringArray(R.array.drawer_options);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerTitles));

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
//                getActionBar().setTitle(mTitle);
//                getActionBar().setTitle("Test");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

                for(int i = 0; i < 4; i++) {
                    if(!(layers[i] == null)) {
                        if (layerToggles[i]) {
                            layers[i].addLayerToMap();
                        } else {
                            layers[i].removeLayerFromMap();
                        }
                    }
                }
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                getActionBar().setTitle("Test");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Set the adapter for the list view
//        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        for(int i = 0; i < 4; i++) {
            layerToggles[i] = false;
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position == 0) {
                selectItem(position);
            }
//            selectItem(position);



        }
    }

    private void selectItem(int position) {
        layerToggles[position] = !layerToggles[position];
    }


    /**mDrawerTitle
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Move Camera to Peoria
        LatLng peoria = new LatLng(40.692751, -89.594682);
//        mMap.addMarker(new MarkerOptions().position(peoria).title("Peoria"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(peoria, 11));
        try {
            layers[0] = new GeoJsonLayer(mMap, R.raw.arterialrecon, getApplicationContext());
            layerArterial = new GeoJsonLayer(mMap, R.raw.arterialrecon, getApplicationContext());
        } catch (Exception e) {

        }

        //layerArterial.addLayerToMap();

    }
}
