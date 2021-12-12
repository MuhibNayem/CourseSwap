package com.example.addrop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private FirebaseUser currentUser;
    private FirebaseAuth userAuth;
    private DatabaseReference reference, swapRef, swapReff;

    private TextInputLayout dropCourse;
    private TextInputLayout dropCourseSec;
    private TextInputLayout addCourse;
    private TextInputLayout addCourseSec;

    private AutoCompleteTextView _dropCourseNameText;
    private AutoCompleteTextView _dropCourseSecText;
    private AutoCompleteTextView _addCourseNameText;
    private AutoCompleteTextView _addCourseSecText;

    String phoneString,emailString,emaiL;
    String currentUserId=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolBar = (Toolbar) findViewById(R.id.mainpage_tool_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        dropCourse = findViewById(R.id.dropCourseName);
        dropCourseSec = findViewById(R.id.dropCourseSection);
        addCourse = findViewById(R.id.addCourseName);
        addCourseSec = findViewById(R.id.addCourseSection);

        _dropCourseNameText = findViewById(R.id.dropCourseNameText);
        _dropCourseSecText = findViewById(R.id.dropCourseSecText);
        _addCourseNameText = findViewById(R.id.addCourseNameText);
        _addCourseSecText = findViewById(R.id.addCourseSecText);

        emaiL = getIntent().getStringExtra("email");

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        swapReff = FirebaseDatabase.getInstance().getReference();

        itemsforCourseName();
        itemsforCourseSec();

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null && currentUserId==null) {
            sendUserToLoginActivity();
        }
        else {
            retriveUserInfo();
            verifyUserExistance();
        }
    }

    private void verifyUserExistance() {
        final String currentUserId = currentUser.getUid();
        reference.child("Users").orderByChild(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child(currentUserId).child("Student ID").exists())) {

                } else {
                    sendUserToSettingActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendUserToSettingActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), SettingActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mainIntent.putExtra("email", emaiL);
        startActivity(mainIntent);
        finish();
    }

    private void sendUserToLoginActivity() {
        Intent LoginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
        finish();
    }

    private void sendUserToRegiActivity() {
        Intent LoginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
        finish();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        final ProgressDialog _loadingbar;
        _loadingbar = new ProgressDialog(this);

        if (item.getItemId() == R.id.main_settings_btn) {
            startActivity(new Intent(getApplicationContext(),SettingActivity.class));
            //finish();
        }
        if (item.getItemId() == R.id.main_logout_btn) {

            if (!checkInternetConnecttion()) {

                _loadingbar.setTitle("Logging Out");
                _loadingbar.setMessage("Please wait...");
                _loadingbar.show();
                sendUserToLoginActivity();

                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                userAuth.signOut();
            } else {
                Toast.makeText(this, "No Network Enabled", Toast.LENGTH_SHORT).show();
            }


        }
        _loadingbar.dismiss();
        return true;
    }

    public void itemsforCourseName() {

        String[] item = new String[]{

                "CSE110",
                "CSE111",
                "CSE220",
                "CSE221",
                "CSE260",
                "CSE230",
                "CSE250",
                "CSE251",
                "CSE320",
                "CSE321",
                "CSE330",
                "CSE331",
                "CSE370",
                "CSE421",
                "CSE470"

        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                HomeActivity.this,
                R.layout.dropdown_items,
                item
        );

        _dropCourseNameText.setAdapter(adapter);
        _addCourseNameText.setAdapter(adapter);

    }

    public void itemsforCourseSec() {

        String[] item = new String[]{

                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "10",
                "11",
                "12",
                "13",
                "14",
                "15",

        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                HomeActivity.this,
                R.layout.dropdown_items,
                item
        );

        _dropCourseSecText.setAdapter(adapter);
        _addCourseSecText.setAdapter(adapter);

    }

    private void retriveUserInfo() {
        currentUserId = currentUser.getUid();


        reference.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.hasChild("phone") && dataSnapshot.hasChild("email")) {

                            phoneString = dataSnapshot.child("phone").getValue().toString();
                            emailString = dataSnapshot.child("email").getValue().toString();
                            //Toast.makeText(HomeActivity.this, emailString, Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    public void letSubmit(View view) {

        final String setAddCourseNameText, setAddCourseSecText, setDropCourseNameText, setDropCourseSecText,setPhone;

        setAddCourseNameText = _addCourseNameText.getText().toString().trim();
        setAddCourseSecText = _addCourseSecText.getText().toString().trim();
        setDropCourseNameText = _dropCourseNameText.getText().toString().trim();
        setDropCourseSecText = _dropCourseSecText.getText().toString().trim();


        final ProgressDialog _loadingBar;
        _loadingBar = new ProgressDialog(this);

        final String currentUserID = currentUser.getUid();



        _loadingBar.setTitle("Submitting");
        _loadingBar.setMessage("Please Wait");


        if (checkInternetConnecttion()) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> profileMap = new HashMap<>();

            profileMap.put("WantToAddCourseName", setAddCourseNameText);
            profileMap.put("WantToAddCourseSection", setAddCourseSecText);
            profileMap.put("WantToDropCourseName", setDropCourseNameText);
            profileMap.put("WantToDropCourseSection", setDropCourseSecText);
            profileMap.put("uid", currentUserID);
            profileMap.put("phone", phoneString);
            profileMap.put("email", emailString);

            _loadingBar.show();


            reference.child("Swap_Details").child(setDropCourseNameText).child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                _loadingBar.dismiss();
                                Toast.makeText(HomeActivity.this, "Submitted successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                String massage = task.getException().toString().trim();
                                _loadingBar.dismiss();
                                Toast.makeText(HomeActivity.this, "Error :" + massage, Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
        }

    }

    public boolean checkInternetConnecttion() {
        ConnectivityManager manager =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if (activeNetwork != null) {
            return false;

        } else {
            return true;
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}