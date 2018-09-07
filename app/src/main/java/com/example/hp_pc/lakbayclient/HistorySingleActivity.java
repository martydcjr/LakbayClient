package com.example.hp_pc.lakbayclient;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistorySingleActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {



    private String rideID, currentUserID, customerID, driverID, userDriverOrCustomer;

    private TextView rideLocation;
    private TextView rideDistance;
    private TextView rideDate;
    private TextView userName;
    private TextView userPhone;

    private ImageView userImage;

    private RatingBar mRatingBar;

    private DatabaseReference historyRideInfoBD;

    private LatLng destinationLatLng, pickupLatLng;


    private GoogleMap map;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_single);
        polylines = new ArrayList<>();

        rideID = getIntent().getExtras().getString("rideID");

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        rideLocation = findViewById(R.id.rideLocation);
        rideDistance = findViewById(R.id.rideDistance);
        rideDate = findViewById(R.id.rideDate);
        userName = findViewById(R.id.userName);
        userPhone = findViewById(R.id.userPhone);

        userImage = findViewById(R.id.userImage);

        mRatingBar = findViewById(R.id.ratingBar);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        historyRideInfoBD = FirebaseDatabase.getInstance().getReference().child("history").child(rideID);
        getRideInformation();
    }

    private void getRideInformation() {
        historyRideInfoBD.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot child:dataSnapshot.getChildren()){
                        if (child.getKey().equals("customer")){
                            customerID =  child.getValue().toString();
                            if (!customerID.equals(currentUserID)){
                                userDriverOrCustomer = "drivers";
                                getUserInformation("clients", customerID);

                            }
                        }
                        if (child.getKey().equals("driver")){
                            driverID =  child.getValue().toString();
                            if (!driverID.equals(currentUserID)){
                                userDriverOrCustomer = "clients";
                                getUserInformation("drivers", driverID);
                                displayCustomerRelatedObject();
                            }
                        }
                        if (child.getKey().equals("timestamp")){
                            rideDate.setText(getDate(Long.valueOf(child.getValue().toString())));
                        }
                        if (child.getKey().equals("rating")){
                            mRatingBar.setRating(Integer.valueOf(child.getValue().toString()));
                        }
                        if (child.getKey().equals("destination")){
                            rideLocation.setText(child.getValue().toString());

                        }
                        if (child.getKey().equals("location")){
                            pickupLatLng = new LatLng(Double.valueOf(child.child("from").child("lat").getValue().toString()),
                                    Double.valueOf(child.child("from").child("lng").getValue().toString()));
                            destinationLatLng = new LatLng(Double.valueOf(child.child("to").child("lat").getValue().toString()),
                                    Double.valueOf(child.child("to").child("lng").getValue().toString()));

                            Location loc1 = new Location("");
                            loc1.setLatitude(pickupLatLng.latitude);
                            loc1.setLongitude(pickupLatLng.longitude);

                            Location loc2 = new Location("");
                            loc2.setLatitude(destinationLatLng.latitude);
                            loc2.setLongitude(destinationLatLng.longitude);

                            float distance = loc1.distanceTo(loc2)/1000;
                            String s = String.format("%.2f", distance);

                            rideDistance.setText(String.valueOf(s + " Km"));

                            if (destinationLatLng != new LatLng(0,0)){
                                getRouteToMarker();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayCustomerRelatedObject() {
        mRatingBar.setVisibility(View.VISIBLE);
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                historyRideInfoBD.child("rating").setValue(rating);
                DatabaseReference mDriverRatingDB = FirebaseDatabase.getInstance().getReference().child("drivers").child(driverID).child("rating");
                mDriverRatingDB.child(rideID).setValue(rating);
            }
        });
    }

    private void getUserInformation(String otherUserDriverOrCustomer, String otherUserID) {
        DatabaseReference mOtherUserID = FirebaseDatabase.getInstance().getReference().child(otherUserDriverOrCustomer).child(otherUserID);
        mOtherUserID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("user_firstname") != null && map.get("user_lastname") != null){
                        String firstName = map.get("user_firstname").toString();
                        String lastName = map.get("user_lastname").toString();
                        userName.setText(firstName + " " + lastName);
                    }
                    if (map.get("user_mobile") != null){
                        userPhone.setText(map.get("user_mobile").toString());
                    }
                    if (map.get("profile_image_url") != null){
                        Glide.with(getApplication()).load(map.get("profile_image_url").toString()).into(userImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private String getDate(Long timestamp) {
        Calendar cal =  Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(timestamp*1000);
        String date = DateFormat.format("dd-MM-yyyy hh:mm", cal).toString();


        return date;
    }

    private void getRouteToMarker() {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(pickupLatLng, destinationLatLng)
                .build();
        routing.execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

    }

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.colorAccent};

    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            FancyToast.makeText(this, "Routing Failed", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }else {
            FancyToast.makeText(this, "Something went wrong, Try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int i) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(pickupLatLng);
        builder.include(destinationLatLng);
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int padding = (int) (width*0.12);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.animateCamera(cameraUpdate);
        map.addMarker(new MarkerOptions().position(pickupLatLng).title("Pick up Location"));
        map.addMarker(new MarkerOptions().position(destinationLatLng).title("Drop off Location"));


        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int e = 0; e <route.size(); e++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(15 + e * 3);
            polyOptions.addAll(route.get(e).getPoints());
            Polyline polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);

            FancyToast.makeText(this,"Route "+ (e+1) +", " +
                            "distance: "+ route.get(e).getDistanceValue()/1000+"Km/s, " +
                            "duration: "+ route.get(e).getDurationValue()/60 + "Min/s",
                    FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();

        }

    }

    @Override
    public void onRoutingCancelled() {

    }

    private void erasePolylines(){
        for (Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }
}
