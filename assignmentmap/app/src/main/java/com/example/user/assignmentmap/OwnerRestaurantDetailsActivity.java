package com.example.user.assignmentmap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OwnerRestaurantDetailsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener
        ,GoogleMap.OnMarkerDragListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mMap;
    private GoogleApiClient client;
    DatabaseReference databaseReference;
    String key="";
    double lattitude=13.067439,longitude=80.237617;
    //bydefault it will show chennai location when owner will add restaurant.

    String lat,lng,name,addr,menu,time1,time2,time3,time4,time5,time6,time7,number;

    Toolbar toolbar;
    EditText restName,restAddress,restMenu,mon,tues,wed,thus,fri,sat,sun,contact;
    TextView saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_restaurant_details);

        toolbar = findViewById(R.id.ownerToolbar);
        restName = findViewById(R.id.owner_hotel_name);
        restAddress = findViewById(R.id.owner_hotel_address);
        restMenu = findViewById(R.id.owner_hotel_menu);
        mon = findViewById(R.id.monday_time);
        tues = findViewById(R.id.tuesday_time);
        wed = findViewById(R.id.wednesday_time);
        thus = findViewById(R.id.thusday_time);
        fri = findViewById(R.id.friday_time);
        sat = findViewById(R.id.saterday_time);
        sun = findViewById(R.id.sunday_time);
        contact = findViewById(R.id.owner_contact_info);
        saveBtn = findViewById(R.id.save_restaurant_details);

        Intent intent = getIntent();
        key = intent.getStringExtra("Key");

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child("Owner");

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    if(snapshot.getKey().equals(key))
                    {
                        lat = String.valueOf(snapshot.child("RestaurantDetails").child("Lattitude").getValue());
                        lng = String.valueOf(snapshot.child("RestaurantDetails").child("Longitude").getValue());

                        if(!lat.equals(""))
                        {
                            lattitude=Double.parseDouble(lat);
                           // Toast.makeText(OwnerRestaurantDetailsActivity.this, "ggg", Toast.LENGTH_SHORT).show();
                        }
                        if(!lng.equals(""))
                        {
                            longitude=Double.parseDouble(lng);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();

        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                saveRestaurantdetails();
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();
    }

    private void saveRestaurantdetails()
    {
        name = restName.getText().toString();
        addr = restAddress.getText().toString();
        menu = restMenu.getText().toString();
        time1 = mon.getText().toString();
        time2 = tues.getText().toString();
        time3 = wed.getText().toString();
        time4 = thus.getText().toString();
        time5 = fri.getText().toString();
        time6 = sat.getText().toString();
        time7 = sun.getText().toString();
        number = contact.getText().toString();

        if(name.equals(""))
        {
            restName.setError("Can not be empty");
            restName.requestFocus();
            return;
        }

        if(addr.equals(""))
        {
            restName.setError("Can not be empty");
            restName.requestFocus();
            return;
        }

        databaseReference.child(key).child("RestaurantDetails").child("Name").setValue(name);
        databaseReference.child(key).child("RestaurantDetails").child("Address").setValue(addr);
        databaseReference.child(key).child("RestaurantDetails").child("Menu").setValue(menu);
        databaseReference.child(key).child("RestaurantDetails").child("Timings").child("Monday").setValue(time1);
        databaseReference.child(key).child("RestaurantDetails").child("Timings").child("Tuesday").setValue(time2);
        databaseReference.child(key).child("RestaurantDetails").child("Timings").child("Wednesday").setValue(time3);
        databaseReference.child(key).child("RestaurantDetails").child("Timings").child("Thursday").setValue(time4);
        databaseReference.child(key).child("RestaurantDetails").child("Timings").child("Friday").setValue(time5);
        databaseReference.child(key).child("RestaurantDetails").child("Timings").child("Saterday").setValue(time6);
        databaseReference.child(key).child("RestaurantDetails").child("Timings").child("Sunday").setValue(time7);
        databaseReference.child(key).child("RestaurantDetails").child("ContactInfo").setValue(number);
        databaseReference.child(key).child("RestaurantDetails").child("Lattitude").setValue(String.valueOf(lattitude));
        databaseReference.child(key).child("RestaurantDetails").child("Longitude").setValue(String.valueOf(longitude));

        Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        mMap.clear();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(lattitude,longitude));
        markerOptions.title("My restaurant");
        markerOptions.draggable(true);

        mMap.addMarker(markerOptions);

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        marker.setDraggable(true);
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker)
    {

    }

    @Override
    public void onMarkerDrag(Marker marker)
    {

    }

    @Override
    public void onMarkerDragEnd(Marker marker)
    {
        lattitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
