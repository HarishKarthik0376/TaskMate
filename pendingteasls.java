package com.HkCodes.Todolist;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
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

import com.HkCodes.Todolist.Adapters.futureadapter;
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

public class pendingteasls extends AppCompatActivity {
    ArrayList<tasks> arrayList = new ArrayList<tasks>();
    SharedPreferences pendinghistory;
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
        setContentView(R.layout.activity_pendingteasls);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        constraintLayout = findViewById(R.id.constraintnothingtoshow1);
        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getUid();
        pendinghistory = getSharedPreferences("pendingtasks",MODE_PRIVATE);
        editorpending = pendinghistory.edit();
        loadDatanew();
        RecyclerView recyclerView = findViewById(R.id.recyclerviewpending);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        pendingadapter pendingadapter = new pendingadapter(arrayList,this);
        recyclerView.setAdapter(pendingadapter);
        goback = findViewById(R.id.pendinggoback);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(pendingteasls.this,MainActivity.class);
                intent.putExtra("key","second");
                startActivity(intent);
                finishAffinity();
            }
        });
        Dialog dialog2 = new Dialog(this);
        dialog2.setContentView(R.layout.dialogtofiliter);
        dialog2.setCanceledOnTouchOutside(false);
        AutoCompleteTextView autoCompleteTextView1 = dialog2.findViewById(R.id.autofilter);
        MaterialButton filteryes = dialog2.findViewById(R.id.filteryes);
        filtertasks = findViewById(R.id.filterpendingtasks);
        filtertasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.show();
                arrayAdapter = new ArrayAdapter<String>(pendingteasls.this,R.layout.listitems,items1);
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
                            recyclerView.setAdapter(pendingadapter);
                            pendingadapter.notifyDataSetChanged();
                            dialog2.dismiss();
                        } else {
                            for (tasks task : arrayList) {
                                if (task.category.equals(cattofilter)) {
                                    filteredList.add(task);
                                    countfilter++;
                                }
                            }
                            if (countfilter > 0) {
                                futureadapter futureadapter1 = new futureadapter(filteredList,pendingteasls.this);
                                recyclerView.setAdapter(futureadapter1);
                                futureadapter1.notifyDataSetChanged();
                                dialog2.dismiss();
                            } else {
                                dialog2.dismiss();
                                Toast.makeText(pendingteasls.this, "None of the tasks in the list match this category", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
        closefilter = dialog2.findViewById(R.id.closefilterdialog);
        closefilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
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

                                            Intent auto = new Intent(pendingteasls.this,MainActivity.class);
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
        String json = pendinghistory.getString("pendingdata" + Uid, null);
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
            editorpending.putString("pendingdata" + Uid, json);
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
        Intent intent = new Intent(pendingteasls.this,MainActivity.class);
        intent.putExtra("key","second");
        startActivity(intent);
      finishAffinity();
    }
}