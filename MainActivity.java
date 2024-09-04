package com.HkCodes.Todolist;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.HkCodes.Todolist.Adapters.priority;
import com.HkCodes.Todolist.Models.Users;
import com.HkCodes.Todolist.Models.tasks;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ImageView profileicon, notificationicon, grettingimage;
    TextView greetingtext, nametext, daytext, datettext, monthtext, yeartext, timetext;
    TextView todaystasks, totaltasks, taskcompletedprogress, pendingtasks, futuretasks1;
    CardView cardView1, cardView2, cardView3, cardView4,cardView5;
    Calendar calendar = Calendar.getInstance();
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    int currentcount, updadtedcount;
    int arraycount;
    LinearLayout linearLayout;
    int count = 0;
    Dialog dialog;
    SharedPreferences sharedPreferences, todasyhistory, pendinghistory;
    SharedPreferences.Editor editor, editor1, editorpending;
    ArrayList<tasks> arrayList = new ArrayList<>();
    String Uid;
    ArrayList<tasks> pendingtaskslist = new ArrayList<>();
    RecyclerView recyclerView;
    int dayoftheweek;
    ArrayList<tasks> futuretasks;
    ArrayList<tasks> prioritytasks;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        daytext = findViewById(R.id.daytext);
        datettext = findViewById(R.id.datetext);
        yeartext = findViewById(R.id.yeartext);
        monthtext = findViewById(R.id.monthtext);
        timetext = findViewById(R.id.timetext);
        grettingimage = findViewById(R.id.imagetochange);
        greetingtext = findViewById(R.id.greetingtext);
        nametext = findViewById(R.id.nametext);
        profileicon = findViewById(R.id.profileicon);
        linearLayout  = findViewById(R.id.visnovis);
        notificationicon = findViewById(R.id.notificationicon);



        notificationicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cal = new Intent(MainActivity.this,calenderpage.class);
                startActivity(cal);

            }
        });
        profileicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openprofile = new Intent(MainActivity.this, profilepage.class);
                startActivity(openprofile);
            }
        });
        todasyhistory = getSharedPreferences("todaystasks", MODE_PRIVATE);
        editor1 = todasyhistory.edit();
        pendinghistory = getSharedPreferences("pendingtasks", MODE_PRIVATE);
        editorpending = pendinghistory.edit();
        String dateformat1 = "dd/MM/yy";
        SimpleDateFormat dateform2 = new SimpleDateFormat(dateformat1);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        sharedPreferences = getSharedPreferences("userdetails", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Uid = mAuth.getUid();
        priorityload();
        recyclerView = findViewById(R.id.horizontalrecyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        priority priority = new priority(prioritytasks, this);
        database.getReference().child("Users").child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = new Users();
                String fname = snapshot.child("firstname").getValue(String.class);
//                String lname = snapshot.child("lastname").getValue(String.class);
                if (sharedPreferences.getString("Firstname", "").equals("")) {
//                    nametext.setText(fname + " " + lname);
                    editor.putString("Firstname", fname);
//                    editor.putString("Lastname", lname);
                    editor.apply();
                } else {
                    String nameinshared = sharedPreferences.getString("Firstname", null);
//                    String lastnameonshared = sharedPreferences.getString("Lastname", null);
                    if (nameinshared.equals(fname)) {
                        nametext.setText(nameinshared);
                    } else {
                        nametext.setText(fname);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
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

                                } else {

                                }
                            } else {
                                Intent auto = new Intent(MainActivity.this,videoscreen.class);
                                startActivity(auto);
                                finishAffinity();


                            }
                        }

                    });
                }
                handler2.postDelayed(this,1500);
            }
        },0);





        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Calendar calendar1 = Calendar.getInstance();
                int hourOfDay = calendar1.get(Calendar.HOUR_OF_DAY);
                int minute = calendar1.get(Calendar.MINUTE);
                String currenttime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                timetext.setText(currenttime);
                int ampm = calendar1.get(Calendar.AM_PM);
                if (ampm == 1) {
                    if (calendar1.get(Calendar.HOUR_OF_DAY) >= 16) {
                        greetingtext.setText("Good Evening,");
                    } else {
                        greetingtext.setText("Good Morning,");
                    }
                    if (calendar1.get(Calendar.HOUR_OF_DAY) >= 18) {
                        grettingimage.setImageResource(R.drawable.newnight);
                        monthtext.setTextColor(getResources().getColorStateList(R.color.navygreen1));
                        timetext.setTextColor(getResources().getColorStateList(R.color.white));
                    } else {
                        grettingimage.setImageResource(R.drawable.backgroundimg);
                        monthtext.setTextColor(getResources().getColorStateList(R.color.lightyellow));
                        timetext.setTextColor(getResources().getColorStateList(R.color.background_black));

                    }

                } else {
                    greetingtext.setText("Good Morining,");
                    grettingimage.setImageResource(R.drawable.backgroundimg);
                    monthtext.setTextColor(getResources().getColorStateList(R.color.lightyellow));
                    timetext.setTextColor(getResources().getColorStateList(R.color.background_black));
                }
                int todaysdate = calendar1.get(Calendar.DATE);
                if (todaysdate < 10) {
                    datettext.setText("0" + String.valueOf(todaysdate));
                } else {
                    datettext.setText(String.valueOf(todaysdate));
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM");
                calendar1.set(Calendar.MONTH, calendar1.get(Calendar.MONTH));
                String monthname = simpleDateFormat.format(calendar1.getTime());
                monthtext.setText(monthname);
                yeartext.setText(String.valueOf(calendar1.get(Calendar.YEAR)).substring(2));
                int dayofthemonth = calendar1.get(Calendar.DAY_OF_WEEK);
                if (dayofthemonth == 1) {
                    daytext.setText("Sunday");
                } else if (dayofthemonth == 2) {
                    daytext.setText("Monday");
                } else if (dayofthemonth == 3) {
                    daytext.setText("Tuesday");
                } else if (dayofthemonth == 4) {
                    daytext.setText("Wednesday");
                } else if (dayofthemonth == 5) {
                    daytext.setText("Thursday");
                } else if (dayofthemonth == 6) {
                    daytext.setText("Friday");
                } else if (dayofthemonth == 7) {
                    daytext.setText("Saturday");
                }
                handler.postDelayed(this, 1000);
            }
        }, 0);
        todaystasks = findViewById(R.id.todaystasksnumber);
        pendingtasks = findViewById(R.id.pendingtasksnumber);
        futuretasks1 = findViewById(R.id.futuretasksnumber);
        totaltasks = findViewById(R.id.totaltasknumber);
        String onopenformat = "dd/MM/yy";
        SimpleDateFormat dateform = new SimpleDateFormat(onopenformat);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadDatanew();
                Iterator<tasks> futureIterator = futuretasks.iterator();
                while (futureIterator.hasNext()) {
                    tasks task = futureIterator.next();
                    Calendar calendar1 = Calendar.getInstance();
                    if (task.getDate().equals(dateform.format(calendar1.getTime()))) {
                        arrayList.add(task);
                        futureIterator.remove();
                        Gson gson = new Gson();
                        String json = gson.toJson(futuretasks);
                        String json1 = gson.toJson(arrayList);
                        editor1.putString("todaydata" + Uid, json1);
                        editor1.apply();
                        editor1.putString("futuredata" + Uid, json);
                        editor1.apply();
                    }
                }

                Iterator<tasks> pendingIterator = arrayList.iterator();
                while (pendingIterator.hasNext()) {
                    tasks task = pendingIterator.next();
                    Calendar pendclaender = Calendar.getInstance();
                    if (task.date.equals(dateform2.format(pendclaender.getTime()))) {
                        continue;
                    } else {
                        if (Integer.parseInt(task.date.substring(0, 2)) < Integer.parseInt(dateform2.format(pendclaender.getTime()).substring(0, 2)) ||
                                Integer.parseInt(task.date.substring(3, 5)) < Integer.parseInt(dateform2.format(pendclaender.getTime()).substring(3, 5))) {
                            pendingtaskslist.add(task);
                            Gson gson = new Gson();
                            String updatedgson = gson.toJson(pendingtaskslist);
                            editorpending.putString("pendingdata" + Uid, updatedgson);
                            editorpending.apply();
                            pendingIterator.remove();
                            String updatedgson2 = gson.toJson(arrayList);
                            editor1.putString("todaydata" + Uid, updatedgson2);
                            editor1.apply();
                        }
                    }
                }
                todaystasks.setText(String.valueOf(arrayList.size()));
                pendingtasks.setText(String.valueOf(pendingtaskslist.size()));
                futuretasks1.setText(String.valueOf(futuretasks.size()));
                totaltasks.setText(String.valueOf(arrayList.size() + pendingtaskslist.size() + futuretasks.size()+prioritytasks.size()));
                new Handler().postDelayed(this, 1000);
            }
        }, 0);
        dayoftheweek = calendar.get(Calendar.DAY_OF_WEEK);
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                Calendar calendar2 = Calendar.getInstance();
                if (dayoftheweek != calendar2.get(Calendar.DAY_OF_WEEK)) {
                    dayoftheweek = calendar2.get(Calendar.DAY_OF_WEEK);
                    todaystasks.setText("0");

                }
                handler1.postDelayed(this, 1000);
            }
        }, 0);

        cardView1 = findViewById(R.id.cardview3);
        cardView2 = findViewById(R.id.cardview4);
        cardView3 = findViewById(R.id.cardview5);
        cardView4 = findViewById(R.id.cardview6);
        cardView5 = findViewById(R.id.dailycard);
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent todaystask = new Intent(MainActivity.this, todaystask.class);
                todaystask.putExtra("currentday", daytext.getText().toString().toUpperCase() + "'S");
                startActivity(todaystask);
            }
        });
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent todaystask = new Intent(MainActivity.this, futuretasks.class);
                todaystask.putExtra("currentday", daytext.getText().toString().toUpperCase() + "'S");
                startActivity(todaystask);
            }
        });
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent todaystask = new Intent(MainActivity.this, pendingteasls.class);
                startActivity(todaystask);
            }
        });
        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent todaystask = new Intent(MainActivity.this, reminderpage.class);
                startActivity(todaystask);
            }
        });
        cardView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent todaystask = new Intent(MainActivity.this, dailyreminders.class);
                startActivity(todaystask);
            }
        });
        Intent intent = getIntent();
        String keyvalue = intent.getStringExtra("key");
        ConstraintLayout constraintLayout;
        constraintLayout = findViewById(R.id.main);
        constraintLayout.setVisibility(View.INVISIBLE);
        if(keyvalue.equals("first")) {
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialogloading);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    constraintLayout.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            }, 2000);
        }
        else
        {
            constraintLayout.setVisibility(View.VISIBLE);
        }

        recyclerView.setAdapter(priority);
        if(mAuth.getCurrentUser()==null)
        {
            Intent auto = new Intent(MainActivity.this,videoscreen.class);
            startActivity(auto);
            finishAffinity();

        }

    }
    public void removedata(tasks item) {
        if (item!=null) {
            prioritytasks.remove(item);
            Gson gson4 = new Gson();
            String json4 = gson4.toJson(prioritytasks);
            editor1.putString("prioritydata" + Uid, json4);
            editor1.apply();
        } else {

        }
        if(prioritytasks.size() == 0)
        {
            linearLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            linearLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void loadDatanew() {
        Gson gson = new Gson();
        String json = todasyhistory.getString("todaydata" + Uid, null);
        if (todasyhistory.getString("todaydata" + Uid, null) == null) {

        } else {
            Type type = new TypeToken<ArrayList<tasks>>() {
            }.getType();
            arrayList = gson.fromJson(json, type);
        }
        Gson gson1 = new Gson();
        String json1 = todasyhistory.getString("futuredata" + Uid, null);
        Type type1 = new TypeToken<ArrayList<tasks>>() {
        }.getType();
        futuretasks = gson1.fromJson(json1, type1);
        if (futuretasks == null) {
            futuretasks = new ArrayList<>();
        }
        Gson gson2 = new Gson();
        String json2 = pendinghistory.getString("pendingdata" + Uid, null);
        Type type2 = new TypeToken<ArrayList<tasks>>() {
        }.getType();
        pendingtaskslist = gson2.fromJson(json2, type2);
        if (pendingtaskslist == null) {
            pendingtaskslist = new ArrayList<>();
        }

    }

    private void priorityload() {
        Gson gson3 = new Gson();
        String json3 = todasyhistory.getString("prioritydata" + Uid, null);
        Type type3 = new TypeToken<ArrayList<tasks>>() {
        }.getType();
        prioritytasks = gson3.fromJson(json3, type3);
        if (prioritytasks == null) {
            prioritytasks = new ArrayList<>();
        }
        if(prioritytasks.size() == 0)
        {
            linearLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            linearLayout.setVisibility(View.INVISIBLE);
        }

    }

}
