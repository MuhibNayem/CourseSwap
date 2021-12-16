package SpydoTech.Inc.addrop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UpdateSetting_BottomSheet extends BottomSheetFragment {

    private Button updateAcc;
    private TextInputEditText student_ID, fullname, username, phoneNum, email;
    private ProgressDialog loadingBar;

    private DatabaseReference reference;
    private FirebaseAuth userAuth;
    private String currentUserID;

    View view;

    String emailString;


    public UpdateSetting_BottomSheet(String emailString) {
        // Required empty public constructor
        this.emailString = emailString;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_update_setting__bottom_sheet, container, false);

        loadingBar = new ProgressDialog(getContext());
        loadingBar.setTitle("Updating");
        loadingBar.setMessage("Please wait....");
        loadingBar.setCanceledOnTouchOutside(true);

        reference = FirebaseDatabase.getInstance().getReference();
        userAuth = FirebaseAuth.getInstance();
        currentUserID = userAuth.getCurrentUser().getUid();

        updateAcc = view.findViewById(R.id.update_btn);
        student_ID = view.findViewById(R.id.user_id_txt);
        phoneNum = view.findViewById(R.id.regi_user_phone_txt);
        username = view.findViewById(R.id.regi_username_txt);
        fullname = view.findViewById(R.id.regi_fullname_txt);
        email = view.findViewById(R.id.regi_userEmail_txt);


        updateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });


        return view;
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

    private boolean validateUSerEmail() {
        String userEmail = email.getText().toString().trim();
        if (userEmail.isEmpty()) {
            email.setError("Field can't be empty");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    private void updateSettings() {
        final String setStudentID = student_ID.getText().toString().trim();
        final String phonenum = phoneNum.getText().toString().trim();
        final String fullName = fullname.getText().toString().trim();
        final String userName = username.getText().toString().trim();
        final String useremail = email.getText().toString().trim();
        loadingBar.show();



        if (TextUtils.isEmpty(setStudentID)) {
            student_ID.setError("Field Can not be empty");
            loadingBar.dismiss();
        }

        if (!validateFullname() | !validatePhoneNumber() | !validateUsername() | !validateUSerEmail()) {
            loadingBar.dismiss();
            return;
        } else {



            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("username", userName);
            profileMap.put("name", fullName);
            profileMap.put("phone", phonenum);
            profileMap.put("Student ID", setStudentID);
            profileMap.put("User ID", currentUserID);
            profileMap.put("email", useremail);





            reference.child("Users").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                loadingBar.dismiss();
                                Toast.makeText(getContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();
                                goToMainActivity();
                            } else {
                                String massage = task.getException().toString().trim();
                                Toast.makeText(getContext(), "Error :" + massage, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }
    }

    public void goToMainActivity() {
        Intent mainIntent = new Intent(getContext(), home_activity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        getActivity().finish();
    }

}