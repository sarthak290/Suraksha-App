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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class userhome extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    TextView tv;
    Button bt;
    LatLng lng,lng1;
    int i=1;
    private GoogleMap mMap;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userhome);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        tv = findViewById(R.id.tv);
        bt = findViewById(R.id.bt);
        SharedPreferences sp = getSharedPreferences("mysp", MODE_PRIVATE);
        final String uname = sp.getString("username", "NA");
        final String upno = sp.getString("pno", "NA");
        tv.setText(uname);
        ActivityCompat.requestPermissions(userhome.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            Toast.makeText(userhome.this, "Please wait..", Toast.LENGTH_SHORT).show();

            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 10, userhome.this);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("stations")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {



                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                       final String sname=document.getData().get("station_name").toString();
                                       final String pno=document.getData().get("pno").toString();

                                           db.collection(sname)
                                                   .get()
                                                   .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                           if (task.isSuccessful()) {
                                                               double lati = 0.0;
                                                               double longi = 0.0;
                                                               for (QueryDocumentSnapshot document : task.getResult()) {
                                                                   lati = Double.parseDouble(document.getData().get("lat").toString());
                                                                   longi = Double.parseDouble(document.getData().get("long").toString());

                                                               }
                                                               double d = 0.0;
                                                               double km;
                                                               d = distance(lng.latitude, lng.longitude, lati, longi);
                                                               km = d * 1.609;
                                                               if ((km < 1.0 || km == 1.0) && (i<3 ||i==3)) {
                                                                   lng1 = new LatLng(lati, longi);
                                                                   CameraUpdate cu1 = CameraUpdateFactory.newLatLngZoom(lng1, 15);
                                                                   mMap.animateCamera(cu1);
                                                                   MarkerOptions mo1 = new MarkerOptions();
                                                                   mo1.position(lng1);
                                                                   try {
                                                                       mo1.title(sname + " , " + pno + ", " + i);
                                                                       mMap.addMarker(mo1);
                                                                   } catch (Exception e) {
                                                                       Toast.makeText(userhome.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                   }
                                                                   i++;
                                                               }
                                                           } else {
                                                               Toast.makeText(userhome.this, "Error in station name", Toast.LENGTH_SHORT).show();
                                                           }
                                                       }
                                                   });

                                    }
                                } else {
                                    Toast.makeText(userhome.this, "error in stations", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
// Create a new user with a first and last name
                Map<String, Object> user = new HashMap<>();
                user.put("username", uname);
                user.put("userpno", upno);
                user.put("lat", lng.latitude);
                user.put("long", lng.longitude);
                user.put("time", currentTime);


// Add a new document with a generated ID
                db.collection("alert")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(userhome.this, "Nearest police station has been informed", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(userhome.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
    public double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        return (dist);
    }

    public double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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
        Toast.makeText(this, "Location updated", Toast.LENGTH_SHORT).show();
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(lng, 15);
        mMap.animateCamera(cu);
        MarkerOptions mo = new MarkerOptions();
        mo.position(lng);
        try {
            Geocoder gc = new Geocoder(userhome.this);
            List<Address> addresses = gc.getFromLocation(lng.latitude, lng.longitude, 1);
            Address ad = addresses.get(0);
            mo.title(ad.getAddressLine(0));
            mMap.addMarker(mo);
        } catch (Exception e) {
            Toast.makeText(userhome.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
