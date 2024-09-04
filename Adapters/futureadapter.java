package com.HkCodes.Todolist.Adapters;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.HkCodes.Todolist.Models.tasks;
import com.HkCodes.Todolist.R;
import com.HkCodes.Todolist.futuretasks;
import com.HkCodes.Todolist.reciever;
import com.HkCodes.Todolist.todaystask;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class futureadapter extends RecyclerView.Adapter<futureadapter.ViewHolder> {
    public futureadapter(ArrayList<tasks> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    ArrayList<tasks> arrayList = new ArrayList<tasks>();
    ArrayList<tasks> prioritytasks;
    ArrayList<tasks> todaytasks;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<tasks> completedtasks;
    Context context;
    SharedPreferences todasyhistory;
    SharedPreferences.Editor editor1;
    private FirebaseAuth mAuth;
    SharedPreferences reminderhis;
    SharedPreferences.Editor remindereditor;
    SharedPreferences completedhistory;
    SharedPreferences.Editor editorpending;
    int hourtosend;
    int mintosend;
    String ruidtoupdate;
    boolean status;
    String hr,min;
    public int hour,mins;
    NotificationManager notificationManager;
    private static final String CHANNEL_ID = "testingid";
    private static final int notificationid = 81;
    boolean checked;
    String Uid;
    String time;
    long milli;
    int finalevalue;
    int newreqcode;
    boolean isReminderEnabled;
    String title,description,date,times,category,ruid;
    String[] items = {"Home","Work","School","Personal"};

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.futruretasklayout,parent,false);
        futureadapter.ViewHolder viewHolder = new ViewHolder(view);
        notificationchanel(context);
        reminderhis = context.getSharedPreferences("reminderhis",Context.MODE_PRIVATE);
        remindereditor = reminderhis.edit();
        completedhistory = context.getSharedPreferences("completedtasks",Context.MODE_PRIVATE);
        editorpending = completedhistory.edit();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getUid();
        todasyhistory = context.getSharedPreferences("todaystasks",Context.MODE_PRIVATE);
        editor1 = todasyhistory.edit();
        int adapterposition = holder.getAdapterPosition();
        tasks tasks = arrayList.get(adapterposition);
        holder.taskname.setText(tasks.getTaskname());
        holder.taskdes.setText(tasks.getTaskdescription());
        holder.category.setText(tasks.getCategory());
        holder.date.setText(tasks.getDate());
        holder.time.setText(tasks.getTime());
        isReminderEnabled = reminderhis.getBoolean("ischecked"+tasks.getRuid(),false);
        holder.materialSwitch.setChecked(isReminderEnabled);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.confirmdelete);
                dialog.setCanceledOnTouchOutside(false);
                holder.delno = dialog.findViewById(R.id.nodelete);
                holder.delyes = dialog.findViewById(R.id.yesdelete);
                dialog.show();
                int adpaterpos = holder.getAdapterPosition();
                holder.delyes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (adpaterpos != RecyclerView.NO_POSITION) {
                            tasks delete = arrayList.get(adpaterpos);
                            loadDatanew();
                            if (completedtasks == null) {
                                completedtasks = new ArrayList<>();
                            }
                            ruidtoupdate = delete.getRuid();
                            completedtasks.add(new tasks(
                                    holder.taskname.getText().toString(),
                                    holder.taskdes.getText().toString(),
                                    holder.category.getText().toString(),
                                    holder.date.getText().toString(),
                                    ruidtoupdate,
                                    holder.time.getText().toString()
                            ));
                            Gson gsoncompleted = new Gson();
                            String Jsoncompleted = gsoncompleted.toJson(completedtasks);
                            editorpending.putString("completeddata" + Uid, Jsoncompleted);
                            editorpending.apply();
                            arrayList.remove(adpaterpos);
                            notifyItemRemoved(adpaterpos);
                            ((futuretasks) context).removedata(delete);
                            dialog.dismiss();
                        }
                    }
                });
                holder.delno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
        Dialog updatedialog = new Dialog(context);
        updatedialog.setContentView(R.layout.dialogtoaddtodaystask);
        updatedialog.setCanceledOnTouchOutside(false);
        updatedialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        updatedialog.getWindow().setGravity(Gravity.BOTTOM);
        holder.addatask = updatedialog.findViewById(R.id.addtask);
        holder.closedialog = updatedialog.findViewById(R.id.closetodaystask);
        holder.closedialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedialog.dismiss();
            }
        });
        if(holder.materialSwitch.isChecked())
        {
            holder.materialSwitch.setThumbTintList(context.getColorStateList(R.color.lightgreen));
            holder.materialSwitch.setTrackDecorationTintList(context.getColorStateList(R.color.lightgreen));
        }
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDatanew();
                int editpos = holder.getAdapterPosition();
                updatedialog.show();
                tasks updatetasks = arrayList.get(editpos);
                holder.updatetaskname =  updatedialog.findViewById(R.id.addatasknameedidttext);
                holder.updatetaskdesc = updatedialog.findViewById(R.id.addataskdescriptionedittext);
                holder.autoCompleteTextView = updatedialog.findViewById(R.id.autocompletetext);
                holder.datebtn = updatedialog.findViewById(R.id.datebtn);
                holder.timebtn = updatedialog.findViewById(R.id.timebtn);
                holder.checkBox = updatedialog.findViewById(R.id.checkbox);
                checked = false;
                ruidtoupdate = updatetasks.getRuid();
                holder.updatetaskname.setText(updatetasks.getTaskname());
                if(updatetasks.getTaskdescription().equals(""))
                {
                    holder.updatetaskdesc.setText("");
                }
                else {
                    holder.updatetaskdesc.setText(updatetasks.getTaskdescription());
                }
                holder.autoCompleteTextView.setText(updatetasks.getCategory());
                arrayAdapter = new ArrayAdapter<String>(context,R.layout.listitems,items);
                holder.autoCompleteTextView.setAdapter(arrayAdapter);
                holder.autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String item = parent.getItemAtPosition(position).toString();
                    }
                });
                holder.datebtn.setText(updatetasks.getDate());
                holder.timebtn.setText(updatetasks.getTime());
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        String format = "dd/MM/yy";
                        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                        holder.datebtn.setText(dateFormat.format(calendar.getTime()));
                    }
                };
                holder.datebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog =  new DatePickerDialog(context,date1,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                        datePickerDialog.show();

                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Calendar calender1 = Calendar.getInstance();
                        hour = calender1.get(Calendar.HOUR_OF_DAY);
                        mins = calender1.get(Calendar.MINUTE);
                        new Handler().postDelayed(this,100);
                    }
                },0);
                holder.timebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog timePickerDialog;
                        timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String modifiedtime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                                time = modifiedtime;
                                holder.timebtn.setText(modifiedtime);
                            }
                        },hour,mins,true);
                        timePickerDialog.setTitle("Time of the task");
                        timePickerDialog.show();
                    }
                });

                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(checked)
                        {
                            checked = false;
                        }
                        else
                        {
                            checked = true;
                        }
                    }
                });
                holder.addatask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tasks newtask = arrayList.get(editpos);
                        Calendar calendar1 = Calendar.getInstance();
                        String onopenformat = "dd/MM/yy";
                        SimpleDateFormat dateform = new SimpleDateFormat(onopenformat);
                if (holder.datebtn.getText().toString().equals(dateform.format(calendar1.getTime()))) {
                    if (!checked) {
                        if (todaytasks == null) {
                            todaytasks = new ArrayList<>();
                        }
                        todaytasks.add(new tasks(
                                holder.updatetaskname.getText().toString(),
                                holder.updatetaskdesc.getText().toString(),
                                holder.autoCompleteTextView.getText().toString(),
                                holder.datebtn.getText().toString(),
                                ruidtoupdate,
                                holder.timebtn.getText().toString()
                        ));
                        Gson gson5 = new Gson();
                        String Json5 = gson5.toJson(todaytasks);
                        editor1.putString("todaydata" + Uid, Json5);
                        editor1.apply();
                        notifyDataSetChanged();
                        arrayList.remove(editpos);
                        notifyItemRemoved(editpos);
                        ((futuretasks) context).removedata(newtask);
                        updatedialog.dismiss();
                        Toast.makeText(context, "Task moved to today's task", Toast.LENGTH_SHORT).show();
                    } else {
                        if (prioritytasks == null) {
                            prioritytasks = new ArrayList<>();
                        }
                        prioritytasks.add(new tasks(
                                holder.updatetaskname.getText().toString(),
                                holder.updatetaskdesc.getText().toString(),
                                holder.autoCompleteTextView.getText().toString(),
                                holder.datebtn.getText().toString(),
                                ruidtoupdate,
                                holder.timebtn.getText().toString()
                        ));
                        Gson gson3 = new Gson();
                        String Json3 = gson3.toJson(prioritytasks);
                        editor1.putString("prioritydata" + Uid, Json3);
                        editor1.commit();
                        arrayList.remove(editpos);
                        notifyItemRemoved(editpos);
                        ((futuretasks) context).removedata(newtask);
                        notifyDataSetChanged();
                        updatedialog.dismiss();
                        Toast.makeText(context, "Task moved to priority list", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!checked) {
                        arrayList.set(editpos,new tasks(
                                holder.updatetaskname.getText().toString(),
                                holder.updatetaskdesc.getText().toString(),
                                holder.autoCompleteTextView.getText().toString(),
                                holder.datebtn.getText().toString(),
                                ruidtoupdate,
                                holder.timebtn.getText().toString()
                        ));
                        Gson gson = new Gson();
                        String Json = gson.toJson(arrayList);
                        editor1.putString("futuredata" + Uid, Json);
                        editor1.apply();
                        holder.taskname.setText(newtask.getTaskname());
                        holder.taskdes.setText(newtask.getTaskdescription());
                        holder.category.setText(newtask.getCategory());
                        holder.date.setText(newtask.getDate());
                        holder.time.setText(newtask.getTime());
                        notifyDataSetChanged();
                        updatedialog.dismiss();
                    } else {
                        if (prioritytasks == null) {
                            prioritytasks = new ArrayList<>();
                        }
                        prioritytasks.add(new tasks(
                                holder.updatetaskname.getText().toString(),
                                holder.updatetaskdesc.getText().toString(),
                                holder.autoCompleteTextView.getText().toString(),
                                holder.datebtn.getText().toString(),
                                ruidtoupdate,
                                holder.timebtn.getText().toString()
                        ));
                        Gson gson1 = new Gson();
                        String Json1 = gson1.toJson(prioritytasks);
                        editor1.putString("prioritydata" + Uid, Json1);
                        editor1.apply();
                        arrayList.remove(editpos);
                        notifyItemRemoved(editpos);
                        ((futuretasks) context).removedata(newtask);
                        notifyDataSetChanged();
                        updatedialog.dismiss();
                        Toast.makeText(context, "Task moved to priority list", Toast.LENGTH_SHORT).show();
                    }
                }
                status = reminderhis.getBoolean("ischecked"+updatetasks.getRuid(),false);
                if(status) {
                    holder.materialSwitch.setThumbTintList(context.getColorStateList(R.color.babyred));
                    holder.materialSwitch.setTrackDecorationTintList(context.getColorStateList(R.color.babyred));
                    newreqcode = reminderhis.getInt("reqcode"+updatetasks.getRuid(),0);
                    remindereditor.putBoolean("ischecked" + updatetasks.getRuid(), false);
                    remindereditor.apply();
                    canclenotifs(context, newreqcode);

                }
                else
                {
                }
            }

        });
            }

        });
        holder.materialSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapos = holder.getAdapterPosition();
                tasks tasksreminder = arrayList.get(adapos);
                newreqcode = reminderhis.getInt("reqcode" + tasksreminder.getRuid(), 0);
                finalevalue = uniqyereqcode(tasksreminder.getRuid());
                Calendar calendar = Calendar.getInstance();
                if(holder.materialSwitch.isChecked())
                {
                    hr = tasksreminder.getTime().substring(0,2);
                    hourtosend = Integer.parseInt(hr);
                    min = tasksreminder.getTime().substring(3,5);
                    mintosend = Integer.parseInt(min);
                    remindereditor.putBoolean("ischecked"+tasksreminder.getRuid(),true);
                    remindereditor.apply();
                    holder.materialSwitch.setThumbTintList(context.getColorStateList(R.color.lightgreen));
                    holder.materialSwitch.setTrackDecorationTintList(context.getColorStateList(R.color.lightgreen));
                    if(newreqcode == 0)
                    {
                        remindereditor.putInt("reqcode" + tasksreminder.getRuid(),finalevalue);
                        remindereditor.apply();
                    }
                    newreqcode = reminderhis.getInt("reqcode"+tasksreminder.getRuid(),0);
                    calendar.set(Calendar.HOUR_OF_DAY,hourtosend);
                    calendar.set(Calendar.MINUTE,mintosend);
                    calendar.set(Calendar.SECOND,0);
                    calendar.set(Calendar.YEAR, Integer.parseInt(tasksreminder.getDate().substring(6, 8)) + 2000);
                    calendar.set(Calendar.MONTH, Integer.parseInt(tasksreminder.getDate().substring(3, 5)) - 1);
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tasksreminder.getDate().substring(0, 2)));
                    if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    milli = calendar.getTimeInMillis();
                    Intent redirect = new Intent(context, reciever.class);
                    redirect.removeExtra("keyactual");
                    title = tasksreminder.getTaskname();
                    description = tasksreminder.getTaskdescription();
                    date = tasksreminder.getDate();
                    times = tasksreminder.getTime();
                    category = tasksreminder.getCategory();
                    ruid = tasksreminder.getRuid();
                    remindereditor.putString(ruid+"title",title);
                    redirect.putExtra("keyactual",ruid);
                    remindereditor.apply();
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,newreqcode,redirect,PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,milli,pendingIntent);

                }
                else
                {
                    remindereditor.putBoolean("ischecked"+tasksreminder.getRuid(),false);
                    remindereditor.apply();
                    holder.materialSwitch.setThumbTintList(context.getColorStateList(R.color.babyred));
                    holder.materialSwitch.setTrackDecorationTintList(context.getColorStateList(R.color.babyred));
                    int adapterPosition  = holder.getAdapterPosition();
                    tasks off = arrayList.get(adapterPosition);
                    newreqcode = reminderhis.getInt("reqcode" + off.getRuid(),0);
                    canclenotifs(context,newreqcode);
                }
            }
        });

            }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskname,taskdes,date,time,category,dialogdeletetext;
        ImageView edit,delete;
        MaterialButton delno,delyes;
        EditText updatetaskname,updatetaskdesc;
        MaterialButton datebtn,timebtn;
        AutoCompleteTextView autoCompleteTextView;
        MaterialButton addatask;
        ImageView closedialog;
        MaterialSwitch materialSwitch;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskname = itemView.findViewById(R.id.tasknmaeedit1);
            taskdes = itemView.findViewById(R.id.taskdescriptionedit1);
            category = itemView.findViewById(R.id.categorytodayedit1);
            date = itemView.findViewById(R.id.todaysdateedit1);
            time = itemView.findViewById(R.id.todaystimeedit1);
            edit = itemView.findViewById(R.id.edittodaystask1);
            delete = itemView.findViewById(R.id.deletecompletetask1);
            materialSwitch = itemView.findViewById(R.id.futuretaskreminder);
        }

    }
    private void loadDatanew() {
        Gson gson2 = new Gson();
        String json2 = todasyhistory.getString("prioritydata" + Uid, null);
        Type type2 = new TypeToken<ArrayList<tasks>>() {
        }.getType();
        prioritytasks = gson2.fromJson(json2, type2);
        if(prioritytasks==null)
        {
            prioritytasks = new ArrayList<>();
        }
        Gson gson3 = new Gson();
        String json3 = todasyhistory.getString("todaydata" + Uid, null);
        Type type3 = new TypeToken<ArrayList<tasks>>() {
        }.getType();
        todaytasks = gson3.fromJson(json3, type3);
        if(todaytasks==null)
        {
            todaytasks = new ArrayList<>();
        }
        Gson gsonc = new Gson();
        String jsonc = completedhistory.getString("completeddata" + Uid, null);
        Type typec = new TypeToken<ArrayList<tasks>>() {
        }.getType();
        completedtasks = gsonc.fromJson(jsonc, typec);
        if(completedtasks==null)
        {
            completedtasks = new ArrayList<>();
        }
    }
    private int uniqyereqcode(String uuid)
    {
        finalevalue = uuid.hashCode();
        return finalevalue;
    }
    private void notificationchanel(Context context) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Reminder", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Checkchannel");
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
    private void canclenotifs(Context context,int reqcode)
    {
        Intent intent = new Intent(context, reciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,reqcode,intent,PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        notificationManager.cancel(newreqcode);
    }
}
