package com.HkCodes.Todolist;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.HkCodes.Todolist.Adapters.completedadapter;
import com.HkCodes.Todolist.Adapters.pendingadapter;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class reminderpage extends AppCompatActivity {
    ArrayList<tasks> arrayList = new ArrayList<tasks>();
    SharedPreferences completedhistory;
    SharedPreferences.Editor editorpending;
    String[] items1 = {"Home","Work","School","Personal","All"};
    String item;
    ImageView filtertasks,closefilter;
    int countfilter;
    private FirebaseAuth mAuth;
    String Uid;
    ImageView goback;
    ArrayAdapter<String> arrayAdapter;
    ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reminderpage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.remindermain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        constraintLayout = findViewById(R.id.constraintnothingtoshow1);
        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getUid();
        completedhistory = getSharedPreferences("completedtasks",MODE_PRIVATE);
        editorpending = completedhistory.edit();
        loadDatanew();
        RecyclerView recyclerView = findViewById(R.id.recylerviewcompleted);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        completedadapter completedadapter = new completedadapter(arrayList,this);
        recyclerView.setAdapter(completedadapter);
        goback = findViewById(R.id.completedgoback);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirect = new Intent(reminderpage.this,MainActivity.class);
                redirect.putExtra("key","second");
               startActivity(redirect);
                finishAffinity();
            }
        });
        Dialog dialog3= new Dialog(reminderpage.this);
        dialog3.setContentView(R.layout.confirmdelete);
        ImageView deleteall = findViewById(R.id.deleteall);
        deleteall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialButton yes,no;
                TextView deletedtext;
                yes = dialog3.findViewById(R.id.yesdelete);
                no = dialog3.findViewById(R.id.nodelete);
                deletedtext = dialog3.findViewById(R.id.dialogdeletetext);
                deletedtext.setText("Are You Sure You Want To Delete All The Tasks?");
                deletedtext.setTextSize(12);
                dialog3.show();
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        arrayList.clear();
                        Gson gsonnew = new Gson();
                        String Jsoncompleted = gsonnew.toJson(arrayList);
                        editorpending.putString("completeddata" + Uid, Jsoncompleted);
                        editorpending.apply();
                        completedadapter.notifyDataSetChanged();
                        loadDatanew();
                        dialog3.dismiss();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog3.dismiss();
                    }
                });

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

                                } else {

                                }
                            } else {

                                            Intent auto = new Intent(reminderpage.this,MainActivity.class);
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
        String json = completedhistory.getString("completeddata" + Uid, null);
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
    public void removedata(tasks item) {
        if (item!=null) {
            arrayList.remove(item);
            Gson gson = new Gson();
            String json = gson.toJson(arrayList);
            editorpending.putString("completeddata" + Uid, json);
            editorpending.apply();
        } else {
            Toast.makeText(this, "Empty!!", Toast.LENGTH_SHORT).show();
        }
        if(arrayList.size()==0)
        {
            constraintLayout.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(reminderpage.this,MainActivity.class);
        intent.putExtra("key","second");
        startActivity(intent);
        finishAffinity();
    }


}