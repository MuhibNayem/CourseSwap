package SpydoTech.Inc.addrop;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class history_fragment extends Fragment {

    private View view;


    private FirebaseUser currentUser;
    private FirebaseAuth userAuth;
    private DatabaseReference reference;

    private AutoCompleteTextView _addCourseNameText;
    private AutoCompleteTextView _addCourseSecText;

    RecyclerView recyclerView;

    Button search;

    studentData[] stdData;

    private Dialog dialog;
    Button Okay;

    String _fullname;
    String _phonenum;
    String _studentID;
    String _studentEmail;
    String currentUserId;
    String addCourseName, addCourseSec, dropCourseName, dropCourseSec;

    private TextInputLayout addCourseTxt;
    private TextInputLayout addCourseSecTxt;



    public history_fragment() {

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_history, container, false);

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        currentUserId = currentUser.getUid();

        addCourseTxt = view.findViewById(R.id.addCourseName);
        addCourseSecTxt = view.findViewById(R.id.addCourseSection);

        _addCourseNameText = view.findViewById(R.id.addCourseNameText);
        _addCourseSecText = view.findViewById(R.id.addCourseSecText);


        setHasOptionsMenu(true);

        search = view.findViewById(R.id.btn_search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retriveStudentInfo(currentUserId,v);

            }
        });



        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        itemsforCourseName();
        itemsforCourseSec();






        return  view;
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


        _addCourseSecText.setAdapter(adapter);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void retriveStudentInfo(String uid, View v) {
        final ProgressDialog _loadingbar;
        _loadingbar = new ProgressDialog(getActivity());
        _loadingbar.setTitle("Searching");
        _loadingbar.setMessage("Please wait...");
        _loadingbar.show();

        final String setAddCourseNameText, setAddCourseSecText;

        setAddCourseNameText = _addCourseNameText.getText().toString().trim();
        setAddCourseSecText = _addCourseSecText.getText().toString().trim();

        if (!setAddCourseNameText.isEmpty() && !setAddCourseSecText.isEmpty()) {

            addCourseTxt.setError(null);
            addCourseTxt.setErrorEnabled(false);
            addCourseSecTxt.setError(null);
            addCourseSecTxt.setErrorEnabled(false);

            reference.child("SwapDetails").child(uid).child(setAddCourseNameText).child(setAddCourseSecText)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("phone")) {

                                _fullname = dataSnapshot.child("name").getValue().toString();
                                _phonenum = dataSnapshot.child("phone").getValue().toString();
                                _studentID = dataSnapshot.child("User ID").getValue().toString();
                                _studentEmail = dataSnapshot.child("email").getValue().toString();
                                addCourseName = dataSnapshot.child("addCourseName").getValue().toString();
                                addCourseSec = dataSnapshot.child("addCourseSec").getValue().toString();
                                dropCourseName = dataSnapshot.child("dropCourseName").getValue().toString();
                                dropCourseSec = dataSnapshot.child("dropCourseSec").getValue().toString();

                                stdData = new studentData[]{
                                        new studentData(_fullname, _phonenum, _studentID, _studentEmail)
                                };

                                courseAdapter stdDataAdapter = new courseAdapter(stdData, addCourseName, addCourseSec, dropCourseName, dropCourseSec, recyclerView);
                                recyclerView.setAdapter(stdDataAdapter);


                                _loadingbar.dismiss();


                            } else {
                                showNotFoundDialog(v);
                                _loadingbar.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }else{
            addCourseTxt.setError("Empty Field");
            addCourseSecTxt.setError("Empty Field");
            _loadingbar.dismiss();

        }



    }

    private void showNotFoundDialog(View v) {
        dialog = new Dialog(v.getContext());
        dialog.setContentView(R.layout.no_record_found);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        Okay = dialog.findViewById(R.id.btn_okay);

        Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();


            }


        });

        dialog.show();
    }



}