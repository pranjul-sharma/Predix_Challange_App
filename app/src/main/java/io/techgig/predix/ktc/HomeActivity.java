package io.techgig.predix.ktc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import pb.ApiClient;
import pb.ApiException;
import pb.Configuration;
import pb.locationintelligence.LIAPIGeocodeServiceApi;
import pb.locationintelligence.model.GeocodeServiceResponse;

public class HomeActivity extends AppCompatActivity implements HomeRecyclerAdapter.RecyclerOnClickListener{

    RecyclerView recyclerView;

    Toolbar toolbar;
    ProgressBar progressBar;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_home);
        progressBar = (ProgressBar) findViewById(R.id.progress_circle_home);
        progressBar.getIndeterminateDrawable().setColorFilter(R.color.colorPrimaryDark2, PorterDuff.Mode.MULTIPLY);
        toolbar.setTitle("Know this City");
        toolbar.setTitleTextColor(Color.WHITE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        HomeRecyclerAdapter adapter = new HomeRecyclerAdapter(this,this);

        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onItemClick(int position) {
        if (NetworkCheck.isInternetAvailable(this)){
            int i = 1;
            String title=null;
            progressBar.setVisibility(View.VISIBLE);
            switch (position){
                case 0:
                  i=0;
                case 1:
                    if (i == 0)
                        title = "Locations";
                    else
                        title = "Hotels";

                    new NetworkTask.GeoLocationTask(this).execute(title +" Near You.");
                    break;
                case 2:
                    i = 2;
                case 3:
                    if ( i == 2 )
                        title = "Hospitals";
                    else
                        title = "Local Shops";
                    Intent intent = new Intent(this,LocalShopActivity.class);
                    intent.putExtra("TITLE",title+" Near You.");
                    startActivity(intent);
                    break;
            }
        } else {
            Snackbar.make(getWindow().getDecorView(),"Internet not available. Please try again later",Snackbar.LENGTH_LONG).show();
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.INVISIBLE);
    }
}
