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

import com.HkCodes.Todolist.MainActivity;
import com.HkCodes.Todolist.Models.tasks;
import com.HkCodes.Todolist.R;
import com.HkCodes.Todolist.reciever;
import com.HkCodes.Todolist.todaystask;
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

public class priority extends RecyclerView.Adapter<priority.ViewHolder> {
    public priority(ArrayList<tasks> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    ArrayList<tasks> arrayList;
    Context context;
    int count = 0;
    ArrayList<tasks> futuretasks;
    ArrayList<tasks> todaystask;
    ArrayList<tasks> completedtasks;
    int hourtosend;
    int mintosend;
    String ruidtoupdate;
    boolean status;
    String hr,min;
    int datetosend;
    ArrayAdapter<String> arrayAdapter;
    SharedPreferences todasyhistory;
    SharedPreferences.Editor editor1;
    SharedPreferences reminderhis;
    SharedPreferences.Editor remindereditor;
    SharedPreferences completedhistory;
    SharedPreferences.Editor editorpending;
    public int hour,mins;
    NotificationManager notificationManager;
    private static final String CHANNEL_ID = "testingid";
    private static final int notificationid = 81;
    boolean checked;
    String Uid;
    String time;
    boolean ischecked;
    long milli;
    int finalevalue;
    int newreqcode;
    boolean isReminderEnabled;
    private FirebaseAuth mAuth;
    String title,description,date,times,category,ruid;
    String[] items = {"Home","Work","School","Personal"};
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.horiziontallayout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        notificationchanel(context);
        reminderhis = context.getSharedPreferences("reminderhis",Context.MODE_PRIVATE);
        remindereditor = reminderhis.edit();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int adapterpos = holder.getAdapterPosition();
        tasks tasks = arrayList.get(adapterpos);
        holder.taskname.setText(tasks.getTaskname());
        holder.date.setText(tasks.getDate());
        holder.time.setText(tasks.getTime());
        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getUid();
        todasyhistory = context.getSharedPreferences("todaystasks",Context.MODE_PRIVATE);
        editor1 = todasyhistory.edit();
        completedhistory = context.getSharedPreferences("completedtasks",Context.MODE_PRIVATE);
        editorpending = completedhistory.edit();
        tasks tasks1 = arrayList.get(position);
        isReminderEnabled = reminderhis.getBoolean("ischecked"+tasks1.getRuid(),false);
        if(isReminderEnabled)
        {
            holder.clock.setImageResource(R.drawable.alarmclock);
        }
        else
        {
            holder.clock.setImageResource(R.drawable.alarmclockoff);
        }
        if(newreqcode == 0)
        {
            remindereditor.putInt("reqcode" + tasks1.getRuid(),finalevalue);
            remindereditor.apply();
        }
        newreqcode = reminderhis.getInt("reqcode"+tasks1.getRuid(),0);
        holder.deletetask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.confirmdelete);
                dialog.setCanceledOnTouchOutside(false);
                holder.delno = dialog.findViewById(R.id.nodelete);
                holder.delyes = dialog.findViewById(R.id.yesdelete);
                dialog.show();
                int adpaterpos1 = holder.getAdapterPosition();
                holder.delyes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadDatanew();
                        if (adpaterpos1 != RecyclerView.NO_POSITION) {
                            tasks delete = arrayList.get(adpaterpos1);
                            if (completedtasks == null) {
                                completedtasks = new ArrayList<>();
                            }
                            ruidtoupdate = delete.getRuid();
                            completedtasks.add(new tasks(
                                    holder.taskname.getText().toString(),
                                    delete.getTaskdescription(),
                                    delete.getCategory(),
                                    holder.date.getText().toString(),
                                    ruidtoupdate,
                                    holder.time.getText().toString()
                            ));
                            Gson gsoncompleted = new Gson();
                            String Jsoncompleted = gsoncompleted.toJson(completedtasks);
                            editorpending.putString("completeddata" + Uid, Jsoncompleted);
                            editorpending.apply();
                            arrayList.remove(adpaterpos1);
                            ((MainActivity) context).removedata(delete);
                            notifyItemRemoved(adpaterpos1);
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

//        holder.clock.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(count == 0) {
//                    holder.clock.setImageResource(R.drawable.alarmclock);
//                    count=1;
//                }
//                else
//                {
//                    holder.clock.setImageResource(R.drawable.alarmclockoff);
//                    count=0;
//                }
//            }
//        });
        holder.clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapos = holder.getAdapterPosition();
                tasks tasksreminder = arrayList.get(adapos);
                newreqcode = reminderhis.getInt("reqcode" + tasksreminder.getRuid(), 0);
                finalevalue = uniqyereqcode(tasksreminder.getRuid());
                Calendar calendar = Calendar.getInstance();
                isReminderEnabled = reminderhis.getBoolean("ischecked"+tasks1.getRuid(),false);
                if(!isReminderEnabled)
                {
                    hr = tasksreminder.getTime().substring(0,2);
                    hourtosend = Integer.parseInt(hr);
                    min = tasksreminder.getTime().substring(3,5);
                    mintosend = Integer.parseInt(min);
                    remindereditor.putBoolean("ischecked"+tasksreminder.getRuid(),true);
                    remindereditor.apply();
                    holder.clock.setImageResource(R.drawable.alarmclock);
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
                    holder.clock.setImageResource(R.drawable.alarmclockoff);
                    int adapterPosition  = holder.getAdapterPosition();
                    tasks off = arrayList.get(adapterPosition);
                    newreqcode = reminderhis.getInt("reqcode" + off.getRuid(),0);
                    canclenotifs(context,newreqcode);
                }
            }
        });
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.expandview);
        holder.dialgtaskdate = dialog.findViewById(R.id.expanddate);
        holder.dialogtaskcat = dialog.findViewById(R.id.expandcat);
        holder.dialogtaskname = dialog.findViewById(R.id.expandtaskname);
        holder.diealogtaskdesc = dialog.findViewById(R.id.expanddesc);
        holder.dialogtasktime = dialog.findViewById(R.id.expandtime);
        holder.edittask = dialog.findViewById(R.id.editexpand);
        dialog.setCanceledOnTouchOutside(false);
        holder.viewtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapos = holder.getAdapterPosition();
                tasks tasks1 = arrayList.get(adapos);
                holder.dialogtasktime.setText(tasks1.getTime());
                holder.dialgtaskdate.setText(tasks1.getDate());
                holder.dialogtaskcat.setText(tasks1.getCategory());
                holder.diealogtaskdesc.setText(tasks1.getTaskdescription());
                holder.dialogtaskname.setText(tasks1.getTaskname());
                holder.closedialog = dialog.findViewById(R.id.closeexpand);
