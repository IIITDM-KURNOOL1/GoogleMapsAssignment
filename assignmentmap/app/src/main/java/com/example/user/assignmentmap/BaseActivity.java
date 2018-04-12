package com.example.user.assignmentmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;

import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener , HandleJsonListener,HandleIdListener , ItemClicked{

    Toolbar toolbar;
    RecyclerView restaurant_view;
    RestaurantListAdapter restaurantListAdapter;
    DatabaseReference databaseReference;

    int PROXIMITY_RADIUS = 100000;
    double longitude, latitude;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    public static final int REQUEST_LOCATION_CODE=101;
    public ArrayList<RestaurantDetails> arrayList = new ArrayList<>();

    ImageView ownerRestaurantButton;
    String key="",usertype="",phone="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child("Owner");

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    if(!String.valueOf(snapshot.child("RestaurantDetails").child("Lattitude").getValue()).equals("") &&
                            !String.valueOf(snapshot.child("RestaurantDetails").child("Longitude").getValue()).equals("") )
                    {
                        RestaurantDetails restaurantDetails = new RestaurantDetails();
                        restaurantDetails.setmName(String.valueOf(snapshot.child("RestaurantDetails").child("Name").getValue()));
                        restaurantDetails.setmVicinity(String.valueOf(snapshot.child("RestaurantDetails").child("Address").getValue()));

                        restaurantDetails.setTimings(String.valueOf(snapshot.child("RestaurantDetails").child("Timings").child("Monday").getValue())+"\n"
                                +String.valueOf(snapshot.child("RestaurantDetails").child("Timings").child("Tuesday").getValue())+"\n"
                                +String.valueOf(snapshot.child("RestaurantDetails").child("Timings").child("Wednesday").getValue())+"\n"
                                +String.valueOf(snapshot.child("RestaurantDetails").child("Timings").child("Thursday").getValue())+"\n"
                                +String.valueOf(snapshot.child("RestaurantDetails").child("Timings").child("Friday").getValue())+"\n"
                                +String.valueOf(snapshot.child("RestaurantDetails").child("Timings").child("Saterday").getValue())+"\n"
                                +String.valueOf(snapshot.child("RestaurantDetails").child("Timings").child("Sunday").getValue())+"\n");

                        restaurantDetails.setMenu(String.valueOf(snapshot.child("RestaurantDetails").child("Menu").getValue()));
                        restaurantDetails.setContactInfo(String.valueOf(snapshot.child("RestaurantDetails").child("ContactInfo").getValue()));
                        restaurantDetails.setLattitude(String.valueOf(snapshot.child("RestaurantDetails").child("Lattitude").getValue()));
                        restaurantDetails.setLongitude(String.valueOf(snapshot.child("RestaurantDetails").child("Longitude").getValue()));

                        arrayList.add(restaurantDetails);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        ownerRestaurantButton = findViewById(R.id.restaurant_edit);
        ownerRestaurantButton.setVisibility(View.INVISIBLE);
        restaurant_view = findViewById(R.id.nearestHotelList);


        final Intent intent = getIntent();
        key = intent.getStringExtra("Key");
        usertype = intent.getStringExtra("Usertype");

        if(usertype.equals("Owner"))
        {
            ownerRestaurantButton.setVisibility(View.VISIBLE);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        if (lm != null) {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        buildGoogleApiClient();
        String url = getUrl(latitude,longitude);

        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(BaseActivity.this,url,this);

        getNearbyPlacesData.execute();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        //setting layout manager to recyclerview
        restaurant_view.setLayoutManager(linearLayoutManager);

        restaurantListAdapter = new RestaurantListAdapter(arrayList, BaseActivity.this);

        restaurantListAdapter.notifyDataSetChanged();

        restaurant_view.setAdapter(restaurantListAdapter);

        restaurantListAdapter.setClickListener(BaseActivity.this);

        ownerRestaurantButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent1 = new Intent(BaseActivity.this,OwnerRestaurantDetailsActivity.class);
                intent1.putExtra("Key",key);
                startActivity(intent1);
            }
        });




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    //permission is granted
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                    {
                        if(client==null)
                        {
                            buildGoogleApiClient();
                        }
                    }
                }
                else
                {
                    //permission denied
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                return;
        }
    }
    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    };

    private String getUrl(double latitude, double longitude) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type=" + "restaurant");
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key=" + "AIzaSyDAC-AxMfyIi-18VJUf3ub9KGgv11clbOQ");

        return googlePlaceUrl.toString();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;

        if(client!=null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
    }
    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else
            return true;
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void updateData(String json)
    {
        List<HashMap<String,String>> nearbyPlaceList;
        DataParser parser = new DataParser();
        nearbyPlaceList = parser.parse(json);

        showNearbyPlaces(nearbyPlaceList);
    }
    @Override
    public void updateId(String json)
    {
        try {
            JSONObject parentObject = new JSONObject(json);

            JSONObject object = parentObject.getJSONObject("result");
            phone = object.getString("formatted_phone_number");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showNearbyPlaces(List<HashMap<String,String>> nearbyPlaceList)
    {
        for(int i=0;i<nearbyPlaceList.size();i++)
        {

            HashMap<String,String> googleID = nearbyPlaceList.get(i);

            String placeId = googleID.get("place_id");
            String placevicinity = googleID.get("vicinity");
            String placeName = googleID.get("name");
            String placeTimings = googleID.get("timings");
            String placeMenu = googleID.get("menu");
            String lattitude = googleID.get("lattitude");
            String longitude = googleID.get("longitude");
            String placeUrl = getPlaceUrl(placeId);

            GetNearbyPlaceID getNearbyPlaceID = new GetNearbyPlaceID(BaseActivity.this,placeUrl,this);

            getNearbyPlaceID.execute();

            RestaurantDetails restaurantDetails = new RestaurantDetails();
            restaurantDetails.setmName(placeName);
            restaurantDetails.setmVicinity(placevicinity);
            restaurantDetails.setTimings(placeTimings);
            restaurantDetails.setMenu(placeMenu);
            restaurantDetails.setLattitude(lattitude);
            restaurantDetails.setLongitude(longitude);
            restaurantDetails.setContactInfo(phone);

            arrayList.add(restaurantDetails);
        }

        //Creating object of LinearLayoutManager and setting to recyclerView.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        //setting layout manager to recyclerview
        restaurant_view.setLayoutManager(linearLayoutManager);

        restaurantListAdapter = new RestaurantListAdapter(arrayList, BaseActivity.this);

        restaurantListAdapter.notifyDataSetChanged();

        restaurant_view.setAdapter(restaurantListAdapter);

        restaurantListAdapter.setClickListener(BaseActivity.this);
    }
    private String getPlaceUrl(String placeId)
    {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        googlePlaceUrl.append("placeid=" + placeId);
        googlePlaceUrl.append("&key=" + "AIzaSyDAC-AxMfyIi-18VJUf3ub9KGgv11clbOQ");

        return googlePlaceUrl.toString();
    }

    @Override
    public void onCLick(View view, int position)
    {
        RestaurantDetails restaurantDetails = arrayList.get(position);

        Intent intent = new Intent(BaseActivity.this,MapsActivity.class);
        intent.putExtra("Lattitude",Double.parseDouble(restaurantDetails.getLattitude()));
        intent.putExtra("Longitude",Double.parseDouble(restaurantDetails.getLongitude()));
        intent.putExtra("MyLattitude",latitude);
        intent.putExtra("MyLongitude",longitude);
        Toast.makeText(this, restaurantDetails.getLattitude()+" "+latitude, Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        Intent intent = new Intent(BaseActivity.this,LoginPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }




}