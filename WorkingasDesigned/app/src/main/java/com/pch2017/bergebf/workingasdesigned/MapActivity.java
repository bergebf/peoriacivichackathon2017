package com.pch2017.bergebf.workingasdesigned;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.geojson.GeoJsonLayer;


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
                invalidateOptionsMenu();

                //iterate through each layer and show/hide as necessary
                //Doign this when the Drawer closes, because it is easy
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
                invalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //Initially hide all layers
        for(int i = 0; i < 5; i++) {
            layerToggles[i] = false;
        }
    }

    //Event listener for when Drawer items are selected
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        layerToggles[position] = !layerToggles[position];
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Move Camera to Peoria and set the zoom
        LatLng peoria = new LatLng(40.692751, -89.594682);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(peoria, 11));
        try {
            //Import each type of information into its own layer
            layers[0] = new GeoJsonLayer(mMap, R.raw.arterialrecon2, getApplicationContext());
            layers[1] = new GeoJsonLayer(mMap, R.raw.pavement2017, getApplicationContext());
            layers[2] = new GeoJsonLayer(mMap, R.raw.sidewalkconstruction, getApplicationContext());
            layers[3] = new GeoJsonLayer(mMap, R.raw.residential, getApplicationContext());
            layers[4] = new GeoJsonLayer(mMap, R.raw.pendingpermits, getApplicationContext());

            //For simplicity, only the Arterial Construction layer will provide details.
            //TODO: make this work for the other data types.  Data types should have a more
            //      generalized structure.
            layers[0].setOnFeatureClickListener(new GeoJsonLayer.OnFeatureClickListener() {
                @Override
                public void onFeatureClick(Feature feature) {
                    TextView mtext;
                    Button mButton;

                    //Create a dialog box to rpesent teh user with additional information
                    final Dialog dialog = new Dialog(MapActivity.this);
                    dialog.setContentView(R.layout.details_dialog);
                    dialog.setTitle("Project Details");

                    //Fill in the text fields with the information from the GeoJSON data
                    mtext = (TextView) dialog.findViewById(R.id.textView5);
                    mtext.setText(feature.getProperty("ProjStreet"));
                    mtext = (TextView) dialog.findViewById(R.id.textView7);
                    mtext.setText(feature.getProperty("EstStart"));
                    mtext = (TextView) dialog.findViewById(R.id.textView9);
                    mtext.setText(feature.getProperty("EstEnd"));
                    mtext = (TextView) dialog.findViewById(R.id.textView11);
                    mtext.setText("Ongoing");
                    mtext = (TextView) dialog.findViewById(R.id.textView13);
                    mtext.setText("Chuck's Construction");

                    //Add a click handler to allow teh user to close the dialog
                    mButton = (Button) dialog.findViewById(R.id.button4);
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
            //This SHOULD actually do something.  Meh.
        }
    }
}
