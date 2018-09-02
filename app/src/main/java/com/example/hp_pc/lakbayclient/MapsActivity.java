package com.example.hp_pc.lakbayclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{



    private static final String TAG = "MapsActivity";

//    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
//    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
//    private static final float DEFAULT_ZOOM = 15.5f;
//    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
//            new LatLng(-40, -168), new LatLng(71, 136));
//
//    //widgets
//    private AutoCompleteTextView nsearchtext;
//    private ImageView ngps;
//
//    //vars
//    private Boolean mLocationPermissionGranted = false;
//    private FusedLocationProviderClient mFusedProviderClient;
//    private PlaceAutocompleteAdapter nPlaceAutocompleteAdapter;
//
//    private GoogleApiClient nGoogleApiClient;
//    private PlaceInfo nPlace;
//    private LatLng pickupLocation;
//
//    Button nrequest;
//    ImageView single, family, barkada;
//    TextView cap;
    private GoogleMap nmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

//        nrequest = findViewById(R.id.request);
//
//        cap = findViewById(R.id.capacity);
//
//        single = findViewById(R.id.single);
//        family = findViewById(R.id.family);
//        barkada = findViewById(R.id.barkada);
//
//        Bitmap abitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.lakbay_single)).getBitmap();
//        Bitmap aimagerounded = Bitmap.createBitmap(abitmap.getWidth(), abitmap.getHeight(), abitmap.getConfig());
//        Canvas acanvas = new Canvas(aimagerounded);
//        Paint apaint = new Paint();
//        apaint.setAntiAlias(true);
//        apaint.setShader(new BitmapShader(abitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
//        acanvas.drawRoundRect(new RectF(0, 0, abitmap.getWidth(), abitmap.getHeight()), 1000, 1000, apaint);
//        single.setImageBitmap(aimagerounded);
//
//        Bitmap bbitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.lakbay_family)).getBitmap();
//        Bitmap bimagerounded = Bitmap.createBitmap(bbitmap.getWidth(), bbitmap.getHeight(), bbitmap.getConfig());
//        Canvas bcanvas = new Canvas(bimagerounded);
//        Paint bpaint = new Paint();
//        bpaint.setAntiAlias(true);
//        bpaint.setShader(new BitmapShader(bbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
//        bcanvas.drawRoundRect(new RectF(0, 0, bbitmap.getWidth(), bbitmap.getHeight()), 1000, 1000, bpaint);
//        family.setImageBitmap(bimagerounded);
//
//        Bitmap cbitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.lakbay_barkada)).getBitmap();
//        Bitmap cimagerounded = Bitmap.createBitmap(cbitmap.getWidth(), cbitmap.getHeight(), cbitmap.getConfig());
//        Canvas ccanvas = new Canvas(cimagerounded);
//        Paint cpaint = new Paint();
//        cpaint.setAntiAlias(true);
//        cpaint.setShader(new BitmapShader(cbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
//        ccanvas.drawRoundRect(new RectF(0, 0, cbitmap.getWidth(), cbitmap.getHeight()), 1000, 1000, cpaint);
//        barkada.setImageBitmap(cimagerounded);
//
//
//        single.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View button) {
//                //Set the button's appearance
//                button.setSelected(!button.isSelected());
//                if (button.isSelected()) {
//                    //Handle selected state change
//                    family.setSelected(!button.isSelected());
//                    barkada.setSelected(!button.isSelected());
//                    cap.setText("1-4");
//                } else {
//                    //Handle de-select state change
//                }
//            }
//        });
//
//        family.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View button) {
//                //Set the button's appearance
//                button.setSelected(!button.isSelected());
//                if (button.isSelected()) {
//                    //Handle selected state change
//                    single.setSelected(!button.isSelected());
//                    barkada.setSelected(!button.isSelected());
//                    cap.setText("1-6");
//                } else {
//                    //Handle de-select state change
//                }
//            }
//
//        });
//
//        barkada.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View button) {
//                //Set the button's appearance
//                button.setSelected(!button.isSelected());
//                if (button.isSelected()) {
//                    //Handle selected state change
//                    single.setSelected(!button.isSelected());
//                    family.setSelected(!button.isSelected());
//                    cap.setText("1-12");
//                } else {
//                    //Handle de-select state change
//                }
//            }
//        });

//        nrequest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                DatabaseReference userdata = FirebaseDatabase.getInstance().getReference("drivers_available");
//
//                GeoFire geoFire = new GeoFire(userdata);
//                geoFire.setLocation(userId, new GeoLocation(.getLatitude(), currentLocation.getLongitude()));
//
//
//            }
//        });

//        nsearchtext = findViewById(R.id.input_search);
//        ngps = findViewById(R.id.ic_gps);

//        getLocationPermission();
//        getDeviceLocation();

    }