//                holder.clock1 = dialog.findViewById(R.id.clock);
                holder.delexpand = dialog.findViewById(R.id.deleexpand);
                holder.edittask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog editdialog = new Dialog(context);
                        dialog.dismiss();
                        editdialog.setContentView(R.layout.dialogtoaddtodaystask);
                        editdialog.setCanceledOnTouchOutside(false);
                        editdialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        editdialog.getWindow().setGravity(Gravity.BOTTOM);
                        editdialog.show();
                        loadDatanew();
                        int editpos = holder.getAdapterPosition();
                        tasks updatetasks = arrayList.get(editpos);
                        ruidtoupdate = updatetasks.getRuid();
                        holder.addatask = editdialog.findViewById(R.id.addtask);
                        holder.closedialog = editdialog.findViewById(R.id.closetodaystask);
                        holder.closedialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editdialog.dismiss();
                                dialog.show();
                            }
                        });
                        holder.updatetaskname =  editdialog.findViewById(R.id.addatasknameedidttext);
                        holder.updatetaskdesc = editdialog.findViewById(R.id.addataskdescriptionedittext);
                        holder.autoCompleteTextView = editdialog.findViewById(R.id.autocompletetext);
                        holder.datebtn = editdialog.findViewById(R.id.datebtn);
                        holder.timebtn = editdialog.findViewById(R.id.timebtn);
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
                        holder.checkBox = editdialog.findViewById(R.id.checkbox);
                        checked = true;
                        holder.checkBox.setChecked(checked);
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
                                        if (todaystask == null) {
                                            todaystask = new ArrayList<>();
                                        }
                                        todaystask.add(new tasks(
                                                holder.updatetaskname.getText().toString(),
                                                holder.updatetaskdesc.getText().toString(),
                                                holder.autoCompleteTextView.getText().toString(),
                                                holder.datebtn.getText().toString(),
                                                ruidtoupdate,
                                                holder.timebtn.getText().toString()

                                        ));
                                        Gson gson = new Gson();
                                        String Json = gson.toJson(todaystask);
                                        editor1.putString("todaydata" + Uid, Json);
                                        editor1.commit();
                                        arrayList.remove(editpos);
                                        notifyItemRemoved(editpos);
                                        ((MainActivity) context).removedata(newtask);
                                        notifyDataSetChanged();
                                        editdialog.dismiss();
                                    } else {
                                        arrayList.set(editpos,new tasks(
                                                holder.updatetaskname.getText().toString(),
                                                holder.updatetaskdesc.getText().toString(),
                                                holder.autoCompleteTextView.getText().toString(),
                                                holder.datebtn.getText().toString(),
                                                ruidtoupdate,
                                                holder.timebtn.getText().toString()
                                        ));
                                        Gson gson3 = new Gson();
                                        String Json3 = gson3.toJson(arrayList);
                                        editor1.putString("prioritydata" + Uid, Json3);
                                        editor1.apply();
                                        holder.taskname.setText(newtask.getTaskname());
                                        notifyDataSetChanged();
                                        editdialog.dismiss();
                                    }
                                } else {
                                    if (!checked) {
                                        if (futuretasks == null) {
                                            futuretasks = new ArrayList<>();
                                        }
                                        futuretasks.add(new tasks(
                                                holder.updatetaskname.getText().toString(),
                                                holder.updatetaskdesc.getText().toString(),
                                                holder.autoCompleteTextView.getText().toString(),
                                                holder.datebtn.getText().toString(),
                                                ruidtoupdate,
                                                holder.timebtn.getText().toString()
                                        ));
                                        Gson gson = new Gson();
                                        String Json = gson.toJson(futuretasks);
                                        editor1.putString("futuredata" + Uid, Json);
                                        editor1.apply();
                                        arrayList.remove(editpos);
                                        notifyItemRemoved(editpos);
                                        ((MainActivity) context).removedata(newtask);
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "Task has been moved to future tasks", Toast.LENGTH_SHORT).show();
                                        editdialog.dismiss();
                                    } else {
                                        arrayList.set(editpos,new tasks(
                                                holder.updatetaskname.getText().toString(),
                                                holder.updatetaskdesc.getText().toString(),
                                                holder.autoCompleteTextView.getText().toString(),
                                                holder.datebtn.getText().toString(),
                                                ruidtoupdate,
                                                holder.timebtn.getText().toString()
                                        ));
                                        Gson gson1 = new Gson();
                                        String Json1 = gson1.toJson(arrayList);
                                        editor1.putString("prioritydata" + Uid, Json1);
                                        editor1.apply();
                                        holder.taskname.setText(newtask.getTaskname());
                                        notifyDataSetChanged();
                                        editdialog.dismiss();
                                    }
                                }
                                status = reminderhis.getBoolean("ischecked"+updatetasks.getRuid(),false);
                                if(status) {
                                    holder.clock.setImageResource(R.drawable.alarmclockoff);
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

                holder.delexpand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog dialog1= new Dialog(context);
                        dialog1.setContentView(R.layout.confirmdelete);
                        dialog1.setCanceledOnTouchOutside(false);
                        holder.delno = dialog1.findViewById(R.id.nodelete);
                        holder.delyes = dialog1.findViewById(R.id.yesdelete);
                        dialog1.show();
                        int adpaterpos1 = holder.getAdapterPosition();
                        holder.delyes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (adpaterpos1 != RecyclerView.NO_POSITION) {
                                    tasks delete = arrayList.get(adpaterpos1);
                                    arrayList.remove(adpaterpos1);
                                    ((MainActivity) context).removedata(delete);
                                    notifyItemRemoved(adpaterpos1);
                                    dialog1.dismiss();
                                    dialog.dismiss();
                                }
                            }
                        });
                        holder.delno.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();
                            }
                        });

                    }
                });
