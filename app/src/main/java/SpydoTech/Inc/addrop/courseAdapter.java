package SpydoTech.Inc.addrop;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class courseAdapter extends  RecyclerView.Adapter<courseAdapter.ViewHolder>{

    studentData[] studentdata;
    Context cotext;
    String addCourseName, addCourseSec, dropCourseName, dropCourseSec,id;

    ImageView imageView;
    private Dialog dialog;
    private DatabaseReference reference;
    private FirebaseAuth userAuth;
    private String currentUserID;
    RecyclerView recyclerView;

    Button Okay, Cancel;

    public courseAdapter(studentData[] studentdata,String addCourseName, String addCourseSec, String dropCourseName, String dropCourseSec, RecyclerView recyclerView) {
        this.studentdata = studentdata;
        this.addCourseName = addCourseName;
        this.addCourseSec = addCourseSec;
        this.dropCourseName = dropCourseName;
        this.dropCourseSec = dropCourseSec;
        this.recyclerView = recyclerView;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.course_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        studentData dataList = studentdata[position];
        holder.name.setText(dataList.getName());

        holder.phone.setText(dataList.getPhone());

        holder.id.setText(dataList.getId());

        id = dataList.getId();

        holder.email.setText(dataList.getEmail());


        holder.student_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(v,holder);


            }
        });

    }

    private void showDialog(View v, ViewHolder holder) {
        dialog = new Dialog(v.getContext());
        dialog.setContentView(R.layout.custom_dialog_layout);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        Okay = dialog.findViewById(R.id.btn_okay);
        Cancel = dialog.findViewById(R.id.btn_cancel);

        Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecord();
                dialog.dismiss();


            }


        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private void deleteRecord() {
        reference = FirebaseDatabase.getInstance().getReference();
        userAuth = FirebaseAuth.getInstance();
        currentUserID = userAuth.getCurrentUser().getUid();
        reference.child("SwapDetails").child(currentUserID).
                child(dropCourseName).child(dropCourseSec)
                .removeValue();



    }

    @Override
    public int getItemCount() {
        return studentdata.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView phone;
        TextView id;
        TextView email;
        TextView nameLbl;
        TextView phoneLbl;
        TextView idLbl;
        CardView student_details;
        public ViewHolder(@NonNull View itemView){
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.std_name);
            phone = itemView.findViewById(R.id.std_phone);
            id = itemView.findViewById(R.id.std_id);
            email = itemView.findViewById(R.id.std_email);
            nameLbl = itemView.findViewById(R.id.std_name_lbl);
            phoneLbl = itemView.findViewById(R.id.std_phone_lbl);
            idLbl = itemView.findViewById(R.id.std_id_lbl);
            student_details = itemView.findViewById(R.id.course_details);

        }

    }
}
