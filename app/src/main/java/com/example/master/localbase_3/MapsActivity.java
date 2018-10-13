package com.example.master.localbase_3;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.master.localbase_3.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static final String TAG = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getData();
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
    private void getData() {
        String url = "https://opendata.epa.gov.tw/ws/Data/ATM00625/?$format=json";
        StringRequest stringRequest = new StringRequest(
                url,
                new Response.Listener < String > () {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "response = " + response.toString());
                        parserJson(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "error : " + error.toString());
                    }
                }
        );
        Volley.newRequestQueue(this).add(stringRequest);
    }
    private void parserJson(String data) {
        MarkerOptions mo = new MarkerOptions();
        String dataParsed = "";
        String Country;
        String Site;
        String PM25;

        try {
            JSONArray JA = new JSONArray(data);
            for (int i = 0; i < JA.length(); i++) {
                JSONObject JO = (JSONObject) JA.get(i);
                Country = "Country:" + JO.get("county");
                Site = "Site:" + JO.get("Site");
                PM25 = "PM25:" + JO.get("PM25");

                Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
                List <Address> addressLocation = geoCoder.getFromLocationName(JO.getString("county") + JO.getString("Site"), 1);

                double latitude = addressLocation.get(0).getLatitude();
                double longitude = addressLocation.get(0).getLongitude();

                mMap.addMarker(mo.position(new LatLng(latitude, longitude))
                        .title(Country + Site)
                        .snippet(Country + Site + PM25)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                );
                dataParsed = dataParsed + Country + Site + PM25 + "\n";
                Log.d("json:", dataParsed);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}