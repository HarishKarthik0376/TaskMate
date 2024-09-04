package com.HkCodes.Todolist;

import android.app.Dialog;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class login extends AppCompatActivity {
TextView redirectsignup,forgotpassword;
EditText emailid,password;
MaterialButton loginbtn;
ProgressBar progressBar;
    boolean nightmode;
ImageView loginrightarrow;
private FirebaseAuth mAuth;
FirebaseDatabase database;
Dialog dialog;
    String status;
    SharedPreferences sharedPreferences;
ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainlogin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        redirectsignup = findViewById(R.id.signupcreate);
        redirectsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirect = new Intent(login.this,signup.class);
                startActivity(redirect);
                finish();
            }
        });
        emailid = findViewById(R.id.loginemail);
        password = findViewById(R.id.loginpassword);
        loginbtn = findViewById(R.id.continuelogin);
        progressBar = findViewById(R.id.loadinglogin);
        loginrightarrow = findViewById(R.id.loginrightarrow);
        forgotpassword = findViewById(R.id.forgotpassword);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        sharedPreferences = getSharedPreferences("logindets",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        mAuth = FirebaseAuth.getInstance();
        status = sharedPreferences.getString("statusvalue","new");
        if(status.equals("new")) {
            mAuth.signOut();
            status = "current";
            editor.putString("statusvalue",status);
            editor.apply();
        }
        Dialog dialog1 = new Dialog(this);
        dialog1.setContentView(R.layout.dialogloading);
        dialog1.setCanceledOnTouchOutside(false);
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!emailid.getText().toString().equals((""))) {
                    mAuth.sendPasswordResetEmail(emailid.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialog1.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog1.dismiss();
                                    Toast.makeText(login.this, "Password Reset Link Sent!!", Toast.LENGTH_SHORT).show();
                                }
                            },4000);
                        }
                    });
                }
                else
                {
                    Toast.makeText(login.this, "Please Enter The Mail Id To  Reset Password!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailid.getText().toString().equals("")||password.getText().toString().equals(""))
                {
                    Toast.makeText(login.this, "Please Fill All The Fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.signInWithEmailAndPassword(emailid.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        loginbtn.setVisibility(View.INVISIBLE);
                                        loginrightarrow.setVisibility(View.INVISIBLE);
                                        progressBar.setVisibility(View.VISIBLE);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(login.this, "Success", Toast.LENGTH_SHORT).show();
                                                Intent redirect1 = new Intent(login.this, MainActivity.class);
                                                redirect1.putExtra("key","first");
                                                startActivity(redirect1);
                                                finishAffinity();
                                            }
                                        }, 5000);
                                    } else {
                                        loginbtn.setVisibility(View.INVISIBLE);
                                        loginrightarrow.setVisibility(View.INVISIBLE);
                                        progressBar.setVisibility(View.VISIBLE);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(login.this, "Error,Please Try Again Later", Toast.LENGTH_SHORT).show();
                                                loginbtn.setVisibility(View.VISIBLE);
                                                loginrightarrow.setVisibility(View.VISIBLE);
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        }, 5000);
                                    }
                                }
                            });
                }
            }
        });
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser refreshedUser = mAuth.getCurrentUser();
                        if (refreshedUser != null) {
                            Intent auto = new Intent(login.this,MainActivity.class);
                            auto.putExtra("key","first");
                            startActivity(auto);
                            finishAffinity();
                        }
                    }
                }
            });
        }
    }
}