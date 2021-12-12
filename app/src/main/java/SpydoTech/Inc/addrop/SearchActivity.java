package SpydoTech.Inc.addrop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private DatabaseReference reference;

    RecyclerView recyclerView;
    ImageView imageView;
    studentData[] stdData;
    String uid = null;
    String _fullname;
    String _phonenum;
    String _studentID;
    String _studentEmail;
    String addCourseName, addCourseSec, dropCourseName, dropCourseSec;

    private AutoCompleteTextView _dropCourseNameText;
    private AutoCompleteTextView _dropCourseSecText;
    private AutoCompleteTextView _addCourseNameText;
    private AutoCompleteTextView _addCourseSecText;


    ArrayList<String> list;
    ArrayList<String> userlist;
    int item;

    private Dialog dialog;
    Button Okay;
    studentDataAdapter stdDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reference = FirebaseDatabase.getInstance().getReference();

        list = (ArrayList<String>) getIntent().getSerializableExtra("list");


        imageView = findViewById(R.id.imageView);

        userlist = new ArrayList<>();


        addCourseName = list.get(1);
        addCourseSec = list.get(2);
        dropCourseName = list.get(3);
        dropCourseSec = list.get(4);

        _dropCourseNameText = findViewById(R.id.dropCourseNameText);
        _dropCourseSecText = findViewById(R.id.dropCourseSecText);
        _addCourseNameText = findViewById(R.id.addCourseNameText);
        _addCourseSecText = findViewById(R.id.addCourseSecText);

        for (int i = 0; i < list.size(); i = i + 5) {
            uid = list.get(i);

            retriveStudentInfo(uid);


        }



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), home_activity.class));
    }

    private void retriveStudentInfo(String uid) {


        final String id = uid.substring(5, 33);




                reference.child("Users").child(id)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("username")
                                        && dataSnapshot.hasChild("phone") && dataSnapshot.hasChild("Student ID")) {

                                    _fullname = dataSnapshot.child("name").getValue().toString();
                                    _phonenum = dataSnapshot.child("phone").getValue().toString();
                                    _studentID = dataSnapshot.child("User ID").getValue().toString();
                                    _studentEmail = dataSnapshot.child("email").getValue().toString();

                                    userlist.add(_fullname);
                                    userlist.add(_phonenum);
                                    userlist.add(_studentID);
                                    userlist.add(_studentEmail);




                                    stdData = new studentData[]{
                                            new studentData(_fullname, _phonenum, _studentID, _studentEmail)
                                    };

                                    studentDataAdapter stdDataAdapter = new studentDataAdapter(stdData, SearchActivity.this, addCourseName, addCourseSec, dropCourseName, dropCourseSec);
                                    recyclerView.setAdapter(stdDataAdapter);





                                } else {
                                    Toast.makeText(SearchActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });




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

    private void showDialog() {
        dialog = new Dialog(getApplicationContext());
        dialog.setContentView(R.layout.same_course_alert);

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

