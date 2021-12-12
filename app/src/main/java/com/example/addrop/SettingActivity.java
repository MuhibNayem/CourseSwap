package com.example.addrop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private Button updateAcc;
    private TextInputEditText student_ID, fullname, username, phoneNum;
    private EditText status;
    private CircleImageView userProfilePic;
    private ProgressDialog loadingBar;
    private DatabaseReference reference;
    private FirebaseAuth userAuth;
    private String currentUserID;
    private static final int gallaryPick = 1;
    private StorageReference userProfilePicRef;
    Uri imageuri = null;

    String emailString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setting);

        initializeFileds();

        emailString = getIntent().getStringExtra("email");


        updateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
                loadingBar.show();
            }
        });

        retriveUserInfo();

        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cropImage();


            }
        });




    }

    private void cropImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

    }

    private void initializeFileds() {

        updateAcc = findViewById(R.id.update_btn);
        student_ID = findViewById(R.id.user_id_txt);
        userProfilePic = findViewById(R.id.profile_image);
        phoneNum = findViewById(R.id.regi_user_phone_txt);
        username = findViewById(R.id.regi_username_txt);
        fullname = findViewById(R.id.regi_fullname_txt);

        reference = FirebaseDatabase.getInstance().getReference();
        userAuth = FirebaseAuth.getInstance();
        currentUserID = userAuth.getCurrentUser().getUid();
        userProfilePicRef = FirebaseStorage.getInstance().getReference();

        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Updating");
        loadingBar.setMessage("Please wait....");
        loadingBar.setCanceledOnTouchOutside(true);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Picasso.get().load(resultUri).into(userProfilePic);

                uploadImage(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadImage(final Uri imageUri) {
        StorageReference filRef = userProfilePicRef.child(currentUserID+".jpg");
        filRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(SettingActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                StorageReference profilePicRef = FirebaseStorage.getInstance().getReference().child(currentUserID+".jpg");

                profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(userProfilePic);
                        imageuri = uri;

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String massage = e.toString().trim();
                Toast.makeText(SettingActivity.this, "Error: " + massage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent HomeIntent = new Intent(getApplicationContext(), home_activity.class);
        HomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(HomeIntent);
        finish();
    }

    private void updateSettings() {
        final String setStudentID = student_ID.getText().toString().trim();
        final String phonenum = phoneNum.getText().toString().trim();
        final String fullName = fullname.getText().toString().trim();
        final String userName = username.getText().toString().trim();
        loadingBar.show();



        if (TextUtils.isEmpty(setStudentID)) {
            student_ID.setError("Field Can not be empty");
            loadingBar.dismiss();
        }

        if (!validateFullname() | !validatePhoneNumber() | !validateUsername()) {
            loadingBar.dismiss();
            return;
        } else {



            String uri = imageuri.toString();
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("username", userName);
            profileMap.put("name", fullName);
            profileMap.put("phone", phonenum);
            profileMap.put("Student ID", setStudentID);
            profileMap.put("User ID", currentUserID);
            profileMap.put("image", uri);
            profileMap.put("email", emailString);





            reference.child("Users").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                loadingBar.dismiss();
                                goToMainActivity();
                                Toast.makeText(SettingActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                String massage = task.getException().toString().trim();
                                Toast.makeText(SettingActivity.this, "Error :" + massage, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }
    }

    public void goToMainActivity() {
        Intent mainIntent = new Intent(SettingActivity.this, home_activity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private boolean validateFullname() {
        String val = fullname.getText().toString().trim();

        if (val.isEmpty()) {
            fullname.setError("Field can not be empty");
            return false;
        } else {
            fullname.setError(null);
            return true;
        }
    }

    private boolean validateUsername() {
        String val = username.getText().toString().trim();
        String checkSpaces = "\\A\\w{1,20}\\z";

        if (val.isEmpty()) {
            username.setError("Field can not be empty");
            return false;
        } else if (val.length() > 20) {
            username.setError("Username is too large");
            return false;
        } else if (!val.matches(checkSpaces)) {
            username.setError("No white spaces");
            return false;
        } else {
            username.setError(null);

            return true;
        }
    }

    private boolean validatePhoneNumber() {
        String phonenum = phoneNum.getText().toString().trim();
        if (phonenum.isEmpty()) {
            phoneNum.setError("Field can't be empty");
            return false;
        } else {
            phoneNum.setError(null);
            return true;
        }
    }

    private void retriveUserInfo() {

        final ProgressDialog _loadingbar;
        _loadingbar = new ProgressDialog(this);
        _loadingbar.setTitle("Collecting info");
        _loadingbar.setMessage("Please wait...");
        _loadingbar.show();

        reference.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("username")
                                && dataSnapshot.hasChild("phone") && dataSnapshot.hasChild("Student ID")) {

                            String _fullname = dataSnapshot.child("name").getValue().toString();
                            String _username = dataSnapshot.child("username").getValue().toString();
                            String _phonenum = dataSnapshot.child("phone").getValue().toString();
                            String _studentID = dataSnapshot.child("Student ID").getValue().toString();


                            StorageReference profilePicRef = FirebaseStorage.getInstance().getReference().child(currentUserID+".jpg");

                            profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.get().load(uri).into(userProfilePic);
                                    imageuri = uri;
                                }
                            });

                            fullname.setText(_fullname);
                            username.setText(_username);
                            phoneNum.setText(_phonenum);
                            student_ID.setText(_studentID);


                            _loadingbar.dismiss();


                        }

                        else
                            Toast.makeText(SettingActivity.this, "Please update information", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        _loadingbar.dismiss();

    }


}