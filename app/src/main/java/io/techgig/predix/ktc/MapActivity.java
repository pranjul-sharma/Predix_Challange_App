package io.techgig.predix.ktc;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import pb.locationintelligence.model.Candidate;
import pb.locationintelligence.model.GeocodeAddress;
import pb.locationintelligence.model.GeocodeServiceResponse;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ProgressBar pb_map;
    Double lat,longitude;
    Toolbar toolbar;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        pb_map = (ProgressBar) findViewById(R.id.pb_map);

        Intent intent = getIntent();
        lat = Double.valueOf( intent.getStringExtra("LATITUDE"));
        longitude = Double.valueOf(intent.getStringExtra("LONGITUDE"));
        String title = intent.getStringExtra("TITLE");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setCancelable(false)
                .setMessage("These map locations are for illustration purpose only. So these are getting data from server" +
                        " for same place again and again. Also data is not categorized to extract exact places and hotels." +
                        "\n" + "Thank you.");

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        toolbar = (Toolbar) findViewById(R.id.toolbar_map);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("StaticFieldLeak")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(lat,longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Qutub Minar"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,15.0f));

        new NetworkTask.GeoZoneTask(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (pb_map.getVisibility() == View.INVISIBLE)
                    pb_map.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                StringBuilder latitudes=new StringBuilder();
                StringBuilder longitudes=new StringBuilder();
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject geometry = object.getJSONObject("geometry");
                    JSONArray coordinates = geometry.getJSONArray("coordinates");
                    for(int i=0;i<coordinates.length();i++){
                        JSONArray arr= coordinates.getJSONArray(i);
                        for(int j=0;j<arr.length();j++){
                            JSONArray array = arr.getJSONArray(j);
                            latitudes.append(array.get(1)).append(":");
                            longitudes.append(array.get(0)).append(":");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final String temp1 = latitudes.toString();
                String temp2 = longitudes.toString();
                final String[] tempArr1 = temp1.split(":");
                final String[] tempArr2 = temp2.split(":");
                for (int i=0;i<tempArr1.length-1;i++){
                    final int finalI = i;
                    new NetworkTask.ReverseGeoLocationTask(){
                        @Override
                        protected void onPostExecute(GeocodeServiceResponse result) {
                            super.onPostExecute(result);
                            List<Candidate> candidates = result.getCandidates();
                            GeocodeAddress address = candidates.get(0).getAddress();
                            StringBuilder addressStr = new StringBuilder();
                            addressStr.append(address.getAreaName4()).append(", ")
                                    .append(address.getAreaName3()).append(", ")
                                    .append(address.getAreaName2());
                            Log.v("ADDRESS","I am getting called with "+address.toString());
                            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(tempArr2[finalI]),Double.valueOf(tempArr1[finalI]))).title(addressStr.toString()));
                            Log.v("ADDRESS","I am getting called with "+address.toString());
                        }
                    }.execute(tempArr2[i],tempArr1[i]);
                }
                if (pb_map.getVisibility() == View.VISIBLE)
                    pb_map.setVisibility(View.INVISIBLE);


            }
        }.execute(String.valueOf(longitude),String.valueOf(lat));

    }
}