//                if(count==1)
//                {
//                    holder.clock1.setImageResource(R.drawable.alarmclock);
//                }
//                else
//                {
//                    holder.clock1.setImageResource(R.drawable.alarmclockoff);
//                }
//                holder.clock1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(count == 0) {
//                            holder.clock1.setImageResource(R.drawable.alarmclock);
//                            count=1;
//                        }
//                        else
//                        {
//                            holder.clock1.setImageResource(R.drawable.alarmclockoff);
//                            count=0;
//                        }
//                    }
//                });
                dialog.show();
                holder.closedialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        dialog.setCanceledOnTouchOutside(false);
                    }
                });


            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskname,date,time;
        ImageView viewtask,clock,clock1,edittask,deletetask,closedialog,delexpand;
        MaterialButton delno,delyes;
        CheckBox checkBox;
        TextView dialogtaskname,diealogtaskdesc,dialgtaskdate,dialogtasktime,dialogtaskcat;
        EditText updatetaskname,updatetaskdesc;
        MaterialButton datebtn,timebtn;
        AutoCompleteTextView autoCompleteTextView;
        MaterialButton addatask;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskname = itemView.findViewById(R.id.priorityname);
            date = itemView.findViewById(R.id.prioritydate);
            time = itemView.findViewById(R.id.prioritytime);
            viewtask = itemView.findViewById(R.id.viewtask1);
            clock = itemView.findViewById(R.id.remindertask);
            deletetask = itemView.findViewById(R.id.deletepriotitytask);

        }
    }
    private void loadDatanew() {
        Gson gson1 = new Gson();
        String json1 = todasyhistory.getString("futuredata" + Uid, null);
        Type type1 = new TypeToken<ArrayList<tasks>>() {
        }.getType();
        futuretasks = gson1.fromJson(json1, type1);
        if(futuretasks==null)
        {
            futuretasks = new ArrayList<>();
        }
        Gson gson2 = new Gson();
        String json2 = todasyhistory.getString("todaydata" + Uid, null);
        Type type2 = new TypeToken<ArrayList<tasks>>() {
        }.getType();
        todaystask = gson2.fromJson(json2, type2);
        if(todaystask==null)
        {
            todaystask = new ArrayList<>();
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

    }    private int uniqyereqcode(String uuid)
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
