package com.example.hp_pc.lakbayclient;


import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PayPalRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.PostalAddress;
import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.hp_pc.lakbayclient.models.PlaceInfo;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, RoutingListener {




    private static final String TAG = "MapsActivity";

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15.5f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136)
    );

    //widgets

    private Fragment searchtext;
    private ImageView ngps;
    //vars
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedProviderClient;
    private PlaceAutocompleteAdapter PlaceAutocompleteAdapter;
    private GoogleApiClient GoogleApiClient;
    private GoogleApiClient pGoogleApiClient;
    private PlaceInfo nPlace;
    private LatLng pickupLocation;
    private LatLng destinationLatLng;
    private LatLng loc;
    private GoogleMap nmap;
    Location LastLocation;
    LocationRequest LocationRequest;
    private Boolean requestBol = false;
    private Marker pickupMarker, destinationMarker;
    public DatabaseReference userdata;
    private float rideDistance, ridePrice;
    private double customerRidePrice;

    BraintreeFragment mBraintreeFragment;
    String mAuthorization;
    HashMap<String, String> paramsHash;


    Button ncancel;
    Button nrequest, toPaypal, btnpboxPaynow, btnpbozReturn;
    ImageView single, family, barkada, ic_payment_method;
    String requestType = "single";
    String total, pmethod;
    TextView cap, cash, price, driverStatus;
    RelativeLayout requestLayout, cancelLayout, paymentBoxLayout;
    String pboxPricex;
    String pboxdriverID;

    private RatingBar mRatingBar;

    private String destination;

    private String userUID;

    private String firstName, lastName;
    TextView name, phone, car, tvCar, tvpboxPrice, tvpboxCartype, tvpboxSuccessID, tvpboxPtype, tvpboxDrname, tvpboxifcash, tvPaythrough;
    ImageView userImage;


    int flag = 0;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);


//        getAssignedDriverInfo();
//
//        if (driverFound == true){
//            updateUIThreads();
//            getAssignedDriverInfo();
//        }
//        else {
//            updateUIThreads();
//            getAssignedDriverInfo();
//        }


