package SpydoTech.Inc.addrop;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class BottomSheetFragment extends BottomSheetDialogFragment {


    Button chanage;
    private TextInputLayout newPassword, confirmPass;
    private FirebaseUser currentUser;
    private FirebaseAuth userAuth;
    private ProgressDialog loadingBar;

    View view;

    public BottomSheetFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_bottom_sheet, container, false);

        newPassword = view.findViewById(R.id.newPass);
        confirmPass = view.findViewById(R.id.confirmPass);

        chanage = view.findViewById(R.id.change_btn);

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();

        loadingBar = new ProgressDialog(getContext());

        chanage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePass();

            }
        });

        return  view;
    }

    private void updatePass() {
        String newPass = newPassword.getEditText().getText().toString().trim();
        String confirmPassword = confirmPass.getEditText().getText().toString().trim();

        if (newPass.equals(confirmPassword) && !newPass.isEmpty() && !confirmPassword.isEmpty() && validatePass() &&
        validateComPass()) {
            loadingBar.setTitle("Updating");
            loadingBar.setMessage("Please wait....");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            currentUser.updatePassword(confirmPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void avoid) {
                    Toast.makeText(getContext(), "Password updated Successfully",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.toString(),Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            });
        }

        else{
            return;
        }
    }

    private boolean validatePass() {
        String val = newPassword.getEditText().getText().toString().trim();
        String checkPass = "(?=^.{6,255}$)((?=.*\\d)(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[^A-Za-z0-9])(?=.*[a-z])|(?=.*[^A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[A-Z])(?=.*[^A-Za-z0-9]))^.*";
        if (val.isEmpty()) {
            newPassword.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkPass) || val.length()<6) {
            newPassword.setError("Password should be Complex");
            return false;
        } else {
            newPassword.setError(null);
            newPassword.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateComPass() {
        String com_Pass = confirmPass.getEditText().getText().toString().trim();
        String check_pass = confirmPass.getEditText().getText().toString().trim();
        if (!com_Pass.equals(check_pass) || com_Pass.length()<6) {
            confirmPass.setError("Password mismatch");
            return false;
        }
        else if (com_Pass.isEmpty()) {
            confirmPass.setError("Field can not be empty");
            return false;
        }
        else {
            confirmPass.setError(null);
            confirmPass.setErrorEnabled(false);
            return true;
        }
    }


}