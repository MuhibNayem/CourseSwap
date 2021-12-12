package SpydoTech.Inc.addrop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class home_activity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private Toolbar mToolBar;
    private FirebaseUser currentUser;
    private FirebaseAuth userAuth;
    private DatabaseReference reference, swapRef, swapReff;
    String currentUserId=null;
    String phoneString,emailString,emaiL;
    private itemViewModel viewModel;
    boolean isChecked;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home_activity);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new home_fragment()).commit();

        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.nav_home:
                        fragment = new home_fragment();
                        break;
                    case R.id.nav_search:
                        fragment = new search_fragment();
                        break;
                    case R.id.nav_history:
                        fragment = new history_fragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                return true;
            }
        });


        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        swapReff = FirebaseDatabase.getInstance().getReference();

        emaiL = getIntent().getStringExtra("email");

        mToolBar = (Toolbar) findViewById(R.id.mainpage_tool_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        viewModel = new ViewModelProvider(this).get(itemViewModel.class);
        viewModel.getData().observe(this,item ->{
            Intent mainIntent = new Intent(getApplicationContext(), SearchActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mainIntent.putExtra("list", item);
            startActivity(mainIntent);
            finish();
        });



    }
    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null && currentUserId==null) {
            sendUserToLoginActivity();
        }
        else {

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







}