//        searchtext = v.findViewById(R.id.place_autocomplete_fragment);
        ngps = v.findViewById(R.id.ic_gps);

        ncancel = v.findViewById(R.id.cancel);
        nrequest = v.findViewById(R.id.request);
        btnpboxPaynow = v.findViewById(R.id.btnpboxPaynow);
        btnpbozReturn = v.findViewById(R.id.btnpboxReturn);

        driverStatus = v.findViewById(R.id.driverStatus);
        cap = v.findViewById(R.id.capacity);
        cash = v.findViewById(R.id.cash);
        price = v.findViewById(R.id.price);

        ic_payment_method = v.findViewById(R.id.ic_payment_method);

        single = v.findViewById(R.id.single);
        family = v.findViewById(R.id.family);
        barkada = v.findViewById(R.id.barkada);
        tvCar = v.findViewById(R.id.tvCar);
        single.setSelected(true);

        name = v.findViewById(R.id.tvName);
        phone = v.findViewById(R.id.tvPhone);
        car = v.findViewById(R.id.tvCar);
        userImage = v.findViewById(R.id.userImage);
        mRatingBar = v.findViewById(R.id.ratingBar);
        destinationLatLng = new LatLng(0.0, 0.0);
        tvpboxPrice = v.findViewById(R.id.tvpboxPrice);
        tvpboxCartype = v.findViewById(R.id.tvpboxCartype);
        tvpboxifcash = v.findViewById(R.id.tvpboxifcash);
        tvpboxPtype = v.findViewById(R.id.tvpboxPtype);
        tvpboxDrname = v.findViewById(R.id.tvpboxDrname);
        tvPaythrough = v.findViewById(R.id.tvPaythrough);

        requestLayout = v.findViewById(R.id.switch1);
        cancelLayout = v.findViewById(R.id.switch2);
        paymentBoxLayout = v.findViewById(R.id.PaymentBox);

        tvpboxifcash.setVisibility(View.GONE);


        Bitmap abitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.singlee)).getBitmap();
        Bitmap aimagerounded = Bitmap.createBitmap(abitmap.getWidth(), abitmap.getHeight(), abitmap.getConfig());
        Canvas acanvas = new Canvas(aimagerounded);
        Paint apaint = new Paint();
        apaint.setAntiAlias(true);
        apaint.setShader(new BitmapShader(abitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        acanvas.drawRoundRect(new RectF(0,0,abitmap.getWidth(), abitmap.getHeight()), 1000, 1000, apaint);
        single.setImageBitmap(aimagerounded);

        Bitmap bbitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.family)).getBitmap();
        Bitmap bimagerounded = Bitmap.createBitmap(bbitmap.getWidth(), bbitmap.getHeight(), bbitmap.getConfig());
        Canvas bcanvas = new Canvas(bimagerounded);
        Paint bpaint = new Paint();
        bpaint.setAntiAlias(true);
        bpaint.setShader(new BitmapShader(bbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        bcanvas.drawRoundRect(new RectF(0,0,bbitmap.getWidth(), bbitmap.getHeight()), 1000, 1000, bpaint);
        family.setImageBitmap(bimagerounded);

        Bitmap cbitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.barkada)).getBitmap();
        Bitmap cimagerounded = Bitmap.createBitmap(cbitmap.getWidth(), cbitmap.getHeight(), cbitmap.getConfig());
        Canvas ccanvas = new Canvas(cimagerounded);
        Paint cpaint = new Paint();
        cpaint.setAntiAlias(true);
        cpaint.setShader(new BitmapShader(cbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        ccanvas.drawRoundRect(new RectF(0,0,cbitmap.getWidth(), cbitmap.getHeight()), 1000, 1000, cpaint);
        barkada.setImageBitmap(cimagerounded);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userUID = user.getUid();
        }

        //        single.setSelected(true);

        single.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                //Set the button's appearance
                button.setSelected(!button.isSelected());
                if (button.isSelected()) {
                    //Handle selected state change
                    family.setSelected(!button.isSelected());
                    barkada.setSelected(!button.isSelected());

                    Location loc11 = new Location("");
                    loc11.setLatitude(LastLocation.getLatitude());
                    loc11.setLongitude(LastLocation.getLongitude());

                    Location loc22 = new Location("");
                    loc22.setLatitude(destinationLatLng.latitude);
                    loc22.setLongitude(destinationLatLng.longitude);

                    if((loc22.getLatitude() == 0) && ((loc22.getLatitude() == 0))) {
//                        Toast.makeText(getContext(), "no location po", Toast.LENGTH_SHORT).show();

                        ridePrice = 0;
                        total = String.valueOf(0);
                        cap.setText("1-4");
                        requestType = "single";
                        price.setText("----------------");
                    } else {

//                        float rideDistance = loc11.distanceTo(loc22) / 1000;
//
//                        float basePrice = 70;
//                        float perKm = 10;
//                        ridePrice = ((rideDistance * perKm) + basePrice);
//                        total = String.format("%.2f", ridePrice);
//                        price.setText(String.valueOf(total));
//
                        cap.setText("1-4");
                        requestType = "single";

                        computeFare("singleRide");

//                    setSingle();
                    }

                } else {
                    //Handle de-select state change

//                    ridePrice = 0;
//                    total = String.valueOf(0);
//                    price.setText("Select destination first");

                }
            }
        });

        family.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                //Set the button's appearance
                button.setSelected(!button.isSelected());
                if (button.isSelected()) {
                    //Handle selected state change
                    single.setSelected(!button.isSelected());
                    barkada.setSelected(!button.isSelected());

                    Location loc11 = new Location("");
                    loc11.setLatitude(LastLocation.getLatitude());
                    loc11.setLongitude(LastLocation.getLongitude());

                    Location loc22 = new Location("");
                    loc22.setLatitude(destinationLatLng.latitude);
                    loc22.setLongitude(destinationLatLng.longitude);

                    if((loc22.getLatitude() == 0) && ((loc22.getLatitude() == 0))) {
//                        Toast.makeText(getContext(), "no location po", Toast.LENGTH_SHORT).show();

                        ridePrice = 0;
                        total = String.valueOf(0);
                        price.setText("----------------");
                        cap.setText("1-6");
                        requestType = "family";
                    } else {
                        cap.setText("1-6");
                        requestType = "family";
                        computeFare("familyRide");
                    }

//                    float rideDistance = loc11.distanceTo(loc22)/1000;
//
//                    float basePrice = 110;
//                    float perKm = 15;
//                    ridePrice = ((rideDistance*perKm)+basePrice);
//                    total = String.format("%.2f", ridePrice);
//                    price.setText(String.valueOf(total));
//
//                    cap.setText("1-6");
//                    requestType = "family";
                } else {
                    //Handle de-select state change
                }
            }
        });

        barkada.setOnClickListener(new View.OnClickListener() {

            public void onClick(View button) {
                //Set the button's appearance
                button.setSelected(!button.isSelected());
                if (button.isSelected()) {
                    //Handle selected state change
                    single.setSelected(!button.isSelected());
                    family.setSelected(!button.isSelected());

                    Location loc11 = new Location("");
                    loc11.setLatitude(LastLocation.getLatitude());
                    loc11.setLongitude(LastLocation.getLongitude());

                    Location loc22 = new Location("");
                    loc22.setLatitude(destinationLatLng.latitude);
                    loc22.setLongitude(destinationLatLng.longitude);

                    if((loc22.getLatitude() == 0) && ((loc22.getLatitude() == 0))) {
//                        Toast.makeText(getContext(), "no location po", Toast.LENGTH_SHORT).show();

                        ridePrice = 0;
                        total = String.valueOf(0);
                        price.setText("----------------");
                        cap.setText("1-12");
                        requestType = "family";
                    } else {
                        cap.setText("1-12");
                        requestType = "family";
                        computeFare("barkadaRide");
                    }

//                    float rideDistance = loc11.distanceTo(loc22)/1000;
//
//                    float basePrice = 160;
//                    float perKm = 30;
//                    ridePrice = ((rideDistance*perKm)+basePrice);
//                    total = String.format("%.2f", ridePrice);
//                    price.setText(String.valueOf(total));
//
//                    cap.setText("1-12");
//                    requestType = "barkada";
                } else {
                    //Handle de-select state change
                }
            }

        });


        nrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



