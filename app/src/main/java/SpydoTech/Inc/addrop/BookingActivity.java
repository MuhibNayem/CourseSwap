package SpydoTech.Inc.addrop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookingActivity extends AppCompatActivity {

    String uid,stdName,stdPhone,stdEmail;
    String addCourseName, addCourseSec, dropCourseName, dropCourseSec;
    TextView id,name,phone,email, addCourseNameTxtView, addCourseSecTxtView, dropCourseNameTxtView, dropCourseSecTxtView;
    ImageView proImgView;

    Button book;

    private CircleImageView userProfilePic;
    private ProgressDialog loadingBar;
    private DatabaseReference reference;
    private FirebaseAuth userAuth;
    private String currentUserID;
    private static final int gallaryPick = 1;
    private StorageReference userProfilePicRef;
    Uri imageuri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        uid = getIntent().getStringExtra("uid");
        stdName = getIntent().getStringExtra("name");
        stdPhone = getIntent().getStringExtra("phone");
        stdEmail = getIntent().getStringExtra("email");
        addCourseName = getIntent().getStringExtra("addCourseName");
        addCourseSec = getIntent().getStringExtra("addCourseSec");
        dropCourseName = getIntent().getStringExtra("dropCourseName");
        dropCourseSec = getIntent().getStringExtra("dropCourseSec");

        id = findViewById(R.id.std_id);
        name = findViewById(R.id.std_name);
        phone = findViewById(R.id.std_phone);
        email = findViewById(R.id.std_email);
        proImgView = findViewById(R.id.profile_image);
        book = (Button) findViewById(R.id.booking_btn);
        addCourseNameTxtView = findViewById(R.id.std_wantToAdd);
        addCourseSecTxtView = findViewById(R.id.std_wantToAddSec);
        dropCourseNameTxtView = findViewById(R.id.std_wantToDrop);
        dropCourseSecTxtView = findViewById(R.id.std_wantToDropSec);

        reference = FirebaseDatabase.getInstance().getReference();
        userAuth = FirebaseAuth.getInstance();
        currentUserID = userAuth.getCurrentUser().getUid();


        id.setText(uid);
        name.setText(stdName);
        phone.setText(stdPhone);
        email.setText(stdEmail);
        addCourseNameTxtView.setText(addCourseName);
        addCourseSecTxtView.setText(addCourseSec);
        dropCourseNameTxtView.setText(dropCourseName);
        dropCourseSecTxtView.setText(dropCourseSec);

        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Updating");
        loadingBar.setMessage("Please wait....");
        loadingBar.setCanceledOnTouchOutside(true);

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ubdateInfo();

            }
        });
        retrivePropic(uid);

    }

    private void ubdateInfo() {
        loadingBar.show();
        HashMap<String, String> profileMap = new HashMap<>();

        profileMap.put("name", stdName);
        profileMap.put("phone", stdPhone);
        profileMap.put("User ID", uid);
        profileMap.put("email", stdEmail);
        profileMap.put("addCourseName", addCourseName);
        profileMap.put("addCourseSec", addCourseSec);
        profileMap.put("dropCourseName", dropCourseName);
        profileMap.put("dropCourseSec", dropCourseSec);





        reference.child("SwapDetails").child(currentUserID).child(dropCourseName).child(dropCourseSec).setValue(profileMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {


                            Toast.makeText(BookingActivity.this, "Booked successfully", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            deleteCouseSwapDetails();
                            goToMain();

                        } else {
                            String massage = task.getException().toString().trim();
                            Toast.makeText(BookingActivity.this, "Error :" + massage, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }



                    }
                });
    }

    private void goToMain() {
        startActivity(new Intent(getApplicationContext(),home_activity.class));

        finish();
    }

    private void retrivePropic(String id) {
        StorageReference profilePicRef = FirebaseStorage.getInstance().getReference().child(id+".jpg");

        profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(proImgView);
            }
        });
    }

    private void deleteCouseSwapDetails() {
        reference.child("Swap_Details").
                child(dropCourseName).child(dropCourseSec).
                child(addCourseName).child(addCourseSec).child(uid)
                .removeValue();
        loadingBar.dismiss();

    }


}