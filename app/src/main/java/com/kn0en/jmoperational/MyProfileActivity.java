package com.kn0en.jmoperational;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends AppCompatActivity {

    private EditText mNameOperator,mPhoneOperator,mAddressOperator;

    private MaterialButton mConfirm;

    private RadioGroup mGenderGroup;
    private RadioButton genderButton;

    private CircleImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mOperatorDatabase;

    private String userID,mName,mPhone,mAddress,mGender,mProfileImageUrl;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        mNameOperator = (EditText) findViewById(R.id.operator_name);
        mPhoneOperator = (EditText) findViewById(R.id.operator_phone);
        mAddressOperator = (EditText) findViewById(R.id.operator_address);

        mProfileImage = (CircleImageView) findViewById(R.id.profileImage);

        mConfirm = (MaterialButton) findViewById(R.id.btnConfirm);

        mGenderGroup = (RadioGroup) findViewById(R.id.genderGroup);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mOperatorDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Operators").child(userID);

        getUserInfo();

        mProfileImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,1);
        });

        mConfirm.setOnClickListener(view -> saveUserInformation());
    }

    private void getUserInfo(){
        mOperatorDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("name") != null){
                        mName = map.get("name").toString();
                        mNameOperator.setText(mName);
                    }
                    if (map.get("gender") != null) {
                        mGender = map.get("gender").toString();
                        switch (mGender) {
                            case "Male":
                                mGenderGroup.check(R.id.male);
                                break;
                            case "Female":
                                mGenderGroup.check(R.id.female);
                                break;
                        }
                    }
                    if (map.get("phone") != null){
                        mPhone = map.get("phone").toString();
                        mPhoneOperator.setText(mPhone);
                    }
                    if (map.get("address") != null){
                        mAddress = map.get("address").toString();
                        mAddressOperator.setText(mAddress);
                    }
                    if (map.get("profileImageUrl") != null){
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void saveUserInformation() {
        int selectedGender = mGenderGroup.getCheckedRadioButtonId();

        genderButton = (RadioButton) findViewById(selectedGender);

        if (genderButton.getText() == null){
            return;
        }

        mName = mNameOperator.getText().toString();
        mPhone = mPhoneOperator.getText().toString();
        mAddress = mAddressOperator.getText().toString();
        mGender = genderButton.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("name", mName);
        userInfo.put("gender", mGender);
        userInfo.put("phone", mPhone);
        userInfo.put("address", mAddress);

        mOperatorDatabase.updateChildren(userInfo);

        if (resultUri != null){
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(e -> {
                finish();
                return;
            });

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();

                downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Map newImage = new HashMap();
                        newImage.put("profileImageUrl", uri.toString());
                        mOperatorDatabase.updateChildren(newImage);

                        finish();
                        return;
                    }
                });
            });
        }else{
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }
}