package com.HkCodes.Todolist;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
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

import com.HkCodes.Todolist.Adapters.dialyadapter;
import com.HkCodes.Todolist.Adapters.todaystaskadapter;
import com.HkCodes.Todolist.Models.tasks;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class dailyreminders extends AppCompatActivity {
    ImageView addatask,closetask,filtertasks,closefilter,gobacktodaytask;
    Dialog dialog;
    String[] items = {"Home","Work","School","Personal"};
    String[] items1 = {"Home","Work","School","Personal","All"};
    String item;
    String taskname1,taskdescription1,category,date,time;
    ArrayList<tasks> arrayList;
    SharedPreferences todasyhistory;
    SharedPreferences.Editor editor1;
    RecyclerView recyclerView;
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> arrayAdapter;
    ConstraintLayout constraintLayout;
    private FirebaseAuth mAuth;
    public int hour,mins;
    String Uid;
    int countfilter=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dailyreminders);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getUid();
        constraintLayout  = findViewById(R.id.constraintnothingtoshow);
        todasyhistory = getSharedPreferences("todaystasks",MODE_PRIVATE);
        editor1 = todasyhistory.edit();
        recyclerView = findViewById(R.id.recyclerdialy);
        loadDatanew();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        dialyadapter dialyadapter = new dialyadapter(arrayList,this);
        recyclerView.setAdapter(dialyadapter);
        dialog = new Dialog(dailyreminders.this);
        dialog.setContentView(R.layout.dialogfordialy);
        dialog.setCanceledOnTouchOutside(false);
        addatask = findViewById(R.id.adddailytask);
        autoCompleteTextView = dialog.findViewById(R.id.autocompletetext);
        addatask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                arrayAdapter = new ArrayAdapter<String>(dailyreminders.this, R.layout.listitems, items);
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
                EditText taskname,taskdescription;
                MaterialButton timeselected;
                taskname = dialog.findViewById(R.id.addatasknameedidttext);
                taskdescription = dialog.findViewById(R.id.addataskdescriptionedittext);
                timeselected = dialog.findViewById(R.id.timebtn);
                MaterialButton submittask;
                submittask = dialog.findViewById(R.id.addtask);
                Calendar calendar1 = Calendar.getInstance();
                String modifiedtime = String.format(Locale.getDefault(), "%02d:%02d", calendar1.get(Calendar.HOUR_OF_DAY), calendar1.get(Calendar.MINUTE));
                time = modifiedtime;
                timeselected.setText(modifiedtime);
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
                        timePickerDialog = new TimePickerDialog(dailyreminders.this, new TimePickerDialog.OnTimeSetListener() {
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
                        time = timeselected.getText().toString();
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(dailyreminders.this, android.Manifest.permission.SCHEDULE_EXACT_ALARM)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(dailyreminders.this,
                                        new String[]{android.Manifest.permission.SCHEDULE_EXACT_ALARM}, 81);
                            }


                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(dailyreminders.this, android.Manifest.permission.POST_NOTIFICATIONS)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(dailyreminders.this,
                                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 82);
                            }
                        }



                        if(taskname1.equals("")||category.equals("")||time.equals("")) {
                            Toast.makeText(dailyreminders.this, "All fields are required", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                                    UUID uuid = UUID.randomUUID();
                                    String randomUUIDString = uuid.toString();
                                    tasks tasks = new tasks(taskname1, taskdescription1, category, "0", randomUUIDString, time);
                                    arrayList.add(tasks);
                                    recyclerView.setAdapter(dialyadapter);
                                    constraintLayout.setVisibility(View.INVISIBLE);
                                    Gson gson = new Gson();
                                    String updatedJson = gson.toJson(arrayList);
                                    editor1.putString("dailydata" + Uid, updatedJson);
                                    editor1.apply();
                                    dialog.dismiss();
                                    taskname.setText("");
                                    taskdescription.setText("");
                        }
                    }
                });
            }
        });
        recyclerView.setAdapter(dialyadapter);
        filtertasks = findViewById(R.id.filtterdialytask);
        Dialog dialog1 = new Dialog(this);
        dialog1.setContentView(R.layout.dialogtofiliter);
        dialog1.setCanceledOnTouchOutside(false);
        AutoCompleteTextView autoCompleteTextView1 = dialog1.findViewById(R.id.autofilter);
        MaterialButton filteryes = dialog1.findViewById(R.id.filteryes);
        filtertasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.show();
                arrayAdapter = new ArrayAdapter<String>(dailyreminders.this,R.layout.listitems,items1);
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
                            recyclerView.setAdapter(dialyadapter);
                            dialyadapter.notifyDataSetChanged();
                            dialog1.dismiss();
                        } else {
                            for (tasks task : arrayList) {
                                if (task.category.equals(cattofilter)) {
                                    filteredList.add(task);
                                    countfilter++;
                                }
                            }
                            if (countfilter > 0) {
                                dialyadapter dialyadapter = new dialyadapter(filteredList, dailyreminders.this);
                                recyclerView.setAdapter(dialyadapter);
                                dialyadapter.notifyDataSetChanged();
                                dialog1.dismiss();
                            } else {
                                dialog1.dismiss();
                                Toast.makeText(dailyreminders.this, "None of the tasks in the list match this category", Toast.LENGTH_SHORT).show();
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
        gobacktodaytask = findViewById(R.id.dialyback);
        gobacktodaytask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirect = new Intent(dailyreminders.this,MainActivity.class);
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

                                Intent auto = new Intent(dailyreminders.this,MainActivity.class);
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
            editor1.putString("dailydata" + Uid, json);
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
        String json = todasyhistory.getString("dailydata" + Uid, null);
        Type type = new TypeToken<ArrayList<tasks>>() {
        }.getType();
        arrayList = gson.fromJson(json, type);
        if (arrayList == null) {
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(dailyreminders.this,MainActivity.class);
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