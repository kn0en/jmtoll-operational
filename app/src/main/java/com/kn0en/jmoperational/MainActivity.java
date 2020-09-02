package com.kn0en.jmoperational;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private FirebaseAuth mAuth;
    private String operatorId;
    private DatabaseReference operatorUserHeaderRef;

    private CircleImageView mImageHeader;
    private TextView mNameHeader, mPhoneHeader, mUserDesc;

    private ImageView mUserRequest,mMyProfile,mHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_dashboard);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //operator auth
        mAuth = FirebaseAuth.getInstance();
        operatorId = mAuth.getCurrentUser().getUid();
        operatorUserHeaderRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Operators").child(operatorId);

        //Navigation header item
        View itemView = navigationView.getHeaderView(0);
        mImageHeader = (CircleImageView) itemView.findViewById(R.id.image_header);
        mNameHeader = (TextView) itemView.findViewById(R.id.name_header);
        mPhoneHeader = (TextView) itemView.findViewById(R.id.phone_header);

        mUserDesc = (TextView) findViewById(R.id.user_desc);

        mUserRequest = (ImageView) findViewById(R.id.userRequest);
        mMyProfile = (ImageView) findViewById(R.id.myProfile);
        mHistory = (ImageView) findViewById(R.id.history);

        getOperatorInfoHeader();

        mUserRequest.setOnClickListener(view -> {
            Intent intentUserReq = new Intent(MainActivity.this, UserRequestActivity.class);
            startActivity(intentUserReq);
        });

        mMyProfile.setOnClickListener(view -> {
            Intent intentMyProfile = new Intent(MainActivity.this, MyProfileActivity.class);
            startActivity(intentMyProfile);
        });

        mHistory.setOnClickListener(view -> {
            Intent intentHistory = new Intent(MainActivity.this, HistoryActivity.class);
            intentHistory.putExtra("riderOrOfficerOrOperator", "Operators");
            startActivity(intentHistory);
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                Intent intentDashboard = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intentDashboard);
                navigationView.setCheckedItem(R.id.nav_dashboard);
                break;
            case R.id.nav_user_req:
                Intent intentUserReq = new Intent(MainActivity.this, UserRequestActivity.class);
                startActivity(intentUserReq);
                navigationView.setCheckedItem(R.id.nav_user_req);
                break;
            case R.id.nav_profile:
                Intent intentMyProfile = new Intent(MainActivity.this, MyProfileActivity.class);
                startActivity(intentMyProfile);
                navigationView.setCheckedItem(R.id.nav_profile);
                break;
            case R.id.nav_password:
                Intent intentPassword = new Intent(MainActivity.this, UpdatePasswordActivity.class);
                startActivity(intentPassword);
                navigationView.setCheckedItem(R.id.nav_password);
                break;
            case R.id.nav_history:
                Intent intentHistory = new Intent(MainActivity.this, HistoryActivity.class);
                intentHistory.putExtra("riderOrOfficerOrOperator", "Operators");
                startActivity(intentHistory);
                navigationView.setCheckedItem(R.id.nav_history);
                break;
//            case R.id.nav_tutorial:
//                Intent intentTutorial = new Intent(RiderMapsActivity.this, TutorialSlidePagerActivity.class);
//                intentTutorial.putExtra("riderOrOfficer", "Riders");
//                startActivity(intentTutorial);
//                navigationView.setCheckedItem(R.id.nav_tutorial);
//                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intentSignOut = new Intent(MainActivity.this, SplashScreenActivity.class);
                startActivity(intentSignOut);
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getOperatorInfoHeader() {
        operatorUserHeaderRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("name") != null) {
                        String mName = map.get("name").toString();
                        mNameHeader.setText(mName);
                        mUserDesc.setText("Welcome, " + mName + " !");
                    }
                    if (map.get("phone") != null) {
                        String mPhone = map.get("phone").toString();
                        mPhoneHeader.setText(mPhone);
                    }
                    if (map.get("profileImageUrl") != null) {
                        String mProfileImageHeader = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileImageHeader).into(mImageHeader);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}