//                requestType = "single";

                if (destinationMarker != null){
                    destinationMarker.remove();
                    erasePolylines();
                }

                if (requestType == null){
                    return;
                }
                requestBol = true;
                if(flag == 0 ) {
                    FancyToast.makeText(getContext(), "Please Select Payment Method", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                } else if(flag == 1) {
                    FancyToast.makeText(getContext(), "Cash Payment Selected", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference userdata = FirebaseDatabase.getInstance().getReference("clients_request");
                    GeoFire geoFire = new GeoFire(userdata);
                    geoFire.setLocation(userId, new GeoLocation(LastLocation.getLatitude(), LastLocation.getLongitude()));
                    pickupLocation = new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude());
                    pickupMarker = nmap.addMarker(new MarkerOptions()
                            .position(pickupLocation)
                            .title("Pickup Here")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));
                    driverStatus.setText("Getting your Driver...");
                    getClosestDriver();

                if (requestType == null){
                    return;
                }
                } else if(flag == 2) {
                    FancyToast.makeText(getContext(), "Paypal Payment Selected", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                }


                requestBol = true;

                if(flag == 0 ) {
                    Toast.makeText(getContext(), "please select payment method", Toast.LENGTH_SHORT).show();
                } else if(flag == 1) {
                    Toast.makeText(getContext(), "cash payment selected", Toast.LENGTH_SHORT).show();

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference userdata = FirebaseDatabase.getInstance().getReference("clients_request");
                    GeoFire geoFire = new GeoFire(userdata);
                    geoFire.setLocation(userId, new GeoLocation(LastLocation.getLatitude(), LastLocation.getLongitude()));

                    pickupLocation = new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude());
                    pickupMarker = nmap.addMarker(new MarkerOptions()
                            .position(pickupLocation)
                            .title("Pickup Here")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));

                    driverStatus.setText("Getting your Driver...");

                    getClosestDriver();

                } else if(flag == 2) {
                    Toast.makeText(getContext(), "paypal payment selected", Toast.LENGTH_SHORT).show();
                }


                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference userdata = FirebaseDatabase.getInstance().getReference("clients_request");
                GeoFire geoFire = new GeoFire(userdata);
                geoFire.setLocation(userId, new GeoLocation(LastLocation.getLatitude(), LastLocation.getLongitude()));

                pickupLocation = new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude());
                pickupMarker = nmap.addMarker(new MarkerOptions()
                        .position(pickupLocation)
                        .title("Pickup Here")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));

                driverStatus.setText("Getting your Driver...");

                getClosestDriver();




            }
        });

        ncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        requestLayout.setVisibility(View.VISIBLE);
                        cancelLayout.setVisibility(View.GONE);



                    }
                });

                if (requestBol){
                    endRide();
                }

            }
        });

        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] pmethods  = {"Cash", "Paypal"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Pick payment method")
                        .setItems(pmethods, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(i == 0) {
                                    FancyToast.makeText(getContext(), "Cash Payment Selected", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
//                                    Toast.makeText(getContext(), "Cash", Toast.LENGTH_SHORT).show();
                                    cash.setText("Cash");
                                    pmethod = "Cash";
                                    flag = 1;
                                    ic_payment_method.setImageResource(R.drawable.ic_action_name);
                                } else if(i == 1) {
                                    FancyToast.makeText(getContext(), "Paypal Payment Selected", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
//                                    Toast.makeText(getContext(), "Paypal", Toast.LENGTH_SHORT).show();
                                    cash.setText("Paypal");
                                    pmethod = "Paypal";
                                    flag = 2;
                                    ic_payment_method.setImageResource(R.drawable.paypal_logo);
                                }
                            }
                        });
                builder.create();
                builder.show();
            }
        });

        tvPaythrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] pmethods  = {"Cash", "Paypal"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Pick payment method")
                        .setItems(pmethods, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(i == 0) {
                                    FancyToast.makeText(getContext(), "Cash Payment Selected", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                                    cash.setText("Cash");
                                    pmethod = "Cash";
                                    tvPaythrough.setText("Pay as Cash");
                                    flag = 1;
                                    ic_payment_method.setImageResource(R.drawable.ic_action_name);
                                } else if(i == 1) {
                                    FancyToast.makeText(getContext(), "Paypal Payment Selected", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                                    cash.setText("Paypal");
                                    pmethod = "Paypal";
                                    tvPaythrough.setText("Pay as Paypal");
                                    flag = 2;
                                    ic_payment_method.setImageResource(R.drawable.paypal_logo);
                                }
                            }
                        });
                builder.create();
                builder.show();
            }
        });


        ngps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("PH")
                .build();

        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                destination = place.getName().toString();
                destinationLatLng = place.getLatLng();

                loc = (new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude()));
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(loc);
                builder.include(destinationLatLng);
                LatLngBounds bounds = builder.build();
                int width = getResources().getDisplayMetrics().widthPixels;
                int padding = (int) (width*0.21);

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                nmap.animateCamera(cameraUpdate);
                getRouteToMarker();
                if (destinationMarker != null){
                    destinationMarker.remove();
                    erasePolylines();
                    destinationMarker = nmap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));
                } else {
                    destinationMarker = nmap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));
                }

                //COUNTRY FILTER
