package com.example.addrop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class home_fragment extends Fragment {


    private FirebaseUser currentUser;
    private FirebaseAuth userAuth;
    private DatabaseReference reference, swapRef, swapReff;
    Button submit;

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

    View view;

    public home_fragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_home_fragment, container, false);



        dropCourse = view.findViewById(R.id.dropCourseName);
        dropCourseSec = view.findViewById(R.id.dropCourseSection);
        addCourse = view.findViewById(R.id.addCourseName);
        addCourseSec = view.findViewById(R.id.addCourseSection);

        _dropCourseNameText = view.findViewById(R.id.dropCourseNameText);
        _dropCourseSecText = view.findViewById(R.id.dropCourseSecText);
        _addCourseNameText = view.findViewById(R.id.addCourseNameText);
        _addCourseSecText = view.findViewById(R.id.addCourseSecText);

        setHasOptionsMenu(true);

        submit = (Button) view.findViewById(R.id.submit_btn);
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                letSubmit();

            }
        });

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        swapReff = FirebaseDatabase.getInstance().getReference();

        itemsforCourseName();
        itemsforCourseSec();

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                getActivity(),
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
                getActivity(),
                R.layout.dropdown_items,
                item
        );

        _dropCourseSecText.setAdapter(adapter);
        _addCourseSecText.setAdapter(adapter);

    }



    public void letSubmit() {

        final String setAddCourseNameText, setAddCourseSecText, setDropCourseNameText, setDropCourseSecText,setPhone;

        setAddCourseNameText = _addCourseNameText.getText().toString().trim();
        setAddCourseSecText = _addCourseSecText.getText().toString().trim();
        setDropCourseNameText = _dropCourseNameText.getText().toString().trim();
        setDropCourseSecText = _dropCourseSecText.getText().toString().trim();


        final ProgressDialog _loadingBar;
        _loadingBar = new ProgressDialog(getActivity());

        final String currentUserID = currentUser.getUid();



        _loadingBar.setTitle("Submitting");
        _loadingBar.setMessage("Please Wait");


        if (checkInternetConnecttion()) {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        } else {
            if (!setAddCourseNameText.isEmpty() && !setAddCourseSecText.isEmpty() && !setDropCourseNameText.isEmpty() && !setDropCourseSecText.isEmpty()) {
                HashMap<String, String> profileMap = new HashMap<>();


                profileMap.put("uid", currentUserID);


                _loadingBar.show();


                reference.child("Swap_Details").child(setDropCourseNameText).child(setDropCourseSecText).child(setAddCourseNameText).child(setAddCourseSecText).child(currentUserID)
                        .setValue(profileMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    _loadingBar.dismiss();
                                    Toast.makeText(getActivity(), "Submitted Successfully", Toast.LENGTH_SHORT).show();


                                } else {
                                    String massage = task.getException().toString().trim();
                                    _loadingBar.dismiss();
                                    Toast.makeText(getActivity(), "Error :" + massage, Toast.LENGTH_SHORT).show();

                                }

                            }
                        });
            }
            else{
                Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
            }
        }

    }
    public boolean checkInternetConnecttion() {
        ConnectivityManager manager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if (activeNetwork != null) {
            return false;

        } else {
            return true;
        }


    }



}