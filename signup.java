package com.HkCodes.Todolist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.HkCodes.Todolist.Models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class signup extends AppCompatActivity {
TextView redirectlogin;
EditText firstname,lastname,emailid,password;
MaterialButton signupbtn;
ProgressBar progressBar;
ImageView rightarrow;
private FirebaseAuth mAuth;
FirebaseDatabase database;
SharedPreferences sharedPreferences;
SharedPreferences.Editor editor;
ArrayList<Users> arrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        redirectlogin = findViewById(R.id.signuphave);
        redirectlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirect = new Intent(signup.this,login.class);
                startActivity(redirect);
                finish();
            }
        });
        firstname = findViewById(R.id.signupfirst);
        lastname = findViewById(R.id.signuplast);
        emailid = findViewById(R.id.signupemail);
        password = findViewById(R.id.signuppassword);
        progressBar = findViewById(R.id.loadingsignup);
        signupbtn = findViewById(R.id.continuesignup);
        rightarrow = findViewById(R.id.rightarrowsignup);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        sharedPreferences = getSharedPreferences("userdetails",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(firstname.getText().toString().equals("")||lastname.getText().toString().equals("")||emailid.getText().toString().equals("")||password.getText().toString().equals(""))
            {
                Toast.makeText(signup.this, "Please Fill All The Fields", Toast.LENGTH_SHORT).show();
            }
            else
            {
               mAuth.createUserWithEmailAndPassword(emailid.getText().toString(),password.getText().toString())
                       .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if (task.isSuccessful()) {
                                   String Uid = mAuth.getUid();
                                   signupbtn.setVisibility(View.INVISIBLE);
                                   rightarrow.setVisibility(View.INVISIBLE);
                                   progressBar.setVisibility(View.VISIBLE);
                                   Users users = new Users(firstname.getText().toString(),lastname.getText().toString(),emailid.getText().toString());
                                   database.getReference().child("Users").child(Uid).setValue(users);
                                   editor.putString("Firstname",firstname.getText().toString());
                                   editor.putString("Lastname",lastname.getText().toString());
                                   editor.putString("Emailid",emailid.getText().toString());
                                   editor.putString("uniqueid",Uid);
                                   editor.apply();
                                   new Handler().postDelayed(new Runnable() {
                                       @Override
                                       public void run() {
                                           progressBar.setVisibility(View.INVISIBLE);
                                           Toast.makeText(signup.this, "Success", Toast.LENGTH_SHORT).show();
                                           Intent redirect1 = new Intent(signup.this, MainActivity.class);
                                           redirect1.putExtra("key","first");
                                           startActivity(redirect1);
                                           finish();
                                       }
                                   }, 5000);
                               }
                               else
                               {
                                   signupbtn.setVisibility(View.INVISIBLE);
                                   progressBar.setVisibility(View.VISIBLE);
                                   rightarrow.setVisibility(View.INVISIBLE);
                                   new Handler().postDelayed(new Runnable() {
                                       @Override
                                       public void run() {
                                           Toast.makeText(signup.this, "Error, Please Try Again Later", Toast.LENGTH_SHORT).show();
                                           signupbtn.setVisibility(View.VISIBLE);
                                           progressBar.setVisibility(View.INVISIBLE);
                                           rightarrow.setVisibility(View.VISIBLE);
                                       }
                                   }, 5000);
                               }
                               }
                           });
            }
            }
        });
//        if(mAuth.getCurrentUser()!=null)
//        {
//            Intent auto = new Intent(signup.this,MainActivity.class);
//            startActivity(auto);
//            finish();
//        }
    }
}