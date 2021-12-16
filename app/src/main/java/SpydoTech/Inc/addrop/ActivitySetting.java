package SpydoTech.Inc.addrop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
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

public class ActivitySetting extends AppCompatActivity {


    private TextView student_ID, fullname, username, phoneNum, email;
    private EditText status;
    private CircleImageView userProfilePic;
    private ImageView editIcon;
    private ProgressDialog loadingBar;
    private DatabaseReference reference;
    private FirebaseAuth userAuth;
    private String currentUserID;
    private static final int gallaryPick = 1;
    private StorageReference userProfilePicRef;
    Uri imageuri = null;

    private Dialog dialog;
    Button Okay;

    String emailString;

    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.settings_layout);

        initializeFileds();

        emailString = getIntent().getStringExtra("email");

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
        student_ID = findViewById(R.id.std_ID);
        userProfilePic = findViewById(R.id.profile_image);
        phoneNum = findViewById(R.id.std_phoneNum);
        username = findViewById(R.id.std_username);
        fullname = findViewById(R.id.std_fullname);
        email = findViewById(R.id.std_email);
        editIcon = findViewById(R.id.editBtn);

        reference = FirebaseDatabase.getInstance().getReference();
        userAuth = FirebaseAuth.getInstance();
        currentUserID = userAuth.getCurrentUser().getUid();
        userProfilePicRef = FirebaseStorage.getInstance().getReference();

        mToolBar = (Toolbar) findViewById(R.id.mainpage_tool_bar);
        setSupportActionBar(mToolBar);

        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Updating");
        loadingBar.setMessage("Please wait....");
        loadingBar.setCanceledOnTouchOutside(true);

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit();
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.settings_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.updatePass){
            updatePass();
        }
        if (item.getItemId() == R.id.logout){
            final ProgressDialog _loadingbar;
            _loadingbar = new ProgressDialog(this);
            _loadingbar.setTitle("Logging Out");
            _loadingbar.setMessage("Please wait...");
            _loadingbar.show();

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            userAuth.signOut();
            sendUserToLoginActivity();
        }

        return true;
    }

    private void sendUserToLoginActivity() {
        Intent LoginIntent = new Intent(getApplicationContext(),LoginActivity.class);
        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
        finish();
    }

    private void updatePass() {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.show(getSupportFragmentManager(),bottomSheetFragment.getTag());

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

                Toast.makeText(ActivitySetting.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ActivitySetting.this, "Error: " + massage, Toast.LENGTH_SHORT).show();
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
                            String _studentEmail = dataSnapshot.child("email").getValue().toString();


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
                            email.setText(_studentEmail);


                            _loadingbar.dismiss();


                        }

                        else
                            Toast.makeText(ActivitySetting.this, "Please update information", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        _loadingbar.dismiss();

    }


    public void edit() {
        UpdateSetting_BottomSheet updateSetting_bottomSheet = new UpdateSetting_BottomSheet(emailString);
        updateSetting_bottomSheet.show(getSupportFragmentManager(),updateSetting_bottomSheet.getTag());
    }

}