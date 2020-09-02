package com.kn0en.jmoperational;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kn0en.jmoperational.officerAvailableRecyclerView.OfficerAvailAdapter;
import com.kn0en.jmoperational.officerAvailableRecyclerView.OfficerAvailObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class OfficerAvailableSingleActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private String officerAvailableId;

    private TextView rideLocation, rideDate, rideRuas, rideDistance;

    private TextView userName,userPhone,userCarNumber;

    private TextView officerName,officerPhone,officerCarNumber;

    private CircleImageView userImageRider,userImageOfficer;

    private ImageView mCallUser,mSendUser,mCallOfficer,mSendOfficer;

    private LatLng pickupLocationLatLng,officerLatLng;

    private DatabaseReference detailRideInfoDb;

    private MaterialButton btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_avail_single);

        polylines = new ArrayList<>();
        officerAvailableId = getIntent().getExtras().getString("officerAvailableId");

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        rideLocation = (TextView) findViewById(R.id.rideLocation);
        rideRuas = (TextView) findViewById(R.id.rideRuas);
        rideDate = (TextView) findViewById(R.id.rideDate);
        rideDistance = (TextView) findViewById(R.id.rideDistance);

        //User
        userImageRider = (CircleImageView) findViewById(R.id.userImage);
        userName = (TextView) findViewById(R.id.userName);
        userPhone = (TextView) findViewById(R.id.userPhone);
        userCarNumber = (TextView) findViewById(R.id.userCarNumber);

        mCallUser = (ImageView) findViewById(R.id.bt_call_user);
        mSendUser = (ImageView) findViewById(R.id.bt_msg_user);

        //Officer
        userImageOfficer = (CircleImageView) findViewById(R.id.officerImage);
        officerName = (TextView) findViewById(R.id.officerName);
        officerPhone = (TextView) findViewById(R.id.officerPhone);
        officerCarNumber = (TextView) findViewById(R.id.officerCarNumber);

        mCallOfficer = (ImageView) findViewById(R.id.bt_call_officer);
        mSendOfficer = (ImageView) findViewById(R.id.bt_msg_officer);

        //btnDone
        btnConfirm = (MaterialButton) findViewById(R.id.btnConfirm);

        detailRideInfoDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Officers").child(officerAvailableId);

        getRideInformation();

        btnConfirm.setOnClickListener(view -> {
            Intent intentDashboard = new Intent(OfficerAvailableSingleActivity.this, MainActivity.class);
            startActivity(intentDashboard);
            rideDone();
        });
    }

    float distance,totaldistance;
    String distanceString,locationPickup,ruasTol,datePickup;
    Double officerLocationLat,officerLocationLng,pickupLocationLat,pickupLocationLng;
    String rideRiderId,imageRider,nameRider,phoneRider,carNumberRider;
    String nameOfficer,imageOfficer,phoneOfficer,carNumberOfficer;
    private void getRideInformation() {
        detailRideInfoDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "IntentReset"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot child : snapshot.getChildren()){

                        if (child.getKey().equals("riderRequest")){
                            datePickup = child.child("timestamp").getValue().toString();
                            rideDate.setText(getDate(Long.valueOf(datePickup)));
                        }

                        if (child.getKey().equals("riderRequest")){
                            locationPickup = child.child("locationPickup").getValue().toString();
                            rideLocation.setText("Pickup Location : " + locationPickup);
                        }

                        if (child.getKey().equals("riderRequest")){
                            ruasTol = child.child("ruasRider").getValue().toString();
                            rideRuas.setText("Ruas Tol : " + ruasTol);
                        }

                        if (child.getKey().equals("riderRequest")){
                            officerLocationLat = Double.valueOf(child.child("location").child("from").child("officerLocationLat").getValue().toString());
                            officerLocationLng = Double.valueOf(child.child("location").child("from").child("officerLocationLng").getValue().toString());
                            officerLatLng = new LatLng(officerLocationLat, officerLocationLng);

                            pickupLocationLat = Double.valueOf(child.child("location").child("to").child("pickupLocationLat").getValue().toString());
                            pickupLocationLng = Double.valueOf(child.child("location").child("to").child("pickupLocationLng").getValue().toString());
                            pickupLocationLatLng = new LatLng(pickupLocationLat, pickupLocationLng);

                            Location loc1 = new Location("");
                            loc1.setLatitude(officerLocationLat);
                            loc1.setLongitude(officerLocationLng);

                            Location loc2 = new Location("");
                            loc2.setLatitude(pickupLocationLat);
                            loc2.setLongitude(pickupLocationLng);

                            distance = loc1.distanceTo(loc2);

                            totaldistance = distance/1000;

                            distanceString = String.valueOf(totaldistance);

                            rideDistance.setText("Distance : " + distanceString.substring(0,Math.min(distanceString.length(),5)) + " Km");

                            if(!pickupLocationLatLng.equals(new LatLng(0, 0))){
                                getRouteToMarker();
                            }
                        }

                        //Info User
                        if (child.getKey().equals("riderRequest")){
                            rideRiderId = child.child("riderRideId").getValue().toString();
                        }

                        if (child.getKey().equals("riderRequest")){
                            imageRider = child.child("riderImage").getValue().toString();
                            Glide.with(getApplication()).load(imageRider).into(userImageRider);
                        }

                        if (child.getKey().equals("riderRequest")){
                            nameRider = child.child("nameRider").getValue().toString();
                            userName.setText("Name : " + nameRider);
                        }

                        if (child.getKey().equals("riderRequest")){
                            phoneRider = child.child("phoneRider").getValue().toString();
                            userPhone.setText("Phone : " + phoneRider);

                            mCallUser.setOnClickListener(view -> {
                                Intent callUserIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneRider, null));
                                startActivity(callUserIntent);
                            });

                            mSendUser.setOnClickListener(view -> {
                                String msg = "Berikut adalah informasi petugas penderekan mobil : " +
                                        "\n" +
                                        "\n" +
                                        "Name : " + nameOfficer +
                                        "\n" +
                                        "Phone : " + phoneOfficer +
                                        "\n" +
                                        "Car plate : " + carNumberOfficer +
                                        "\n" +
                                        "\n" +
                                        "Untuk informasi lebih lanjut, petugas kami akan segera menghubungi anda.";
                                Intent sendUserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://wa.me/"+phoneRider+"?text="+msg)));
                                startActivity(Intent.createChooser(sendUserIntent,null));

                            });
                        }

                        if (child.getKey().equals("riderRequest")){
                            carNumberRider = child.child("carNumberRider").getValue().toString();
                            userCarNumber.setText("Car Plate : " + carNumberRider);
                        }

                        //Info Officer
                        if (child.getKey().equals("name")){
                            nameOfficer = child.getValue().toString();
                            officerName.setText("Name : " + nameOfficer);
                        }

                        if (child.getKey().equals("profileImageUrl")){
                            imageOfficer = child.getValue().toString();
                            Glide.with(getApplication()).load(imageOfficer).into(userImageOfficer);
                        }

                        if (child.getKey().equals("phone")){
                            phoneOfficer = child.getValue().toString();
                            officerPhone.setText("Phone : " + phoneOfficer);

                            mCallOfficer.setOnClickListener(view -> {
                                Intent callOfficerIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneOfficer, null));
                                startActivity(callOfficerIntent);
                            });

                            mSendOfficer.setOnClickListener(view -> {
                                String msg = "Berikut adalah informasi pengguna beserta lokasi penjemputan : " +
                                        "\n" +
                                        "\n" +
                                        "Name : " + nameRider +
                                        "\n" +
                                        "Phone : " + phoneRider +
                                        "\n" +
                                        "Car plate : " + carNumberRider +
                                        "\n" +
                                        "Lokasi : " + "http://maps.google.com/maps/@" + officerLocationLat + "," + officerLocationLng;
                                Intent sendUserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://wa.me/"+phoneOfficer+"?text="+msg)));
                                startActivity(Intent.createChooser(sendUserIntent,null));

                            });

                        }

                        if (child.getKey().equals("carNumber")){
                            carNumberOfficer = child.getValue().toString();
                            officerCarNumber.setText("Car Plate : " + carNumberOfficer);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getRouteToMarker() {
        String apiKey = getString(R.string.api_key);
        Routing routing = new Routing.Builder()
                .key(apiKey)
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(officerLatLng,pickupLocationLatLng)
                .build();
        routing.execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.quantum_googblue};
    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(officerLatLng);
        builder.include(pickupLocationLatLng);

        LatLngBounds bounds = builder.build();

        int padding = (int) (100);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,padding);

        mMap.moveCamera(cameraUpdate);

        mMap.addMarker(new MarkerOptions().position(pickupLocationLatLng).title("Pickup Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
        mMap.addMarker(new MarkerOptions().position(officerLatLng).title("Officer").icon(BitmapDescriptorFactory.fromResource(R.drawable.tow)));

        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

//            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingCancelled() {

    }

    private String getDate(Long timestamp) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(timestamp*1000);
        String date = DateFormat.format("dd-MM-yyyy hh:mm",cal).toString();

        return  date;
    }

    private void rideDone(){
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference operatorRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Operators").child(userId).child("history");
        DatabaseReference officerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Officers").child(officerAvailableId).child("history");
        DatabaseReference riderRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").child(rideRiderId).child("history");
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");

        String requestId = historyRef.push().getKey();

        assert requestId != null;
        operatorRef.child(requestId).setValue(true);
        officerRef.child(requestId).setValue(true);
        riderRef.child(requestId).setValue(true);

        HashMap map = new HashMap();
        map.put("operator", userId);
        map.put("officer", officerAvailableId);
        map.put("rider", rideRiderId);
        map.put("timestamp", datePickup);
        map.put("locationPickup", locationPickup);
        map.put("location/from/lat", officerLocationLat);
        map.put("location/from/lng", officerLocationLng);
        map.put("location/to/lat", pickupLocationLat);
        map.put("location/to/lng", pickupLocationLng);
        map.put("distance", distanceString.substring(0,Math.min(distanceString.length(),5)));
        map.put("ruas", ruasTol);
        map.put("riderImage", imageRider);
        map.put("officerImage", imageOfficer);
        map.put("nameRider", nameRider);
        map.put("nameOfficer", nameOfficer);
        map.put("phoneRider", phoneRider);
        map.put("phoneOfficer", phoneOfficer);
        map.put("carNumberRider", carNumberRider);
        map.put("carNumberOfficer", carNumberOfficer);
        historyRef.child(requestId).updateChildren(map);
    }
}