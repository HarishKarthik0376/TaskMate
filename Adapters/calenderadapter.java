package com.HkCodes.Todolist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.HkCodes.Todolist.Models.tasks;
import com.HkCodes.Todolist.R;

import java.util.ArrayList;

public class calenderadapter extends RecyclerView.Adapter<calenderadapter.ViewHolder> {
    public calenderadapter(ArrayList<tasks> calenderlist, Context context) {
        this.calenderlist = calenderlist;
        this.context = context;
    }

    ArrayList<tasks> calenderlist = new ArrayList<tasks>();
    Context context;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layoutofcalender,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int adapos =holder.getAdapterPosition();
        tasks tasks = calenderlist.get(adapos);
        holder.taskname.setText(tasks.getTaskname());
        holder.grp.setText(tasks.getCategory());
        holder.date.setText(tasks.getDate());
        holder.time.setText(tasks.getTime());
    }

    @Override
    public int getItemCount() {
        return calenderlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskname,grp,date,time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskname = itemView.findViewById(R.id.tasknamecalender);
            grp = itemView.findViewById(R.id.grpnamecalender);
            date = itemView.findViewById(R.id.datecal);
            time = itemView.findViewById(R.id.timecal);
        }
    }
}
