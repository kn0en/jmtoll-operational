package com.kn0en.jmoperational;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kn0en.jmoperational.userRequestRecyclerView.UserRequestAdapter;
import com.kn0en.jmoperational.userRequestRecyclerView.UserRequestObject;

import java.util.ArrayList;
import java.util.Map;


public class UserRequestActivity extends AppCompatActivity {

    private RecyclerView mUserRequestRecyclerView;
    private RecyclerView.Adapter mUserRequestAdapter;
    private RecyclerView.LayoutManager mUserRequestLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_request);

        mUserRequestRecyclerView = (RecyclerView) findViewById(R.id.userRequestRecyclerView);
        mUserRequestRecyclerView.setNestedScrollingEnabled(false);
        mUserRequestRecyclerView.setHasFixedSize(true);
        mUserRequestLayoutManager = new LinearLayoutManager(UserRequestActivity.this);
        mUserRequestRecyclerView.setLayoutManager(mUserRequestLayoutManager);
        mUserRequestAdapter = new UserRequestAdapter(getDataSetUserRequest(), UserRequestActivity.this);
        mUserRequestRecyclerView.setAdapter(mUserRequestAdapter);

        getUserRequestIds();

    }

    private void getUserRequestIds() {
        DatabaseReference userRequestDatabase = FirebaseDatabase.getInstance().getReference().child("riderRequest");
        userRequestDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot riderRequest : snapshot.getChildren()){
                        FetchRiderInformation(riderRequest.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void FetchRiderInformation(String riderRequestKey) {
        DatabaseReference riderRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").child(riderRequestKey);
        riderRequestDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0){
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    String mRiderName = "Nama : " + map.get("name").toString();
                    String mRiderRuas = "Ruas Tol : " + map.get("ruas").toString();
                    String riderRequestId = snapshot.getKey();

                    UserRequestObject obj = new UserRequestObject(riderRequestId,mRiderName,mRiderRuas);
                    resultUserRequest.add(obj);
                    mUserRequestAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private ArrayList resultUserRequest = new ArrayList<UserRequestObject>();
    private ArrayList<UserRequestObject> getDataSetUserRequest() { return resultUserRequest; }

}