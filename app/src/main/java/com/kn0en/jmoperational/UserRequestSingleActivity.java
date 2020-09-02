package com.kn0en.jmoperational;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserRequestSingleActivity extends AppCompatActivity {

    private String riderRequestId;

    private TextView userLocation,userLocationLat,userLocationLng, userName,
            userPhone, userRuas, userDetail, userCarNumber;

    private CircleImageView userImage;

    private DatabaseReference userRequestInfoDb;

    private RecyclerView mOfficerAvailableRecyclerView;
    private RecyclerView.Adapter mOfficerAvailableAdapter;
    private RecyclerView.LayoutManager mOfficerAvailableLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_request_single);

        riderRequestId = getIntent().getExtras().getString("riderRequestId");

        userLocation = (TextView) findViewById(R.id.rideLocation);
        userLocationLat = (TextView) findViewById(R.id.rideLocationLat);
        userLocationLng = (TextView) findViewById(R.id.rideLocationLng);
        userName = (TextView) findViewById(R.id.userName);
        userPhone = (TextView) findViewById(R.id.userPhone);
        userCarNumber = (TextView) findViewById(R.id.userCarNumber);
        userRuas = (TextView) findViewById(R.id.userRuas);
        userDetail = (TextView) findViewById(R.id.detail_user);

        userImage = (CircleImageView) findViewById(R.id.userImage);

        userRequestInfoDb = FirebaseDatabase.getInstance().getReference().child("riderRequest").child(riderRequestId);
        getUserInformation("Riders",riderRequestId);

        mOfficerAvailableRecyclerView = (RecyclerView) findViewById(R.id.officerAvailableRecyclerView);
        mOfficerAvailableRecyclerView.setNestedScrollingEnabled(false);
        mOfficerAvailableRecyclerView.setHasFixedSize(true);
        mOfficerAvailableLayoutManager = new LinearLayoutManager(UserRequestSingleActivity.this);
        mOfficerAvailableRecyclerView.setLayoutManager(mOfficerAvailableLayoutManager);
        mOfficerAvailableAdapter = new OfficerAvailAdapter(getDataSetOfficerAvailable(), UserRequestSingleActivity.this);
        mOfficerAvailableRecyclerView.setAdapter(mOfficerAvailableAdapter);

        getofficerAvailableIds();
    }

    String mUserImage, mUserName, mUserPhone, mLocationPickup,
            mPickupLocationLat,mPickupLocationLng,
            mRuas, mCarNumber;
    private void getUserInformation(String otherOfficerOrRider, String otherUserId) {
        DatabaseReference mOtherUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(otherOfficerOrRider).child(otherUserId);
        mOtherUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Map<String, Object> map = (Map<String,Object>) snapshot.getValue();
                    if (map.get("name") != null){
                        mUserName = map.get("name").toString();
                        userName.setText("Name : " + mUserName);
                    }
                    if (map.get("phone") != null){
                        mUserPhone = map.get("phone").toString();
                        userPhone.setText("Phone : " + mUserPhone);
                    }
                    if (map.get("locationPickup") != null){
                        mLocationPickup = map.get("locationPickup").toString();
                        userLocation.setText("Pickup Location : " + mLocationPickup);
                    }
                    if (map.get("pickupLocationLat") != null){
                        mPickupLocationLat = map.get("pickupLocationLat").toString();
                        userLocationLat.setText("Pickup Location Lat : " + mPickupLocationLat);

                        pickupLocationLat = Double.valueOf(mPickupLocationLat);
                    }
                    if (map.get("pickupLocationLng") != null){
                        mPickupLocationLng = map.get("pickupLocationLng").toString();
                        userLocationLng.setText("Pickup Location Lng : " + mPickupLocationLng);

                        pickupLocationLng = Double.valueOf(mPickupLocationLng);
                    }
                    if (map.get("carNumber") != null){
                        mCarNumber = map.get("carNumber").toString();
                        userCarNumber.setText("Car License : " + mCarNumber);
                    }
                    if (map.get("ruas") != null){
                        mRuas = map.get("ruas").toString();
                        userRuas.setText("Ruas Tol : " + mRuas);
                    }
                    if (map.get("profileImageUrl") != null){
                        mUserImage = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mUserImage).into(userImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getofficerAvailableIds() {
        DatabaseReference officersAvailableDatabase = FirebaseDatabase.getInstance().getReference().child("officersAvailable");
        officersAvailableDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot officersAvailable : snapshot.getChildren()){
                            FetchOfficerInformation(officersAvailable.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    String mOfficerName,mOfficerRuas,officerAvailId, mRideDistance;

    LatLng pickupLocationLatLng,officerLatLng;
    float distance,totaldistance;
    String distanceString;
    Double officerLocationLat,officerLocationLng,pickupLocationLat,pickupLocationLng;
    private void FetchOfficerInformation(String officersAvailableKey) {
        DatabaseReference officersAvailableDB = FirebaseDatabase.getInstance().getReference().child("Users").child("Officers").child(officersAvailableKey);
        officersAvailableDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0){
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    String nameOfficer = map.get("name").toString();
                    mOfficerName = "Name : " + nameOfficer;

                    String ruasOfficer =  map.get("ruas").toString();
                    mOfficerRuas = "Ruas Tol : " + ruasOfficer;

                    officerAvailId = snapshot.getKey();

                    officerLocationLat = Double.valueOf(map.get("officerLat").toString());
                    officerLocationLng = Double.valueOf(map.get("officerLng").toString());

                    officerLatLng = new LatLng(officerLocationLat, officerLocationLng);
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

                    mRideDistance = "Distance : " + (distanceString.substring(0,Math.min(distanceString.length(),5)) + " Km");

                    OfficerAvailObject obj = new OfficerAvailObject(officerAvailId,mOfficerName,mOfficerRuas, mRideDistance);
                    resultOfficerAvailable.add(obj);
                    mOfficerAvailableAdapter.notifyDataSetChanged();
                    getAssignedRider(officersAvailableKey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private ArrayList resultOfficerAvailable = new ArrayList<OfficerAvailObject>();
    private ArrayList<OfficerAvailObject> getDataSetOfficerAvailable() { return resultOfficerAvailable; }


    private void getAssignedRider(String officersAvailableKey1) {
            DatabaseReference assignedRiderRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Officers").child(officersAvailableKey1).child("riderRequest");
            assignedRiderRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                        riderRequestId = snapshot.getKey();

                    } else {
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        HashMap map = new HashMap();
        map.put("riderRideId",riderRequestId);
        map.put("riderImage",mUserImage);
        map.put("nameRider",mUserName);
        map.put("phoneRider",mUserPhone);
        map.put("carNumberRider",mCarNumber);
        map.put("ruasRider",mRuas);
        map.put("locationPickup",mLocationPickup);
        map.put("location/to/pickupLocationLat",mPickupLocationLat);
        map.put("location/to/pickupLocationLng",mPickupLocationLng);
        map.put("timestamp",getCurrentTimestamp());
        assignedRiderRef.updateChildren(map);

    }

    private Long getCurrentTimestamp() {
        Long timestamp = System.currentTimeMillis() / 1000;
        return timestamp;
    }
}