//    private void init(){
//        Log.d(TAG, "init: initializing");
//
//        nGoogleApiClient = new GoogleApiClient
//                .Builder(this)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .enableAutoManage(this, this)
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
//        nPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this,
//                nGoogleApiClient, LAT_LNG_BOUNDS, autocompleteFilter);
//
//        nsearchtext.setAdapter(nPlaceAutocompleteAdapter);
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
//        ngps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: clicked gps icon");
//                getDeviceLocation();
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
//        Geocoder geocoder = new Geocoder(MapsActivity.this);
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
//
//            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
//
//                    address.getAddressLine(0));
//
//        }
//    }
//
//    private void getDeviceLocation() {
//        Log.d(TAG, "getDeviceLocation: Getting the Device Current Location");
//
//        mFusedProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
//
//        try {
//            if (mLocationPermissionGranted) {
//
//                final Task location = mFusedProviderClient.getLastLocation();
//
//                location.addOnCompleteListener(new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            Log.d(TAG, "onComplete: found location!");
//                            final Location currentLocation = (Location) task.getResult();
//
//
//                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
//                                    DEFAULT_ZOOM,
//                                    "My Location");
//
//                            nrequest.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//
//                                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                                    DatabaseReference userdata = FirebaseDatabase.getInstance().getReference("client_request");
//
//                                    GeoFire geoFire = new GeoFire(userdata);
//                                    geoFire.setLocation(userId, new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude()));
//
//                                    pickupLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//
//
//                                    nrequest.setText("Getting your Driver...");
//
//                                    getClosestDriver();
//
//                                }
//                            });
//
//                        } else {
//                            Log.d(TAG, "onComplete: Current Location is Null");
//                            Toast.makeText(MapsActivity.this, "Unable to get Current Location", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        } catch (SecurityException e) {
//            Log.d(TAG, "getDeviceLocation: Security Exception" + e.getMessage());
//        }
//    }
//
//    private int radius = 1;
//    private Boolean driverFound = false;
//    private String driverFoundID;
//    private void getClosestDriver(){
//        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("drivers_available");
//
//        GeoFire geoFire = new GeoFire(driverLocation);
//
//        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
//        geoQuery.removeAllListeners();
//
//        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
//            @Override
//            public void onKeyEntered(String key, GeoLocation location) {
//                if (!driverFound){
//                    driverFound = true;
//                    driverFoundID = key;
//                }
//
//            }
//
//            @Override
//            public void onKeyExited(String key) {
//
//            }
//
//            @Override
//            public void onKeyMoved(String key, GeoLocation location) {
//
//            }
//
//            @Override
//            public void onGeoQueryReady() {
//                if (!driverFound){
//                    radius++;
//                    Toast.makeText(MapsActivity.this, "radius" + radius , Toast.LENGTH_SHORT).show();
//                    if (radius == 5){
//                        Toast.makeText(MapsActivity.this, "No Driver Available", Toast.LENGTH_SHORT).show();
//                    }else {
//                        getClosestDriver();
//                    }
//                }else {
//                    Toast.makeText(MapsActivity.this, "Driver Found", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onGeoQueryError(DatabaseError error) {
//
//            }
//        });
//    }

//    private void moveCamera(LatLng latLng, float zoom, String title) {
//        Log.d(TAG, "moveCamera: Moving the Camera to Lat" + latLng.latitude + ", lng" + latLng.longitude);
//        nmap.setMinZoomPreference(5.5f);
//        nmap.setMaxZoomPreference(18.0f);
//        nmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
//        nmap.clear();
//        if (!title.equals("My Location")){
//            MarkerOptions options = new MarkerOptions()
//                    .position(latLng)
//                    .title(title)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_end));
//
//            nmap.addMarker(options);
//        }
//        hideSoftKeyboard();
//    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(MapsActivity.this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is Ready");
        nmap = googleMap;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        nAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }





}