//                final AutocompletePrediction item = PlaceAutocompleteAdapter.getItem(pl);
//                final String placeId = item.getPlaceId();
//
//            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
//                    .getPlaceById(GoogleApiClient, placeId);
//                placeResult.setResultCallback(nUpdatePlaceDetailsCallback);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        getLocationPermission();

//        init();

        return v;
    }

//    private ResultCallback<PlaceBuffer> nUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
//        @Override
//        public void onResult(@NonNull PlaceBuffer places) {
//            if (!places.getStatus().isSuccess()){
//                Log.d(TAG, "onResult: Place query  did not complete succesfully" + places.getStatus().toString());
//                places.release();
//                return;
//            }
//            final Place place = places.get(0);
//
//
//            try{
//                nPlace = new PlaceInfo();
//                nPlace.setName(place.getName().toString());
//                nPlace.setAddress(place.getAddress().toString());
//                nPlace.setId(place.getId());
//                nPlace.setRating(place.getRating());
//                nPlace.setPhoneNumber(place.getPhoneNumber().toString());
//                nPlace.setWebsiteuri(place.getWebsiteUri());
//
//                Log.d(TAG, "onResult: Place:" );
//            }catch (NullPointerException e){
//                Log.e(TAG, "onResult: NullPointerException" + e.getMessage());
//            }
//
//            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
//                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, nPlace.getName());
//
//            places.release();
//        }
//    };



/////////////////////////////////////////////////////////AUTO-COMPLETE LISTENER
//
//    private void init(){
//        Log.d(TAG, "init: initializing");
//
//        pGoogleApiClient = new GoogleApiClient
//                .Builder(getActivity())
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .enableAutoManage(getActivity(), this)
//                .build();
//
//        nsearchtext.setOnItemClickListener(nAutocompleteClickListener);
//
//        //COUNTRY FILTER
//        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
//                .setTypeFilter(Place.TYPE_COUNTRY)
//                .setCountry("PH")
//                .build();
//
//        PlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getContext(),
//                pGoogleApiClient, LAT_LNG_BOUNDS, autocompleteFilter);
//
//        nsearchtext.setAdapter(PlaceAutocompleteAdapter);
//
//        nsearchtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                if(actionId == EditorInfo.IME_ACTION_SEARCH
//                        || actionId == EditorInfo.IME_ACTION_DONE
//                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
//                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
//
//                    //execute our method for searching
//
//                    geoLocate();
//                    hideSoftKeyboard();
//                }
//
//                return false;
//            }
//        });


    //        hideSoftKeyboard();
//    }
//
//    private void geoLocate(){
//        Log.d(TAG, "geoLocate: geolocating");
//
//        String searchString = nsearchtext.getText().toString();
//
//        Geocoder geocoder = new Geocoder(getContext());
//        List<Address> list = new ArrayList<>();
//        try{
//            list = geocoder.getFromLocationName(searchString, 1);
//        }catch (IOException e){
//            Log.d(TAG, "geoLocate: IOException: " + e.getMessage());
//        }
//        if (list.size() > 0){
//            Address address = list.get(0);
//
//            Log.d(TAG, "geoLocate: found a location: " + address.toString());
//            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
//            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
//                    address.getAddressLine(0));
//        }
//    }
//
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: Getting the Device Current Location");

        mFusedProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if (mLocationPermissionGranted) {

                Task location = mFusedProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete: found location!");
                            final Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");


                        } else {
                            Log.d(TAG, "onComplete: Current Location is Null");
                            FancyToast.makeText(getContext(), "Unable to get Current Location", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: Security Exception" + e.getMessage());
        }
    }
