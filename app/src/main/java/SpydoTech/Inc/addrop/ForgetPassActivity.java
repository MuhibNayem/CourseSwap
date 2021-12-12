package SpydoTech.Inc.addrop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgetPassActivity extends AppCompatActivity {

    private TextInputLayout resest_email;

    private Button btnReset;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forget_pass);

        resest_email = findViewById(R.id.forgetpassTxt);
        btnReset = findViewById(R.id.forgetpassBtn);
        loadingBar = new ProgressDialog(this);

    }

    public void resetpasswoord(View view) {
        String emailaddress = resest_email.getEditText().getText().toString();
        if (!emailaddress.isEmpty()) {
            loadingBar.setTitle("Sending Link");
            loadingBar.setMessage("Please wait....");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            FirebaseAuth auth = FirebaseAuth.getInstance();

            auth.sendPasswordResetEmail(emailaddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loadingBar.dismiss();
                                Toast.makeText(getApplicationContext(), "Check Your Email", Toast.LENGTH_SHORT).show();
                            } else {
                                loadingBar.dismiss();
                                Toast.makeText(ForgetPassActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                resest_email.setError("Incorrect email");
                            }
                        }
                    });
        }else{
            resest_email.setError("email can not be empty");
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

    public void back(View view) {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }
}