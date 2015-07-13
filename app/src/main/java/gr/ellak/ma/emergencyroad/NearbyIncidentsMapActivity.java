package gr.ellak.ma.emergencyroad;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.melnykov.fab.FloatingActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by billiout on 11/7/2015.
 */
public class NearbyIncidentsMapActivity extends ActionBarActivity implements GoogleMap.OnMarkerClickListener {
    Activity act = this;
    GoogleMap googleMap;
    FloatingActionButton btn;
    Marker marker;
    LatLng loc;
    Circle mCircle;
    TextView radiusTxt;
    List<Marker> markers;
    List<Incident> list;
    double radius = 1;

    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_nearbymap);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nearby Incidents");

        btn = (FloatingActionButton) findViewById(R.id.btnMyLoc);
        radiusTxt = (TextView) findViewById(R.id.radiusTxt);
        radiusTxt.setText("Radius: " + radius + "km");


        createMapView();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(38.884723, 23.854217), 5.8f));
        googleMap.setOnMarkerClickListener(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.setMyLocationEnabled(true);
            }
        });
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                loc = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(loc);
                markerOptions.title("You are here");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                marker = googleMap.addMarker(markerOptions);
                marker.showInfoWindow();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                googleMap.setMyLocationEnabled(false);
                double radiusInMeters = radius * 1000;
                int strokeColor = 0xffFF99FF; //red outline
                int shadeColor = 0x44FF99FF; //opaque red fill

                CircleOptions circleOptions = new CircleOptions().center(loc).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
                mCircle = googleMap.addCircle(circleOptions);

                new GetNearbyIncidents().execute(radius);

            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.clear();
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
                // TODO Auto-generated method stub
                loc = marker.getPosition();
                googleMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(loc);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                marker = googleMap.addMarker(markerOptions);
                marker.setDraggable(true);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                googleMap.setMyLocationEnabled(false);
                double radiusInMeters = radius * 1000;
                int strokeColor = 0x44FF99FF; //red outline
                int shadeColor = 0x44FF99FF; //opaque red fill

                CircleOptions circleOptions = new CircleOptions().center(loc).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
                mCircle = googleMap.addCircle(circleOptions);

                new GetNearbyIncidents().execute(radius);

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                // TODO Auto-generated method stub

            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                loc = latLng;
                googleMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(loc);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                marker = googleMap.addMarker(markerOptions);
                marker.setDraggable(true);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                googleMap.setMyLocationEnabled(false);
                double radiusInMeters = radius * 1000;
                int strokeColor = 0x44FF99FF; //red outline
                int shadeColor = 0x44FF99FF; //opaque red fill

                CircleOptions circleOptions = new CircleOptions().center(loc).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
                mCircle = googleMap.addCircle(circleOptions);

                new GetNearbyIncidents().execute(radius);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map_nearby, menu);
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

        if (id == R.id.action_radius) {
            final CharSequence[] items = {
                    "500m", "1km", "2km", "5km", "10km"
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose radius:");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if(item == 0){
                        radius = 0.5;
                        if(marker != null) {
                            if (marker.getTitle()!= null)
                                googleMap.setMyLocationEnabled(true);
                            else {
                                googleMap.clear();
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(loc);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                marker = googleMap.addMarker(markerOptions);
                                marker.setDraggable(true);
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                                googleMap.setMyLocationEnabled(false);
                                double radiusInMeters = radius * 1000;
                                int strokeColor = 0x44FF99FF; //red outline
                                int shadeColor = 0x44FF99FF; //opaque red fill

                                CircleOptions circleOptions = new CircleOptions().center(loc).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
                                mCircle = googleMap.addCircle(circleOptions);

                                new GetNearbyIncidents().execute(radius);
                            }
                        }

                        radiusTxt.setText("Radius: " + radius * 1000 + "m");
                    }
                    else if(item == 1){
                        radius = 1;
                        if(marker != null) {
                            if (marker.getTitle()!= null)
                                googleMap.setMyLocationEnabled(true);
                            else {
                                googleMap.clear();
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(loc);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                marker = googleMap.addMarker(markerOptions);
                                marker.setDraggable(true);
                                //marker.showInfoWindow();
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                                googleMap.setMyLocationEnabled(false);
                                double radiusInMeters = radius * 1000;
                                int strokeColor = 0x44FF99FF; //red outline
                                int shadeColor = 0x44FF99FF; //opaque red fill

                                CircleOptions circleOptions = new CircleOptions().center(loc).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
                                mCircle = googleMap.addCircle(circleOptions);

                                new GetNearbyIncidents().execute(radius);
                            }
                        }
                        radiusTxt.setText("Radius: " + radius + "km");
                    }
                    else if(item == 2){
                        radius = 2;
                        if(marker != null) {
                            if (marker.getTitle()!= null)
                                googleMap.setMyLocationEnabled(true);
                            else {
                                googleMap.clear();
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(loc);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                marker = googleMap.addMarker(markerOptions);
                                marker.setDraggable(true);
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                                googleMap.setMyLocationEnabled(false);
                                double radiusInMeters = radius * 1000;
                                int strokeColor = 0x44FF99FF; //red outline
                                int shadeColor = 0x44FF99FF; //opaque red fill

                                CircleOptions circleOptions = new CircleOptions().center(loc).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
                                mCircle = googleMap.addCircle(circleOptions);

                                new GetNearbyIncidents().execute(radius);
                            }
                        }
                        radiusTxt.setText("Radius: " + radius + "km");
                    }
                    else if(item == 3){
                        radius = 5;
                        if(marker != null) {
                            if (marker.getTitle()!= null)
                                googleMap.setMyLocationEnabled(true);
                            else {
                                googleMap.clear();
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(loc);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                marker = googleMap.addMarker(markerOptions);
                                marker.setDraggable(true);
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                                googleMap.setMyLocationEnabled(false);
                                double radiusInMeters = radius * 1000;
                                int strokeColor = 0x44FF99FF; //red outline
                                int shadeColor = 0x44FF99FF; //opaque red fill

                                CircleOptions circleOptions = new CircleOptions().center(loc).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
                                mCircle = googleMap.addCircle(circleOptions);

                                new GetNearbyIncidents().execute(radius);
                            }
                        }
                        //googleMap.setMyLocationEnabled(true);
                        radiusTxt.setText("Radius: " + radius + "km");
                    }
                    else if(item == 4){
                        radius = 10;
                        if(marker != null) {
                            if (marker.getTitle() != null)
                                googleMap.setMyLocationEnabled(true);
                            else {
                                googleMap.clear();
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(loc);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                marker = googleMap.addMarker(markerOptions);
                                marker.setDraggable(true);
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                                googleMap.setMyLocationEnabled(false);
                                double radiusInMeters = radius * 1000;
                                int strokeColor = 0x44FF99FF; //red outline
                                int shadeColor = 0x44FF99FF; //opaque red fill

                                CircleOptions circleOptions = new CircleOptions().center(loc).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
                                mCircle = googleMap.addCircle(circleOptions);

                                new GetNearbyIncidents().execute(radius);
                            }
                        }
                        radiusTxt.setText("Radius: " + radius + "km");
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for(int i = 0; i < markers.size(); i++){
            if (marker.equals(markers.get(i))){
                Intent intent = new Intent(act, IncidentActivity.class);
                intent.putExtra("id", list.get(i).ID);
                intent.putExtra("title", list.get(i).location);
                act.startActivity(intent);
                return true;
            }
        }
        return false;
    }

    class GetNearbyIncidents extends AsyncTask<Double, Void, List<Incident>>{

        @Override
        protected List<Incident> doInBackground(Double... params) {
            List<Incident> list = new ArrayList<>();
            try{
                JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params1 = new ArrayList<>();
                params1.add(new BasicNameValuePair("latitude", String.valueOf(loc.latitude)));
                params1.add(new BasicNameValuePair("longitude", String.valueOf(loc.longitude)));
                params1.add(new BasicNameValuePair("km", String.valueOf(params[0])));
                JSONObject json = jsonParser.makeHttpRequest(act, "http://titansoft.netau.net/get_close_incidents.php", "GET", params1);

                JSONArray addresses = json.getJSONArray("incidents");
                for(int i = 0; i < addresses.length(); i++){
                    Incident incident = new Incident();
                    incident.ID = addresses.getJSONObject(i).getInt("ID");
                    incident.location = addresses.getJSONObject(i).getString("location");
                    incident.lat = addresses.getJSONObject(i).getDouble("latitude");
                    incident.longi = addresses.getJSONObject(i).getDouble("longitude");
                    incident.date = addresses.getJSONObject(i).getString("date");
                    list.add(incident);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }


            return list;
        }

        @Override
        protected void onPostExecute(List<Incident> lists){
            list = lists;
            markers = new ArrayList<>();
            for(Incident incident : lists){
                LatLng loc1 = new LatLng(incident.lat, incident.longi);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(loc1);
                Marker m = googleMap.addMarker(markerOptions);
                markers.add(m);
            }
        }
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
