package com.example.suraksha;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class policehome extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    TextView tv1, tv2;
    Button b1, b2;
    LatLng lng,lng1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private GoogleMap mMap;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policehome);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        b1 = findViewById(R.id.bt1);
        b2 = findViewById(R.id.bt2);
        SharedPreferences sp1 = getSharedPreferences("mysp1", MODE_PRIVATE);
        final String stationname = sp1.getString("station_name", "NA");
        tv1.setText(stationname);
        ActivityCompat.requestPermissions(policehome.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        db.collection(stationname)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference ref = db.collection(stationname).document(document.getId());
                                ref.delete();
                            }
                        } else {
                            Toast.makeText(policehome.this, "Errorrr", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    Toast.makeText(policehome.this, "Please wait..", Toast.LENGTH_SHORT).show();

                    return;
                }
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 3, policehome.this);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("alert").orderBy("time", Query.Direction.ASCENDING)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    String name="";
                                    String pno="", time="";
                                    double lati=0.0, longi=0.0;

                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        name = document.getData().get("username").toString();
                                        pno = document.getData().get("userpno").toString();
                                        time = document.getData().get("time").toString();
                                        lati = Double.parseDouble(document.getData().get("lat").toString());
                                        longi = Double.parseDouble(document.getData().get("long").toString());

                                    }

                                    lng1=new LatLng(lati,longi);
                                    CameraUpdate cu1 = CameraUpdateFactory.newLatLngZoom(lng1, 15);
                                    mMap.animateCamera(cu1);
                                    MarkerOptions mo1 = new MarkerOptions();
                                    mo1.position(lng1);
                                    try {
                                        mo1.title(name+", "+pno+", "+time);
                                        mMap.addMarker(mo1);
                                    } catch (Exception e) {
                                        Toast.makeText(policehome.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    Toast.makeText(policehome.this, "Women is being traced", Toast.LENGTH_SHORT).show();
                                    tv2.setText("Women in danger: "+name);
                                } else {
                                    Toast.makeText(policehome.this, "Error in alert ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
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

        // Add a marker in Sydney and move the camera
        LatLng bareilly = new LatLng(28.3670, 79.4304);
        mMap.addMarker(new MarkerOptions().position(bareilly).title("Marker in Bareilly"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bareilly));
    }

    @Override
    public void onLocationChanged(Location location) {
        lng = new LatLng(location.getLatitude(), location.getLongitude());
        SharedPreferences sp1 = getSharedPreferences("mysp1", MODE_PRIVATE);
        final String stationname = sp1.getString("station_name", "NA");

// Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("lat", lng.latitude);
        user.put("long", lng.longitude);


// Add a new document with a generated ID
        db.collection(stationname)
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(policehome.this, "Location Updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(policehome.this, "Error in getting station location", Toast.LENGTH_SHORT).show();
                    }
                });
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(lng, 15);
        mMap.animateCamera(cu);
        MarkerOptions mo = new MarkerOptions();
        mo.position(lng);
        mMap.clear();
        try {

            mo.title(stationname);
            mMap.addMarker(mo);
        } catch (Exception e) {
            Toast.makeText(policehome.this, "Error in marker" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
