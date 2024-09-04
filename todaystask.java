package com.HkCodes.Todolist;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.HkCodes.Todolist.Adapters.todaystaskadapter;
import com.HkCodes.Todolist.Models.tasks;
import com.HkCodes.Todolist.Models.tasks1;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class todaystask extends AppCompatActivity {
ImageView addatask,closetask,filtertasks,closefilter,gobacktodaytask;
TextView currentday;
Dialog dialog;
CheckBox checkBox;
String[] items = {"Home","Work","School","Personal"};
String[] items1 = {"Home","Work","School","Personal","All"};
String item;
String taskname1,taskdescription1,category,date,time;
ArrayList<tasks> arrayList;
boolean checked;
ArrayList<tasks> futuretasks;
ArrayList<tasks> prioritytasks;
SharedPreferences todasyhistory;
SharedPreferences.Editor editor1;
    SharedPreferences pendinghistory;
    SharedPreferences.Editor editorpending;
RecyclerView recyclerView;
AutoCompleteTextView autoCompleteTextView;
ArrayAdapter<String> arrayAdapter;
ConstraintLayout constraintLayout;
private FirebaseAuth mAuth;
public int hour,mins;
int countfilter=0;
String Uid;
Calendar calendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_todaystask);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getUid();
        Intent fetch = getIntent();
        String dayis = fetch.getStringExtra("currentday");
        currentday = findViewById(R.id.todaysday);
        currentday.setText(dayis);
        constraintLayout  = findViewById(R.id.constraintnothingtoshow);
        todasyhistory = getSharedPreferences("todaystasks",MODE_PRIVATE);
        editor1 = todasyhistory.edit();
        pendinghistory = getSharedPreferences("pendingtasks",MODE_PRIVATE);
        editorpending = pendinghistory.edit();
        recyclerView = findViewById(R.id.recyclerviewtodasytask);
        loadDatanew();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        todaystaskadapter todaystaskadapter = new todaystaskadapter(arrayList,this);
        dialog = new Dialog(todaystask.this);
        dialog.setContentView(R.layout.dialogtoaddtodaystask);
        dialog.setCanceledOnTouchOutside(false);
        addatask = findViewById(R.id.addtodaytask);
        autoCompleteTextView = dialog.findViewById(R.id.autocompletetext);
        checkBox = dialog.findViewById(R.id.checkbox);

        addatask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                arrayAdapter = new ArrayAdapter<String>(todaystask.this,R.layout.listitems,items);
                autoCompleteTextView.setAdapter(arrayAdapter);
                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        item = parent.getItemAtPosition(position).toString();
                    }
                });
                closetask = dialog.findViewById(R.id.closetodaystask);
                closetask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });









                checked = false;
                checkBox.setOnClickListener(new View.OnClickListener() {
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
                EditText taskname,taskdescription;
                MaterialButton dateselected,timeselected;
                taskname = dialog.findViewById(R.id.addatasknameedidttext);
                taskdescription = dialog.findViewById(R.id.addataskdescriptionedittext);
                dateselected = dialog.findViewById(R.id.datebtn);
                timeselected = dialog.findViewById(R.id.timebtn);
                MaterialButton submittask;
                submittask = dialog.findViewById(R.id.addtask);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH));
                calendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR));
                calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH));
                String onopenformat = "dd/MM/yy";
                SimpleDateFormat dateform = new SimpleDateFormat(onopenformat);
                dateselected.setText(dateform.format(calendar.getTime()));
                Calendar calendar1 = Calendar.getInstance();
                String modifiedtime = String.format(Locale.getDefault(), "%02d:%02d", calendar1.get(Calendar.HOUR_OF_DAY), calendar1.get(Calendar.MINUTE));
                time = modifiedtime;
                timeselected.setText(modifiedtime);
                DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        String format = "dd/MM/yy";
                        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                        dateselected.setText(dateFormat.format(calendar.getTime()));
                    }
                };
                dateselected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                todaystask.this,
                                date1,
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                        );

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
                timeselected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog timePickerDialog;
                        timePickerDialog = new TimePickerDialog(todaystask.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String modifiedtime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                                time = modifiedtime;
                                timeselected.setText(modifiedtime);
                            }
                        },hour,mins,true);
                        timePickerDialog.setTitle("Time of the task");
                        timePickerDialog.show();
                    }
                });
                submittask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        taskname1 = taskname.getText().toString();
                        taskdescription1 = taskdescription.getText().toString();
                        category = autoCompleteTextView.getText().toString();
                        date = dateselected.getText().toString();
                        time = timeselected.getText().toString();
                        //permissions::
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(todaystask.this, android.Manifest.permission.SCHEDULE_EXACT_ALARM)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(todaystask.this,
                                        new String[]{android.Manifest.permission.SCHEDULE_EXACT_ALARM}, 81);
                            }


                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(todaystask.this, android.Manifest.permission.POST_NOTIFICATIONS)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(todaystask.this,
                                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 82);
                            }
                        }



                        if(taskname1.equals("")||category.equals("")||date.equals("")&&time.equals("")) {
                            Toast.makeText(todaystask.this, "All fields are required", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            if(date.equals(dateform.format(calendar1.getTime()))) {
                                if(checked == false) {
                                    UUID uuid = UUID.randomUUID();
                                    String randomUUIDString = uuid.toString();
                                    tasks tasks = new tasks(taskname1, taskdescription1, category, date, randomUUIDString, time);
                                    arrayList.add(tasks);
                                    recyclerView.setAdapter(todaystaskadapter);
                                    constraintLayout.setVisibility(View.INVISIBLE);
                                    Gson gson = new Gson();
                                    String updatedJson = gson.toJson(arrayList);
                                    editor1.putString("todaydata" + Uid, updatedJson);
                                    editor1.apply();
                                    dialog.dismiss();
                                    taskname.setText("");
                                    taskdescription.setText("");
                                }
                                else
                                {
                                    UUID uuid = UUID.randomUUID();
                                    String randomUUIDString = uuid.toString();
                                    tasks tasks = new tasks(taskname1, taskdescription1, category, date, randomUUIDString, time);
                                    prioritytasks.add(tasks);
                                    Gson gson2 = new Gson();
                                    String updatedJson2 = gson2.toJson(prioritytasks);
                                    editor1.putString("prioritydata" + Uid, updatedJson2);
                                    editor1.apply();
                                    recyclerView.setAdapter(todaystaskadapter);
                                    Toast.makeText(todaystask.this, "Task added to priority list", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    taskname.setText("");
                                    taskdescription.setText("");
                                    checkBox.setChecked(false);
                                }
                            }
                            else
                            {
                                if(checked == false) {
                                    UUID uuid = UUID.randomUUID();
                                    String randomUUIDString = uuid.toString();
                                    tasks tasks = new tasks(taskname1, taskdescription1, category, date, randomUUIDString, time);
                                    futuretasks.add(tasks);
                                    Gson gson1 = new Gson();
                                    String updatedJson1 = gson1.toJson(futuretasks);
                                    editor1.putString("futuredata" + Uid, updatedJson1);
                                    editor1.apply();
                                    recyclerView.setAdapter(todaystaskadapter);
                                    Toast.makeText(todaystask.this, "Task added to future tasks", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                                else
                                {
                                    UUID uuid = UUID.randomUUID();
                                    String randomUUIDString = uuid.toString();
                                    tasks tasks = new tasks(taskname1, taskdescription1, category, date, randomUUIDString, time);
                                    if(prioritytasks.size()==0)
                                    {
                                        prioritytasks = new ArrayList<>();
                                        prioritytasks.add(tasks);
                                    }
                                    prioritytasks.add(tasks);
                                    Gson gson2 = new Gson();
                                    String updatedJson2 = gson2.toJson(prioritytasks);
                                    editor1.putString("prioritydata" + Uid, updatedJson2);
                                    editor1.apply();
                                    recyclerView.setAdapter(todaystaskadapter);
                                    Toast.makeText(todaystask.this, "Task added to priority list", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    taskname.setText("");
                                    taskdescription.setText("");
                                    checkBox.setChecked(false);
                                }
                            }
                        }
                    }
                });
            }
        });
        recyclerView.setAdapter(todaystaskadapter);
        filtertasks = findViewById(R.id.filltertodaytask);
        Dialog dialog1 = new Dialog(this);
        dialog1.setContentView(R.layout.dialogtofiliter);
        dialog1.setCanceledOnTouchOutside(false);
        AutoCompleteTextView autoCompleteTextView1 = dialog1.findViewById(R.id.autofilter);
        MaterialButton filteryes = dialog1.findViewById(R.id.filteryes);
        filtertasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.show();
                arrayAdapter = new ArrayAdapter<String>(todaystask.this,R.layout.listitems,items1);
                autoCompleteTextView1.setAdapter(arrayAdapter);
                autoCompleteTextView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String item1 = parent.getItemAtPosition(position).toString();
                    }
                });
                filteryes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countfilter = 0;
                        String cattofilter = autoCompleteTextView1.getText().toString();
                        ArrayList<tasks> filteredList = new ArrayList<>();
                        if (cattofilter.equals("All")) {
                            recyclerView.setAdapter(todaystaskadapter);
                            todaystaskadapter.notifyDataSetChanged();
                            dialog1.dismiss();
                        } else {
                            for (tasks task : arrayList) {
                                if (task.category.equals(cattofilter)) {
                                    filteredList.add(task);
                                    countfilter++;
                                }
                            }
                            if (countfilter > 0) {
                                todaystaskadapter todaystaskadapter1 = new todaystaskadapter(filteredList, todaystask.this);
                                recyclerView.setAdapter(todaystaskadapter1);
                                todaystaskadapter1.notifyDataSetChanged();
                                dialog1.dismiss();
                            } else {
                                dialog1.dismiss();
                                Toast.makeText(todaystask.this, "None of the tasks in the list match this category", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
        closefilter = dialog1.findViewById(R.id.closefilterdialog);
        closefilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });
        gobacktodaytask = findViewById(R.id.todayarrowback);
        gobacktodaytask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirect = new Intent(todaystask.this,MainActivity.class);
                redirect.putExtra("key","second");
                startActivity(redirect);
                finishAffinity();
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        String curruid = mAuth.getUid();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser refreshedUser = mAuth.getCurrentUser();
                                if (refreshedUser != null) {

                                }
                            } else {

                                            Intent auto = new Intent(todaystask.this,MainActivity.class);
                                            auto.putExtra("key","second");
                                            startActivity(auto);
                                            finishAffinity();

                            }
                        }
                    });
                }
                handler.postDelayed(this,1500);
            }
        },0);





    }
    public void removedata(tasks item) {
        if (item!=null) {
            arrayList.remove(item);
            Gson gson = new Gson();
            String json = gson.toJson(arrayList);
            editor1.putString("todaydata" + Uid, json);
            editor1.apply();
        } else {
            Toast.makeText(this, "Empty!!", Toast.LENGTH_SHORT).show();
        }
        if(arrayList.size()==0)
        {
            constraintLayout.setVisibility(View.VISIBLE);
        }
    }
    private void loadDatanew() {
        Gson gson = new Gson();
        String json = todasyhistory.getString("todaydata" + Uid, null);
        Type type = new TypeToken<ArrayList<tasks>>() {
        }.getType();
        arrayList = gson.fromJson(json, type);
        Gson gson1 = new Gson();
        String json1 = todasyhistory.getString("futuredata" + Uid, null);
        Type type1 = new TypeToken<ArrayList<tasks>>() {
        }.getType();
        futuretasks = gson1.fromJson(json1, type1);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            arrayList = new ArrayList<>();
        }
        if(arrayList.size() == 0)
        {
            constraintLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            constraintLayout.setVisibility(View.INVISIBLE);
        }
        if (futuretasks == null) {
            futuretasks = new ArrayList<>();
        }
        Gson gson3 = new Gson();
        String json3 = todasyhistory.getString("prioritydata" + Uid, null);
        Type type3 = new TypeToken<ArrayList<tasks>>() {
        }.getType();
        prioritytasks = gson3.fromJson(json3, type3);
        if (prioritytasks == null) {
            prioritytasks = new ArrayList<>();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(todaystask.this,MainActivity.class);
        intent.putExtra("key","second");
        startActivity(intent);
        finishAffinity();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 81:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Alarm permission granted", Toast.LENGTH_SHORT).show();
                } else {

                }
                break;
            case 82:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT).show();
                } else {

                }
                break;
        }
    }
}