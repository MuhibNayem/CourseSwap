package SpydoTech.Inc.addrop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class studentDataAdapter extends RecyclerView.Adapter<studentDataAdapter.ViewHolder> {

    studentData[] studentdata;

    Context context;
    String addCourseName, addCourseSec, dropCourseName, dropCourseSec;

    ArrayList<String> userList;

    ImageView imageView;

    public studentDataAdapter(studentData[] studentdata, Activity activity, String addCourseName, String addCourseSec, String dropCourseName, String dropCourseSec) {
        this.studentdata = studentdata;
        this.context = activity;
        this.addCourseName = addCourseName;
        this.addCourseSec = addCourseSec;
        this.dropCourseName = dropCourseName;
        this.dropCourseSec = dropCourseSec;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.student_details_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final studentData dataList = studentdata[position];
        holder.name.setText(dataList.getName());

        holder.phone.setText(dataList.getPhone());

        holder.id.setText(dataList.getId());

        holder.email.setText(dataList.getEmail());


        holder.student_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), BookingActivity.class);
                intent.putExtra("uid", dataList.getId());
                intent.putExtra("name", dataList.getName());
                intent.putExtra("email", dataList.getEmail());
                intent.putExtra("phone", dataList.getPhone());
                intent.putExtra("addCourseName", addCourseName);
                intent.putExtra("addCourseSec", addCourseSec);
                intent.putExtra("dropCourseName", dropCourseName);
                intent.putExtra("dropCourseSec", dropCourseSec);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentdata.length;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name;
        TextView phone;
        TextView id;
        TextView email;
        TextView nameLbl;
        TextView phoneLbl;
        TextView idLbl;
        CardView student_details;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.std_name);
            phone = itemView.findViewById(R.id.std_phone);
            id = itemView.findViewById(R.id.std_id);
            email = itemView.findViewById(R.id.std_email);
            nameLbl = itemView.findViewById(R.id.std_name_lbl);
            phoneLbl = itemView.findViewById(R.id.std_phone_lbl);
            idLbl = itemView.findViewById(R.id.std_id_lbl);
            student_details = itemView.findViewById(R.id.std_details);

        }

    }
}