//
//
//    /////////////////////////////////////////////////////////AUTO-COMPLETE LISTENER

    private int radius = 1;
    private Boolean driverFound = false;
    private String driverFoundID;

    GeoQuery geoQuery;
    private void getClosestDriver(){
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("drivers_available");

        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound && requestBol){
                    DatabaseReference carType = FirebaseDatabase.getInstance().getReference().child("drivers").child(key);
                    carType.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                                Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (driverFound){
                                    return;
                                }
                                if (driverMap.get("car_type").equals(requestType)){
//                                    Object carrrr = driverMap.get("car_type").toString();
//                                    Toast.makeText(getContext(), "cartype db" + carrrr , Toast.LENGTH_SHORT).show();
                                    driverFound = true;
                                    driverFoundID = dataSnapshot.getKey();
                                    pboxdriverID = dataSnapshot.getKey();

                                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("drivers").child(driverFoundID).child("client_request");
                                    String customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    HashMap map = new HashMap();
                                    map.put("customerRideID", customerID);
                                    map.put("destination", destination);
                                    map.put("destinationLat", destinationLatLng.latitude);
                                    map.put("destinationLng", destinationLatLng.longitude);
                                    map.put("price", total);
                                    driverRef.updateChildren(map);

                                    getDriverLocation();
                                    getHasRideEnded();
                                    driverStatus.setText("Looking for Driver");


                                } else{
//                                    Toast.makeText(getContext(), "No Driver Available", Toast.LENGTH_SHORT).show();

                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound){
                    radius++;
//                    Toast.makeText(getContext(), "radius" + radius , Toast.LENGTH_SHORT).show();
                    if (radius == 6 && !driverFound){
                        FancyToast.makeText(getContext(), "No Available Driver", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();

                    }else {
                        getClosestDriver();
                    }
                }else {
//                    Toast.makeText(getContext(), "Driver Found", Toast.LENGTH_SHORT).show();
                    FancyToast.makeText(getContext(), "Driver Found", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    driverStatus.setText("Driver Found");
                    if (driverFoundID != null){
                        updateUIThreads();
                    }

                    getAssignedDriverInfo();

                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    public void updateUIThreads(){
//        final String foundHim = driverFoundID;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                requestLayout.setVisibility(View.GONE);
                cancelLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getAssignedDriverInfo(){
        userdata = FirebaseDatabase.getInstance().getReference().child("drivers").child(driverFoundID);
        userdata.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//                    pBar.setVisibility(View.GONE);
//                    Info.setVisibility(View.VISIBLE);
                    if (map.get("user_firstname") != null && map.get("user_lastname") != null){
                        firstName = map.get("user_firstname").toString();
                        lastName = map.get("user_lastname").toString();
                        name.setText(firstName + " " + lastName);
                    }
                    if (map.get("user_mobile") != null){
                        phone.setText(map.get("user_mobile").toString());
                    }
                    if (map.get("profile_image_url") != null){
                        Glide.with(getActivity().getApplication()).load(map.get("profile_image_url").toString()).into(userImage);
                    }
                    int ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingAvg;
                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    if (ratingsTotal != 0 ){
                        ratingAvg = ratingSum/ratingsTotal;
                        mRatingBar.setRating(ratingAvg);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;
    private void getHasRideEnded(){
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("drivers").child(driverFoundID).child("client_request").child("customerRideID");
        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
//
                } else {
                    endRide();
//                    payTheRide();
//                    Intent intent = new Intent(getContext(), PaypalExpressCheckout.class);
//                    intent.putExtra("driverID", pboxdriverID);
//                    startActivity(intent);

                    payTheRide();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requestLayout.setVisibility(View.GONE);
                            cancelLayout.setVisibility(View.GONE);
                            paymentBoxLayout.setVisibility(View.VISIBLE);
//                            lpbox.setVisibility(View.VISIBLE);

                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void payTheRide() {
        if(pmethod.equalsIgnoreCase("Cash")) {
            btnpboxPaynow.setVisibility(View.GONE);
            tvpboxifcash.setVisibility(View.VISIBLE);
            tvpboxifcash.setText("Cash payment. Waiting for driver confirmation.");
            tvpboxPtype.setText(pmethod);
            setPayPending();
        } else if(pmethod.equalsIgnoreCase("Paypal")) {
            btnpboxPaynow.setVisibility(View.VISIBLE);
            tvpboxifcash.setVisibility(View.VISIBLE);
            tvpboxifcash.setText("Paypal payment. Proceed to paypal UI.");
            tvpboxPtype.setText(pmethod);
            setPayPending();
        }
        getToken();
        DatabaseReference getPendingData = FirebaseDatabase.getInstance().getReference().child("pending").child(pboxdriverID).child("price_to_pay");

        getPendingData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String c_ride_price = dataSnapshot.child("c_ride_price").getValue().toString();

                    tvpboxPrice.setText(c_ride_price);
                    tvpboxCartype.setText("wait");
                    pboxPricex = c_ride_price;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnpboxPaynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
                setupBraintreeAndStartExpressCheckout();
            }
        });

    }
    DatabaseReference getCurrentRide;
    ValueEventListener getCurrentRideListener;
    String ID1;
    private String setRideID;
    private void setPayPending() {
        final String[] thisisRideID = new String[1];
        getCurrentRide = FirebaseDatabase.getInstance().getReference().child("clients").child(userUID);
//        getCurrentRideListener = getCurrentRide.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String keyID = dataSnapshot.child("current_ride_id").getValue(String.class);
//
//                ID1 = keyID;
//                setRideID = dataSnapshot.child("current_ride_id").getValue(String.class);
//
////                Toast.makeText(getContext(), "setRideID: " + setRideID, Toast.LENGTH_SHORT).show();
//
////                Toast.makeText(getContext(), "keyID: " + keyID, Toast.LENGTH_SHORT).show();
//
////                DatabaseReference setPending = FirebaseDatabase.getInstance().getReference().child("clients").child(userUID).child("pay_in_pending").child(keyID);
////
////                HashMap map = new HashMap();
////                map.put("payment_method", pmethod);
////                map.put("payment_status", "unpaid");
////
////                setPending.updateChildren(map);
//
//                turnToPaid(false);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        ValueEventListener setRideListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                setRidenga[0] = (String) dataSnapshot.child("current_ride_id").getValue();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//        getCurrentRide.addValueEventListener(setRideListener);

        getCurrentRide.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String keyID = dataSnapshot.child("current_ride_id").getValue(String.class);

                ID1 = keyID;
                setRideID = dataSnapshot.child("current_ride_id").getValue(String.class);

                turnToPaid(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getToken() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://lakbay-c65e4.firebaseapp.com/client_token", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                FancyToast.makeText(getContext(), "Failure", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String clientToken) {
                mAuthorization = clientToken;

                try {

                    mBraintreeFragment = BraintreeFragment.newInstance(getActivity(), mAuthorization);
                    FancyToast.makeText(getContext(), "BraintreeFragment is ready to use", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    // mBraintreeFragment is ready to use!
                } catch (InvalidArgumentException e) {
                    // There was an issue with your authorization string.
                }
            }
        });
    }

    public void setupBraintreeAndStartExpressCheckout() {
        PayPalRequest request = new PayPalRequest("120")
                .currencyCode("PHP")
                .intent(PayPalRequest.INTENT_AUTHORIZE);
        PayPal.requestOneTimePayment(mBraintreeFragment, request);

        mBraintreeFragment.addListener(new PaymentMethodNonceCreatedListener() {
            @Override
            public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
                String nonce = paymentMethodNonce.getNonce();

                if(paymentMethodNonce instanceof PayPalAccountNonce) {
                    PayPalAccountNonce payPalAccountNonce = (PayPalAccountNonce)paymentMethodNonce;

                    // Access additional information
                    String email = payPalAccountNonce.getEmail();
                    String firstName = payPalAccountNonce.getFirstName();
                    String lastName = payPalAccountNonce.getLastName();
                    String phone = payPalAccountNonce.getPhone();

                    // See PostalAddress.java for details
                    PostalAddress billingAddress = payPalAccountNonce.getBillingAddress();
                    PostalAddress shippingAddress = payPalAccountNonce.getShippingAddress();
                }

                postNonceToServer(nonce);
            }
        });
    }

    public void postNonceToServer(final String nonce) {
        final RequestParams params = new RequestParams();

        paramsHash = new HashMap<>();
        paramsHash.put("amount", pboxPricex);
        paramsHash.put("nonce", nonce);
        RequestParams requestParams = new RequestParams(paramsHash);

        AsyncHttpClient client2 = new AsyncHttpClient();
        client2.post("http://10.0.2.2/braintree-php-3.33.0/checkout.php", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                FancyToast.makeText(getContext(), "Success Post", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();

                btnpboxPaynow.setVisibility(View.GONE);
                btnpbozReturn.setVisibility(View.VISIBLE);

                try {
                    String str = IOUtils.toString(responseBody, "UTF-8");

                    tvpboxifcash.setText("Success. ID: " + str);

                    if(str.contains("Success")) {
                        turnToPaid(true);
                    }
                    DatabaseReference updatePayment = FirebaseDatabase.getInstance().getReference().child("payments").child(userUID);

                    String requestID = updatePayment.push().getKey();
                    updatePayment.child(requestID).setValue(true);

                    HashMap map = new HashMap();
                    map.put("price_payed", pboxPricex);
                    map.put("payment_method", "Paypal Express Checkout");
                    map.put("push_date", ServerValue.TIMESTAMP);

                    updatePayment.child(requestID).updateChildren(map);

//                    turnToPaid();

                    DatabaseReference updatePerClient = FirebaseDatabase.getInstance().getReference().child("clients").child(userUID).child("pay_in_pending").child(setRideID);
                    HashMap map2 = new HashMap();
                    map2.put("payment_status", "paid");

                    updatePerClient.updateChildren(map2);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

        btnpbozReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLayout.setVisibility(View.VISIBLE);
                paymentBoxLayout.setVisibility(View.GONE);
            }
        });
    }

    private void turnToPaid(Boolean x) {
        if(x) {
            DatabaseReference updatePerClient = FirebaseDatabase.getInstance().getReference().child("clients").child(userUID).child("pay_in_pending").child(setRideID);
            HashMap map2 = new HashMap();
            map2.put("payment_status", "paid");

            updatePerClient.updateChildren(map2);
        } else {
            DatabaseReference setPending = FirebaseDatabase.getInstance().getReference().child("clients").child(userUID).child("pay_in_pending").child(setRideID);
//
            HashMap map = new HashMap();
            map.put("payment_method", pmethod);
            map.put("payment_status", "unpaid");

            setPending.updateChildren(map);

            Toast.makeText(getContext(), "Pay First", Toast.LENGTH_SHORT).show();
        }
    }

    private void endRide(){
        requestBol = false;
        geoQuery.removeAllListeners();
        driverLocationRef.removeEventListener(driverLocationRefListener);
        driveHasEndedRef.removeEventListener(driveHasEndedRefListener);

        if (driverFoundID != null){
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("drivers").child(driverFoundID).child("client_request");
            driverRef.removeValue();
            driverFoundID = null;
        }
        driverFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference userdata = FirebaseDatabase.getInstance().getReference("clients_request");
        GeoFire geoFire = new GeoFire(userdata);
        geoFire.removeLocation(userId);

        if (pickupMarker != null){
            pickupMarker.remove();
        }
        if (driverMarker != null){
            driverMarker.remove();
        }
        nrequest.setText("CONFIRM LAKBAY");

//                    Info.setVisibility(View.GONE);
        name.setText("");
        phone.setText("");
        car.setText("");
        userImage.setImageResource(R.mipmap.default_user);


//        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");
//        String requestID = historyRef.getKey();
//        HashMap map = new HashMap();
//        map.put("timestamp", getCurrentTimestamp());
//
//
//        historyRef.child(requestID).updateChildren(map);

//        String userId1 = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history").child(userId1);
//        String requestID = historyRef.push().getKey();
//
//        HashMap map = new HashMap();
//        map.put("payment_method", userId);
////        map.put("price", ridePrice);
//
//
//        historyRef.child(requestID).updateChildren(map);


    }


//    private Long getCurrentTimestamp() {
//        Long timestamp = System.currentTimeMillis()/1000;
//        return timestamp;
//    }

    private Marker driverMarker;
    private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;
    private void getDriverLocation(){
        DatabaseReference ridePrice = FirebaseDatabase.getInstance().getReference().child("pending").child(driverFoundID).child("price_to_pay");
        String customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap map = new HashMap();
        map.put("c_ride_id", customerID);
        map.put("c_ride_price", customerRidePrice);
        map.put("c_payment_status", "unpaid");
        ridePrice.updateChildren(map);

        tvCar.setText(String.valueOf(customerRidePrice));
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("drivers_working").child(driverFoundID).child("l");
        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && requestBol){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    driverStatus.setText("Driver Found");
                    pickupMarker.remove();
                    if (map.get(0) != null){
                        Object latObject = map.get(0);
                        locationLat = Double.parseDouble(latObject.toString());
//                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null){
                        Object lngObject = map.get(1);
                        locationLng = Double.parseDouble(lngObject.toString());
//                        locationLng = Double.parseDouble(map.get(0).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat, locationLng);
                    if(driverMarker != null){
                        driverMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);
//                    distance = distance/1;

                    if (distance < 100){
                        driverStatus.setText("Driver Arrived at Pick up Location");
                    } else {
                        driverStatus.setText("Driver Found " + String.valueOf(distance) + " meters away");
                    }


                    driverMarker = nmap.addMarker(new MarkerOptions()
                            .position(driverLatLng)
                            .title("Your Driver")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // single car method //

    private void selectTo() {
        ridePrice = 0;
        total = String.valueOf(0);
        price.setText("----------------");
    }

    private void computeFare(String rideType) {

        if(rideType.equals("singleRide")) {

            Location loc11 = new Location("");
            loc11.setLatitude(LastLocation.getLatitude());
            loc11.setLongitude(LastLocation.getLongitude());

//            Location from = new Location("");
//            from.setLatitude(pickupLocation.latitude);
//            from.setLongitude(pickupLocation.longitude);

            Location loc22 = new Location("");
            loc22.setLatitude(destinationLatLng.latitude);
            loc22.setLongitude(destinationLatLng.longitude);

            float rideDistance = loc11.distanceTo(loc22) / 1000;

//            float rideDistance = loc11.distanceTo(loc22) / 1000;
            String url = getDirectionsUrl(LastLocation.getLatitude(), LastLocation.getLongitude(), destinationLatLng);
            new GetDisDur().execute(url);

            Toast.makeText(getContext(), "rideDistance: " + rideDistance, Toast.LENGTH_SHORT).show();

            float basePrice = 70;
            float perKm = 10;
//            ridePrice = ((rideDistance * perKm) + basePrice);
            ridePrice = ((dis * perKm) + basePrice);
            float r = Math.round(ridePrice)+2*3;
            total = String.format("%.2f", r);
            customerRidePrice = Double.parseDouble(total);
            price.setText(String.valueOf(total));

            cap.setText("1-4");
            requestType = "single";

        } else if(rideType.equals("barkadaRide")) {
            Location loc11 = new Location("");
            loc11.setLatitude(LastLocation.getLatitude());
            loc11.setLongitude(LastLocation.getLongitude());

            Location loc22 = new Location("");
            loc22.setLatitude(destinationLatLng.latitude);
            loc22.setLongitude(destinationLatLng.longitude);

            float rideDistance = loc11.distanceTo(loc22)/1000;
//
            float basePrice = 160;
            float perKm = 30;
            ridePrice = ((dis*perKm)+basePrice);
            float r = Math.round(ridePrice)+2*3;
            total = String.format("%.2f", r);
            customerRidePrice = Double.parseDouble(total);
            price.setText(String.valueOf(total));

            cap.setText("1-12");
            requestType = "barkada";
        } else if(rideType.equals("familyRide")) {
            Location loc11 = new Location("");
            loc11.setLatitude(LastLocation.getLatitude());
            loc11.setLongitude(LastLocation.getLongitude());

            Location loc22 = new Location("");
            loc22.setLatitude(destinationLatLng.latitude);
            loc22.setLongitude(destinationLatLng.longitude);

            float rideDistance = loc11.distanceTo(loc22)/1000;

            float basePrice = 110;
            float perKm = 15;
            ridePrice = ((dis*perKm)+basePrice);
            float r = Math.round(ridePrice)+2*3;
            total = String.format("%.2f", r);
            customerRidePrice = Double.parseDouble(total);
            price.setText(String.valueOf(total));

            cap.setText("1-6");
            requestType = "family";

        }
    }

    private String getDirectionsUrl(double originlat, double originlong, LatLng dest) {
        String str_origin = "origin=" + originlat + "," + originlong;

        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String sensor = "sensor=false";

        String mode = "mode=driving";

        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        String output = "json";

        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters +"&key=";
    }

    private class GetDisDur extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                JSONArray routes = jsonObject.getJSONArray("routes");

                JSONObject routes1 = routes.getJSONObject(0);

                JSONArray legs = routes1.getJSONArray("legs");

                JSONObject legs1 = legs.getJSONObject(0);

                JSONObject distance = legs1.getJSONObject("distance");

                JSONObject duration = legs1.getJSONObject("duration");

                String distanceText = distance.getString("text");

                rideDistance = Float.parseFloat(String.valueOf(distance));

//                durationText = duration.getString("text");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private String downloadUrl(String s) {
        return s;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        selectTo();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        FancyToast.makeText(getContext(), "Map is ready", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
        Log.d(TAG, "onMapReady: Map is Ready");
        nmap = googleMap;

        if (mLocationPermissionGranted) {
//            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            buildGoogleApiClient();
            nmap.setMyLocationEnabled(true);
            nmap.getUiSettings().setMyLocationButtonEnabled(false);
//            nmap.getUiSettings().setCompassEnabled(true);

//            init();

        }
    }

    protected synchronized void buildGoogleApiClient(){
        GoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        GoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        LastLocation = location;

//        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
//        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()),
                DEFAULT_ZOOM,
                "My Location");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest = new LocationRequest();
        LocationRequest.setInterval(10000);
        LocationRequest.setFastestInterval(10000);
        LocationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getContext(), FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(GoogleApiClient, LocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: Moving the Camera to Lat" + latLng.latitude + ", lng" + latLng.longitude);
//        nmap.setMinZoomPreference(5.5f);
        nmap.setMaxZoomPreference(18.0f);
        nmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
//        map.clear();
//        if (!title.equals("My Location")){
//            MarkerOptions options = new MarkerOptions()
//                    .position(latLng)
//                    .title(title)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_end));
//
//
//            nmap.addMarker(options);
//        }
//        hideSoftKeyboard();
    }

    private void hideSoftKeyboard(){
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void initMap(){
        Log.d(TAG, "initMap: Initializing Map");
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: Getting Location Permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getActivity(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            if (ContextCompat.checkSelfPermission(this.getActivity(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            }else {
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }else {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Called.");
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0){
                    for (int i = 0; i < grantResults.length; i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: Permission Failed!");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: Permission Granted");
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }

    }





     /*
    ---------------------------------google places api autocmplete suggestions
     */

//    private AdapterView.OnItemClickListener nAutocompleteClickListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            hideSoftKeyboard();
//
//            final AutocompletePrediction item = PlaceAutocompleteAdapter.getItem(i);
//            final String placeId = item.getPlaceId();
//
//            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
//                    .getPlaceById(GoogleApiClient, placeId);
//            placeResult.setResultCallback(nUpdatePlaceDetailsCallback);
//        }
//    };
//
//    private ResultCallback<PlaceBuffer> nUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
//        @Override
//        public void onResult(@NonNull PlaceBuffer places) {
//            if (!places.getStatus().isSuccess()){
//                Log.d(TAG, "onResult: Place query  did not complete succesfully" + places.getStatus().toString());
//                places.release();
//                return;
//            }
//            final Place place = places.get(0);
//
//
//                try{
//                    nPlace = new PlaceInfo();
//                    nPlace.setName(place.getName().toString());
//                    nPlace.setAddress(place.getAddress().toString());
//                    nPlace.setId(place.getId());
//                    nPlace.setRating(place.getRating());
//                    nPlace.setPhoneNumber(place.getPhoneNumber().toString());
//                    nPlace.setWebsiteuri(place.getWebsiteUri());
//
//                    Log.d(TAG, "onResult: Place:" );
//                }catch (NullPointerException e){
//                    Log.e(TAG, "onResult: NullPointerException" + e.getMessage());
//                }
//
//            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
//                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, nPlace.getName());
//
//            places.release();
//        }
//    };

    private void getRouteToMarker() {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(loc, destinationLatLng)
                .build();
        routing.execute();
    }



    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.colorAccent};

    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            FancyToast.makeText(getContext(), "Routing Failed", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }else {
            FancyToast.makeText(getContext(), "Something went wrong, Try again", FancyToast.LENGTH_SHORT, FancyToast.CONFUSING, false).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }
    float dis;

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int i) {



//        if(polylines.size()>0) {
//            for (Polyline poly : polylines) {
//                poly.remove();
//            }
//        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int e = 0; e <route.size(); e++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(15 + e * 3);
            polyOptions.addAll(route.get(e).getPoints());
            Polyline polyline = nmap.addPolyline(polyOptions);
            polylines.add(polyline);
            float d = route.get(e).getDistanceValue();
            dis = d/1000;
            FancyToast.makeText(getContext(),"Route "+ (e+1) +", " +
                            "distance: "+  dis +" Km/s, " +
                            "duration: "+ route.get(e).getDurationValue() + "Min/s",
                    FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
            computeFare("singleRide");
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

    @Override
    public void onPause() {
        super.onPause();
//        GoogleApiClient.stopAutoManage(getActivity());
//        GoogleApiClient.disconnect();
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (GoogleApiClient != null && GoogleApiClient.isConnected()) {
//            GoogleApiClient.stopAutoManage(getActivity());
//            GoogleApiClient.disconnect();
//        }
    }



}














