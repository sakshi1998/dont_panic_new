package com.example.sakshi.dont_panic1;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sakshi.dont_panic1.BloodBank.NearestBloodBanks;
import com.example.sakshi.dont_panic1.BloodBank.Update_blood;
import com.example.sakshi.dont_panic1.Hospital.NearestHospital;
import com.example.sakshi.dont_panic1.Hospital.Update_Availability;
import com.example.sakshi.dont_panic1.Pharmacy.NearestPharmacy;
import com.example.sakshi.dont_panic1.Pharmacy.Update_medicine;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.ButterKnife;

import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback {

    public static GoogleMap mMap;

    double longitude, latitude;
    LocationManager locationManager;
    BottomSheetBehavior sheetBehavior;
    Location location;
    CardView bottomsheet;
    Button button;
    public static Button b1;
    DatabaseReference databaseReference;
    GeoFire geoFire;
    String token;
    Context context;
    FloatingActionButton fab,emergency;
    public static ListView centersListView;
    FloatingActionButton hospitals,pharmacy,blood;
    View layoutBottomSheet;
    private CoordinatorLayout coordinatorLayout;
    public static ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        token = FirebaseInstanceId.getInstance().getToken();

        layoutBottomSheet=findViewById(R.id.bottom_sheet);

        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        bottomsheet =(CardView)findViewById(R.id.headphone_card) ;



        hospitals=(FloatingActionButton) findViewById(R.id.fab_add1);
        pharmacy=(FloatingActionButton) findViewById(R.id.fab_add2);
        blood=(FloatingActionButton) findViewById(R.id.fab_add3);


        sheetBehavior.setPeekHeight(60);
        centersListView = findViewById(R.id.hosplist);


        //sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        sheetBehavior.setHideable(false);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        bottomsheet.setVisibility(View.INVISIBLE);
                        emergency.setVisibility(View.INVISIBLE);
                        fab.setVisibility(View.INVISIBLE);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        bottomsheet.setVisibility(View.VISIBLE);
                        emergency.setVisibility(View.VISIBLE);
                        fab.setVisibility(View.VISIBLE);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        hospitalLocations();

    bottomsheet.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            toggleBottomSheet();

            //showBottomSheetDialog();
        }
    });

        hospitals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  loading("Scanning Hospital Location...");

                new NearestHospital(MainActivity.this);

            }
        });

        pharmacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //loading("Scanning pharmacy Location...");

                new NearestPharmacy(MainActivity.this);
            }
        });
        centersListView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow NestedScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow NestedScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });


        blood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //loading("Scanning Location...");

                new NearestBloodBanks(MainActivity.this);
            }
        });


        if (!Utils.hasLocationPermission(this))
            Utils.requestLocationPermission(this, 1002);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);

        mapFragment.getMapAsync(this);

        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).
                getParent()).findViewById(Integer.parseInt("4"));


        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP ,0);

        // Update margins, set to 10dp
        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160,
                getResources().getDisplayMetrics());
        rlp.setMargins(margin, margin, margin, margin);
        /*rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        rlp.setMargins(50, 50, 0, 0);
        */

        findViewById(R.id.zoomInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });


        findViewById(R.id.zoomOutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });


        findViewById(R.id.refreshButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
            }
        });





        fab =  findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        emergency =  findViewById(R.id.emergecy);

        emergency.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,Emergency.class);
                i.putExtra("latitude", latitude);
                i.putExtra("longitude", longitude);
                startActivity(i);

            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

      //  setMarkerBounce(mMap);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mMap.setMyLocationEnabled(true);
        //mMap.setAnimation(google.maps.Animation.BOUNCE);

        updateCamera(null);


    }

   public void Update(View view){
        Intent i=new Intent(MainActivity.this, Update_Availability.class);
        startActivity(i);
   }
    public void Update1(View view){
        startActivity(new Intent(MainActivity.this, Update_medicine.class));
    }
    public void Update2(View view){
        startActivity(new Intent(MainActivity.this, Update_blood.class));
    }
   void loading(String message){
       progressDialog = new ProgressDialog(this);
       progressDialog.setTitle("Please wait");
       progressDialog.setMessage(message);
       progressDialog.setCancelable(true);
       progressDialog.show();
   }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void toggleBottomSheet(){
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        }

        else {

            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

    }


    void setUpCameraAndMarkers(){


        mMap.moveCamera(CameraUpdateFactory.newLatLng( new LatLng(latitude,longitude)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("User"));

        mMap.addCircle(new CircleOptions()
                .center(new LatLng(latitude,longitude))
                .radius(1000)
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
                .strokeWidth(5.0f)

        );



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void updateCamera(@Nullable LatLng position) {


        if (position == null)
            position = new LatLng(28.6315, 77.2167);


        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(position, 8);
        mMap.animateCamera(location, 2000, null);


    }
    void hospitalLocations() {

        if (!Utils.isNetworkAvailable(this)) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

            snackbar.setActionTextColor(Color.RED);
            snackbar.setDuration(LENGTH_INDEFINITE);

            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);

            snackbar.show();
        } else {

            requestLocationUpdates();

            getLocation();




        }
    }

    private void requestLocationUpdates() {

        LocationRequest request = new LocationRequest();

        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        int permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {


            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    location = locationResult.getLastLocation();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    if (location != null) {
                        setUpCameraAndMarkers();
                        Log.d("yyt", "loc update " + location);


                    }
                    else if(location==null){
                    AlertDialog alertDialog = new AlertDialog.Builder(
                            MainActivity.this).create();


                    alertDialog.setTitle("Location needed");

                    // Setting Dialog Message
                    alertDialog.setMessage("Tap to enable location");


                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    enableLocationSettings();
                                }
                            });


                    alertDialog.show();

                } else if (!Utils.isGooglePlayServicesAvailable(MainActivity.this)) {
                    throw new IllegalArgumentException("No Google Play Services Available");


                }
                }
            }, null);
        }
    }
    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }

    void getLocation() {



            databaseReference= FirebaseDatabase.getInstance().getReference();
            geoFire = new GeoFire(databaseReference);

            geoFire.setLocation(token, new GeoLocation(latitude,longitude), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    if (error != null) {
                        System.err.println("There was an error saving the location to GeoFire: " + error);
                    } else {
                        System.out.println("Location saved on server successfully!");

                    }
                }
            });




    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Share");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
