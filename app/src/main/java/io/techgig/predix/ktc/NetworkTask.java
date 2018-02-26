package io.techgig.predix.ktc;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.List;

import pb.ApiClient;
import pb.ApiException;
import pb.Configuration;
import pb.JSON;
import pb.locationintelligence.LIAPIGeoZoneServiceApi;
import pb.locationintelligence.LIAPIGeocodeServiceApi;
import pb.locationintelligence.model.Boundary;
import pb.locationintelligence.model.Candidate;
import pb.locationintelligence.model.GeoPos;
import pb.locationintelligence.model.GeocodeServiceResponse;
import pb.locationintelligence.model.Geometry;

/**
 * Created by pranjul on 25/2/18.
 */

public class NetworkTask {

    public static class GeoZoneTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            ApiClient defaultClient = Configuration.getDefaultApiClient();
            defaultClient.setoAuthApiKey("cXmxAkTwvZlBx9eMPp1SXzfrKHbEikcS");
            defaultClient.setoAuthSecret("W0XAU5NjmjEArpXC");

            final LIAPIGeoZoneServiceApi api = new LIAPIGeoZoneServiceApi();

            String latitude=params[0];
            String longitude=params[1];
            String distance ="10";
            String distanceUnit="meters";
            String resolution="4";
            String responseSrs="epsg:4326";
            String srsName=null;


            String resp = null;

            try {
                Log.i("GeoRisk","getBasicBoundaryByLocation");
                resp = api.getBasicBoundaryByLocation( latitude,  longitude,  distance,  distanceUnit,  resolution,  responseSrs,  srsName);
            } catch (ApiException e) {
                e.printStackTrace();
            }



            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you*/
            Log.i("Result",result);

        }
    }


    public static class GeoLocationTask extends AsyncTask<String,Void,List<Candidate>> {

        private WeakReference<AppCompatActivity> activityWeakReference;

        GeoLocationTask(AppCompatActivity reference) {
            activityWeakReference = new WeakReference<>(reference);
        }

        private String TAG;

        @Override
        protected List<Candidate> doInBackground(String... strings) {
            ApiClient defaultClient = Configuration.getDefaultApiClient();
            defaultClient.setoAuthApiKey("cXmxAkTwvZlBx9eMPp1SXzfrKHbEikcS");
            defaultClient.setoAuthSecret("W0XAU5NjmjEArpXC");

            TAG = strings[0];
            final LIAPIGeocodeServiceApi api = new LIAPIGeocodeServiceApi();

            String datapackBundle = "premium";
            String country = "INDIA";
            String placeName = null;
            String mainAddress = "Qutub minar, Delhi";
            String lastLine = null;
            String areaName1 = null;
            String areaName2 = null;
            String areaName3 = null;
            String areaName4 = null;
            Integer postalCode = null;
            String matchMode = null;
            Boolean fallbackGeo = null;
            Boolean fallbackPostal = null;
            Integer maxCands = null;
            Integer streetOffset = null;
            String streetOffsetUnits = null;
            Integer cornerOffset = null;
            String cornerOffsetUnits = null;

            GeocodeServiceResponse resp = null;
            String[] latlog = new String[2];
            try {
                Log.i("GeoCode", "geocode");
                List<Candidate> list = api.geocode(datapackBundle, country, placeName, mainAddress, lastLine, areaName1, areaName2, areaName3, areaName4, postalCode, matchMode, fallbackGeo, fallbackPostal, maxCands, streetOffset, streetOffsetUnits, cornerOffset, cornerOffsetUnits).getCandidates();
//                GeoPos geometry = list.get(0).getGeometry();
//                List<Double> coordinates = geometry.getCoordinates();
//
//                latlog[0] = String.valueOf(coordinates.get(0));
//                latlog[1] = String.valueOf(coordinates.get(1));
                return list;
            } catch (ApiException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Candidate> result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you*/
            AppCompatActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) return;

            if (result.isEmpty())
                Snackbar.make(activity.getWindow().getDecorView(), "Something went wrong.", Snackbar.LENGTH_LONG).show();
            else {
                if (TAG.contains("Location") || TAG.contains("Hotel")) {

                        GeoPos pos = result.get(0).getGeometry();
                        List<Double> coor = pos.getCoordinates();

                    Intent intent = new Intent(activity, MapActivity.class);
                    intent.putExtra("LONGITUDE", String.valueOf(coor.get(0)));
                    intent.putExtra("LATITUDE",String.valueOf(coor.get(1)));
                    intent.putExtra("TITLE", TAG);
                    activity.startActivity(intent);
                }

            }
        }
    }
    public static class ReverseGeoLocationTask extends AsyncTask<String,Void,GeocodeServiceResponse>{

        @Override
        protected GeocodeServiceResponse doInBackground(String... params) {
            ApiClient defaultClient = Configuration.getDefaultApiClient();
            defaultClient.setoAuthApiKey("cXmxAkTwvZlBx9eMPp1SXzfrKHbEikcS");
            defaultClient.setoAuthSecret("W0XAU5NjmjEArpXC");

            final LIAPIGeocodeServiceApi api = new LIAPIGeocodeServiceApi();

            String datapackBundle = "basic";
            BigDecimal x = BigDecimal.valueOf(Double.parseDouble(params[1]));
            BigDecimal y = BigDecimal.valueOf(Double.parseDouble(params[0]));
            String country = "INDIA";
            String coordSysName = null;
            Integer distance = null;
            String distanceUnits = null;

            GeocodeServiceResponse resp = null;

            try {
                Log.i("GeoCode","reverseGeocode");
                resp = api.reverseGeocode(datapackBundle, x, y, country, coordSysName, distance, distanceUnits);
            } catch (ApiException e) {
                e.printStackTrace();
            }
            return resp;



        }

        @Override
        protected void onPostExecute(GeocodeServiceResponse result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you*/

        }

    }

}
