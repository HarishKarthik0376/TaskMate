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

public class dateadapter extends RecyclerView.Adapter<dateadapter.ViewHolder> {
    public dateadapter(ArrayList<tasks> calenderlist, Context context) {
        this.calenderlist = calenderlist;
        this.context = context;
    }

    ArrayList<tasks> calenderlist = new ArrayList<tasks>();
    Context context;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.datelayout,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int adapos =holder.getAdapterPosition();
        tasks tasks = calenderlist.get(adapos);
        holder.date.setText(tasks.getDate());
    }

    @Override
    public int getItemCount() {
        return calenderlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.datetextcal);
        }
    }
}
