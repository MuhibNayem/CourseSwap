package com.example.addrop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private DatabaseReference reference;

    RecyclerView recyclerView;
    ImageView imageView;
    studentData[] stdData;
    String uid=null;
    String _fullname;
    String _phonenum;
    String _studentID;
    String _studentEmail;
    String addCourseName, addCourseSec, dropCourseName, dropCourseSec;


    ArrayList<String> list;
    ArrayList<String> courselist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reference = FirebaseDatabase.getInstance().getReference();

        list = (ArrayList<String>) getIntent().getSerializableExtra("list");

        imageView = findViewById(R.id.imageView);

        for(int i = 0; i<list.size(); i=i+5){
            uid = list.get(i);
            retriveStudentInfo(uid);
        }
        addCourseName=list.get(1);
        addCourseSec=list.get(2);
        dropCourseName=list.get(3);
        dropCourseSec=list.get(4);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), home_activity.class));
    }

    private void retriveStudentInfo(String uid) {
        final String id = uid.substring(5,33);



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


                            stdData = new studentData[]{
                                    new studentData(_fullname, _phonenum,_studentID,_studentEmail)
                            };

                            studentDataAdapter stdDataAdapter = new studentDataAdapter(stdData, SearchActivity.this,addCourseName, addCourseSec, dropCourseName, dropCourseSec);
                            recyclerView.setAdapter(stdDataAdapter);


                        }

                        else{
                            Toast.makeText(SearchActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }
}