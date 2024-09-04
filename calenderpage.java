package com.HkCodes.Todolist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.HkCodes.Todolist.Adapters.calenderadapter;
import com.HkCodes.Todolist.Adapters.dateadapter;
import com.HkCodes.Todolist.Models.tasks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class calenderpage extends AppCompatActivity {
    ArrayList<tasks> todaystasks;
    ArrayList<tasks> futuretasks;
    ArrayList<tasks> prioritytasks;
    ArrayList<tasks> pendingtasks;
    ArrayList<tasks> calendarlist = new ArrayList<>();
    CalendarView calendarView;
    RecyclerView recyclerView,daterecycler;
    SharedPreferences todasyhistory;
    SharedPreferences.Editor editor1;
    private FirebaseAuth mAuth;
    String curruid;
    public int hour, mins;
    int countfilter = 0;
    String Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calenderpage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        MaterialButton calendarback;
        calendarback = findViewById(R.id.calendarback);
        calendarback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent redirect = new Intent(calenderpage.this,MainActivity.class);
                redirect.putExtra("key","second");
                startActivity(redirect);
                finishAffinity();

            }
        });
        calendarView = findViewById(R.id.calendarview);
        recyclerView = findViewById(R.id.calendarrecyclerview);
        daterecycler = findViewById(R.id.datesrecyler);
        todasyhistory = getSharedPreferences("todaystasks", MODE_PRIVATE);
        editor1 = todasyhistory.edit();
        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getUid();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager linearLayout1 = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        daterecycler.setLayoutManager(linearLayout1);
        calenderadapter calenderadapter = new calenderadapter(calendarlist, this);
        dateadapter dateadapter = new dateadapter(calendarlist,this);
        loadDatanew();
        Calendar calendar = Calendar.getInstance();
        for (tasks task : todaystasks) {
            if (task.getDate().substring(0, 2).equals(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)))) {
                calendarlist.add(task);
            }
        }
        for (tasks task : prioritytasks) {
            if (task.getDate().substring(0, 2).equals(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)))) {
                calendarlist.add(task);
            }
        }
        recyclerView.setAdapter(calenderadapter);
        daterecycler.setAdapter(dateadapter);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                loadDatanew();
                calendarlist.clear();
                String selectedDay = String.format("%02d", dayOfMonth);
                String selectedMonth = String.format("%02d", month + 1);

                for (tasks task : todaystasks) {
                    if (task.getDate().substring(0, 2).equals(selectedDay) && task.getDate().substring(3, 5).equals(selectedMonth)) {
                        calendarlist.add(task);
                    }
                }
                for (tasks task : pendingtasks) {
                    if (task.getDate().substring(0, 2).equals(selectedDay) && task.getDate().substring(3, 5).equals(selectedMonth)) {
                        calendarlist.add(task);
                    }
                }
                for (tasks task : futuretasks) {
                    if (task.getDate().substring(0, 2).equals(selectedDay) && task.getDate().substring(3, 5).equals(selectedMonth)) {
                        calendarlist.add(task);
                    }
                }
                for (tasks task : prioritytasks) {
                    if (task.getDate().substring(0, 2).equals(selectedDay) && task.getDate().substring(3, 5).equals(selectedMonth)) {
                        calendarlist.add(task);
                    }
                }
                recyclerView.setAdapter(calenderadapter);
                daterecycler.setAdapter(dateadapter);
            }
        });
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

                                            Intent auto = new Intent(calenderpage.this,MainActivity.class);
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

    private void loadDatanew() {
        Gson gson = new Gson();
        String json = todasyhistory.getString("todaydata" + Uid, null);
        Type type = new TypeToken<ArrayList<tasks>>() {
        }.getType();
        todaystasks = gson.fromJson(json, type);
        if (todaystasks == null) {
            todaystasks = new ArrayList<>();
        }
        String json1 = todasyhistory.getString("futuredata" + Uid, null);
        Type type1 = new TypeToken<ArrayList<tasks>>() {
        }.getType();
        futuretasks = gson.fromJson(json1, type1);
        if (futuretasks == null) {
            futuretasks = new ArrayList<>();
        }
        String json3 = todasyhistory.getString("prioritydata" + Uid, null);
        Type type3 = new TypeToken<ArrayList<tasks>>() {
        }.getType();
        prioritytasks = gson.fromJson(json3, type3);
        if (prioritytasks == null) {
            prioritytasks = new ArrayList<>();
        }
        String json4 = todasyhistory.getString("pendingdata" + Uid, null);
        Type type4 = new TypeToken<ArrayList<tasks>>() {
        }.getType();
        pendingtasks = gson.fromJson(json4, type4);
        if (pendingtasks == null) {
            pendingtasks = new ArrayList<>();
        }
    }
}