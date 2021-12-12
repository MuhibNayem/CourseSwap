package com.example.addrop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Button regipage, login, phoneLogin;
    private TextInputLayout email, pass;
    private ProgressDialog loadingBar;
    private FirebaseAuth userAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        regipage = findViewById(R.id.registration_btn);
        login = findViewById(R.id.login_btn);

        email = findViewById(R.id.login_email);
        pass = findViewById(R.id.login_pass);

        loadingBar = new ProgressDialog(this);
        userAuth = FirebaseAuth.getInstance();
    }

    public void callRegiPage(View view) {
        startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
        finish();

    }

    public void letLogin(View view) {
        if (!validateFields()) {
            return;
        }

        loadingBar.setTitle("logging in");
        loadingBar.setMessage("Please wait....");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        final String mail = email.getEditText().getText().toString().trim();
        final String password = pass.getEditText().getText().toString().trim();

        userAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            goToHomeActivity();
                            loadingBar.dismiss();
                            finish();
                            Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                        }
                        else if (checkInternetConnecttion()){
                            Toast.makeText(LoginActivity.this, "No Network Enabled", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                        else {

                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                email.setError("Incorrect email");
                            }else{
                                pass.setError("Paswword incorrect");
                            }

                            loadingBar.dismiss();


                        }
                    }
                });

    }

    private boolean validateFields() {
        String _email = email.getEditText().getText().toString().trim();
        String _pass = pass.getEditText().getText().toString().trim();
        if (_email.isEmpty()) {
            email.setError("Field can not be empty");
            email.requestFocus();
            return false;
        } else if (_pass.isEmpty()) {
            pass.setError("Field can not be empty");
            pass.requestFocus();
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            pass.setError(null);
            pass.setErrorEnabled(false);

            return true;

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

    public void goToHomeActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), home_activity.class);
        startActivity(mainIntent);
        finish();
    }

    public void callforgetPassPage(View view){
        Intent loginIntent = new Intent(getApplicationContext(), ForgetPassActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

}