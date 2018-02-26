package io.techgig.predix.ktc;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;


import com.pb.geomap.ApiClient;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.File;



public class LocalShopActivity extends AppCompatActivity {
    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_shop);

        String title = getIntent().getStringExtra("TITLE");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_local);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Data for hospitals and local vendors is not available know." +
                " It will be updated in near future")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        try {

            mMapView = (MapView) findViewById(R.id.map_view);


            Configuration.getInstance().load(this.getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()));

            mMapView.getOverlays().clear();
            mMapView.getTileProvider().clearTileCache();
            mMapView.getOverlayManager().clear();


            IMapController mapController = mMapView.getController();
            mapController.setZoom(6);
            GeoPoint startPoint = new GeoPoint(12.9716,77.5946);
            mapController.setCenter(startPoint);

            mMapView.setBuiltInZoomControls(true);
            mMapView.setMultiTouchControls(true);

            //Create ApiClient object for GeoMap
            ApiClient mApiClient = new ApiClient(this);
            //Set the Theme of the Map
            mApiClient.setTheme("bronze");
            //Set the Api Key of the GeoMap
            mApiClient.setApi_key("cXmxAkTwvZlBx9eMPp1SXzfrKHbEikcS");
            // Add the GeoMap Tile as the Tile Source to OSMDroid MapView.
            mApiClient.addGeoMapTileSource(mMapView);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            File file = new File(Environment.getExternalStorageDirectory(), "osmdroid");
            if (file.exists()) {
                delete(file);
            }
        }catch(Exception ex){
            ex.printStackTrace(
            );
        }
    }

    private boolean delete(File file) {
        try {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null)
                    for (File f : files) delete(f);
            }
            return file.delete();
        }catch (Exception ex){

        }
        return  true;
    }

    /**
     * This method is used to get the meta data value stored in the manifest files for the provided key
     *
     * @param key
     * @return
     */
    public String getMetaDataFromManifest(String key) {
        String value = "";
        try {
            ApplicationInfo ai = LocalShopActivity.this.getPackageManager().getApplicationInfo(
                    LocalShopActivity.this.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            value = bundle.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

}
