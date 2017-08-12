package com.pch2017.bergebf.workingasdesigned;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.MultiGeometry;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonMultiPolygon;
import com.google.maps.android.data.geojson.GeoJsonPolygon;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GeoJsonLayer layerArterial;
    private DrawerLayout mDrawerLayout;
    private String[] mDrawerTitles;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean[] layerToggles = new boolean[5];
    private GeoJsonLayer[] layers = new GeoJsonLayer[5];

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

                for(int i = 0; i < 5; i++) {
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

        for(int i = 0; i < 5; i++) {
            layerToggles[i] = false;
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
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
            layers[1] = new GeoJsonLayer(mMap, R.raw.pavement2017, getApplicationContext());
            layers[2] = new GeoJsonLayer(mMap, R.raw.sidewalkconstruction, getApplicationContext());
            layers[3] = new GeoJsonLayer(mMap, R.raw.residential, getApplicationContext());
            layers[4] = new GeoJsonLayer(mMap, R.raw.pendingpermits, getApplicationContext());

            layers[0].setOnFeatureClickListener(new GeoJsonLayer.OnFeatureClickListener() {
                @Override
                public void onFeatureClick(Feature feature) {
                    TextView mtext;
                    Button mButton;
                    final Dialog dialog = new Dialog(MapActivity.this);
                    dialog.setContentView(R.layout.details_dialog);
                    dialog.setTitle("Project Details");
                    mtext = (TextView) dialog.findViewById(R.id.textView5);
                    mtext.setText(feature.getProperty("ProjStreet"));
                    mtext = (TextView) dialog.findViewById(R.id.textView7);
                    mtext.setText(feature.getProperty("EstState"));
                    mtext = (TextView) dialog.findViewById(R.id.textView9);
                    mtext.setText(feature.getProperty("EstEnd"));
                    mtext = (TextView) dialog.findViewById(R.id.textView11);
                    mtext.setText("Ongoing");
                    mtext = (TextView) dialog.findViewById(R.id.textView13);
                    mtext.setText("Chuck's Construction");

                    mButton = (Button) dialog.findViewById(R.id.button4);

                    Log.i("GeoJsonClick", "Feature clicked: " + feature.getProperty("ProjStreet"));

                    mButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });

        } catch (Exception e) {

        }


    }
}
