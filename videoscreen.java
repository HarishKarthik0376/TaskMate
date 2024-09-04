package com.HkCodes.Todolist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class videoscreen extends AppCompatActivity {
VideoView view;
FirebaseAuth mAuth;
    String status;
SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_videoscreen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("logindets",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        mAuth = FirebaseAuth.getInstance();
        status = sharedPreferences.getString("statusvalue","new");
        if(status.equals("new")) {
            mAuth.signOut();
            status = "current";
            editor.putString("statusvalue",status);
            editor.commit();
        }


        view = findViewById(R.id.videofile);
        view.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.v2);
        view.start();
        view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                view.start();
            }
        });
        view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                view.seekTo(0);
                view.start();
            }
        });
        MaterialButton loginbtn;
        loginbtn = findViewById(R.id.redirecttologinpage);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent redirect = new Intent(videoscreen.this, login.class);
                    startActivity(redirect);
                    finishAffinity();
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
                            Intent redirect = new Intent(videoscreen.this, MainActivity.class);
                            redirect.putExtra("key","first");
                            startActivity(redirect);
                            finishAffinity();
                        }
                    }
                    else
                    {
                        Toast.makeText(videoscreen.this, "User Logged Out!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
//        else
//        {
//            Toast.makeText(videoscreen.this, "User Logged Out!!", Toast.LENGTH_SHORT).show();
//        }

    }
}