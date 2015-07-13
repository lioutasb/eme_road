package gr.ellak.ma.emergencyroad;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.MatrixCursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.melnykov.fab.FloatingActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by billiout on 10/7/2015.
 */
public class SubmitMapActivity extends ActionBarActivity {
    Activity act = this;
    GoogleMap googleMap;
    FloatingActionButton btn;
    SimpleCursorAdapter suggestionAdapter;
    Marker marker;
    String addressTexts = "";

    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_mapsubmit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Location");
        btn = (FloatingActionButton) findViewById(R.id.btnMyLoc);
        createMapView();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.setMyLocationEnabled(true);
            }
        });

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(38.884723, 23.854217), 5.8f));

        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(loc);
                //markerOptions.title(getAddress(location.getLatitude(), location.getLongitude()));
                new ReverseGeocodingTask(getBaseContext(), markerOptions).execute(loc);
                if (googleMap != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                }
                googleMap.setMyLocationEnabled(false);
            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                //markerOptions.title(LocationAddress.getAddressFromLocation(latLng.latitude, latLng.latitude, getApplicationContext()));
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                new ReverseGeocodingTask(getBaseContext(), markerOptions).execute(latLng);
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.clear();
                marker = null;
            }
        });

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
                // TODO Auto-generated method stub
                //Here your code
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(marker.getPosition());

                //markerOptions.title(LocationAddress.getAddressFromLocation(latLng.latitude, latLng.latitude, getApplicationContext()));
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                new ReverseGeocodingTask(getBaseContext(), markerOptions).execute(marker.getPosition());
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map_search_result, menu);

        final String[] from = new String[] {"address", "city"};
        final int[] to = new int[] {android.R.id.text1, android.R.id.text2};
        suggestionAdapter = new SimpleCursorAdapter(act,
                android.R.layout.simple_list_item_2,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSuggestionsAdapter(suggestionAdapter);
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                //new SearchAddress().execute(query);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println(query);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        int id = item.getItemId();

        if (id == R.id.action_ok) {
            Intent data = new Intent();
            data.putExtra("is_returned", false);
            if(marker != null && marker.isVisible()) {
                data.putExtra("is_returned", true);
                data.putExtra("long", marker.getPosition().longitude);
                data.putExtra("lat", marker.getPosition().latitude);
                data.putExtra("title", addressTexts);
                setResult(RESULT_OK, data);
            }
            finish();
            return true;
        }
        return false;
    }

    class Address {
        String address;
        String city;
    }

    class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String>{
        Context mContext;
        MarkerOptions markerOptions;

        public ReverseGeocodingTask(Context context, MarkerOptions markerOptions){
            super();
            mContext = context;
            this.markerOptions = markerOptions;
        }

        // Finding address using reverse geocoding
        @Override
        protected String doInBackground(LatLng... params) {
            Geocoder geocoder = new Geocoder(mContext, Locale.ENGLISH);
            double latitude = params[0].latitude;
            double longitude = params[0].longitude;

            List<android.location.Address> addresses = null;
            String addressText="";

            try {
                addresses = geocoder.getFromLocation(latitude, longitude,1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(addresses != null && addresses.size() > 0 ){
                android.location.Address address = addresses.get(0);

                addressText = String.format("%s, %s %s, %s",address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getLocality() != null?address.getLocality():"", address.getPostalCode() != null?address.getPostalCode():"", address.getCountryName());
            }

            return addressText;
        }

        @Override
        protected void onPostExecute(String addressText) {
            markerOptions.title(addressText);
            marker = googleMap.addMarker(markerOptions);
            marker.showInfoWindow();
            marker.setDraggable(true);
            System.out.println(addressText);
            addressTexts = addressText;
        }
    }

    class SearchAddress extends AsyncTask<String, Void, List<Address>>{

        @Override
        protected List<Address> doInBackground(String... params) {
            List<Address> adds = new ArrayList<>();
            try{
                JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params1 = new ArrayList<>();
                params1.add(new BasicNameValuePair("query", URLEncoder.encode(params[0], "UTF-8")));
                params1.add(new BasicNameValuePair("sensor", "true"));
                params1.add(new BasicNameValuePair("key", "AIzaSyAMgDA7BXNDpJpLBXYhkV8gUWsvPLludMg"));
                JSONObject json = jsonParser.makeHttpRequest(act, "https://maps.googleapis.com/maps/api/place/textsearch/json", "GET", params1);

                JSONArray addresses = json.getJSONArray("results");

                for(int i = 0; i < addresses.length(); i++){
                    JSONObject add = addresses.getJSONObject(i);
                    Address address = new Address();
                    address.address = add.getString("name");
                    String[] tmp = add.getString("formatted_address").split(address.address + ",");
                    address.city = tmp[1];
                    adds.add(address);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return adds;
        }

        @Override
        protected  void onPostExecute(List<Address> adds){
            populateAdapter(adds);
        }
    }

    private void populateAdapter(List<Address> adds) {
        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "address", "city" });
        for (int i=0; i<adds.size(); i++) {
                c.addRow(new Object[] {i, adds.get(i).address, adds.get(i).city});
        }
        suggestionAdapter.changeCursor(c);
    }

    private void createMapView(){
        try {
            if(null == googleMap){
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                        R.id.mapView)).getMap();

                googleMap.getUiSettings().setZoomGesturesEnabled(true);

                if(null == googleMap) {
                    Toast.makeText(getApplicationContext(),
                            "Error creating map", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NullPointerException exception){
            Log.e("mapApp", exception.toString());
        }
    }
}
