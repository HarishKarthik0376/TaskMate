package com.HkCodes.Todolist;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.HkCodes.Todolist.Models.Users;
import com.HkCodes.Todolist.Models.tasks;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.client.http.FileContent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class profilepage extends AppCompatActivity {
    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_PICK_FILE = 2;
    private googlesync googleDriveServiceHelper;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor syncEditor;
    private SharedPreferences sharedforsync;
    private SharedPreferences.Editor sharedforsynceditor;
    SharedPreferences sharedPreferences1;
    SharedPreferences.Editor sharedprefeditor;
    private FirebaseAuth mAuth;
    Dialog loads;
    private FirebaseDatabase database;
    private TextView firstname, lastname, emailid;
    private ImageView goback;
    private MaterialButton logout, upload,download;
    private MaterialCardView learn, forgotpassword, explore, rate, synctocloud;
    private String mailreset;
    private SharedPreferences sharedPreferences2;
    private SharedPreferences.Editor editor2;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profilepage);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        firstname = findViewById(R.id.profilefirst);
        lastname = findViewById(R.id.profilelast);
        emailid = findViewById(R.id.profileemail);
        logout = findViewById(R.id.logout);
        goback = findViewById(R.id.gobacktohomefromprofile);
        sharedPreferences2 = getSharedPreferences("logindets", MODE_PRIVATE);
        editor2 = sharedPreferences2.edit();
        SharedPreferences sharedPreferences = getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        sharedPreferences1 = getSharedPreferences("logindets",MODE_PRIVATE);
        sharedprefeditor = sharedPreferences1.edit();
        sharedPreferences = getSharedPreferences("syncshare", MODE_PRIVATE);
        syncEditor = sharedPreferences.edit();
        sharedforsync = getSharedPreferences("todaystasks", MODE_PRIVATE);
        sharedforsynceditor = sharedforsync.edit();
        explore = findViewById(R.id.exploremoreapps);
        rate = findViewById(R.id.ratethisapp);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://play.google.com/store/apps/details?id=com.HkCodes.Todolist";
                Intent exp = new Intent(Intent.ACTION_VIEW);
                exp.setData(Uri.parse(url));
                if (exp.resolveActivity(getPackageManager()) != null) {
                    startActivity(exp);
                }

            }
        });
        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://play.google.com/store/apps/developer?id=HkCodes";
                Intent exp = new Intent(Intent.ACTION_VIEW);
                exp.setData(Uri.parse(url));
                if (exp.resolveActivity(getPackageManager()) != null) {
                    startActivity(exp);
                }

            }
        });
        String Uid = mAuth.getUid();
        if (Uid.equals(sharedPreferences.getString("uniqueid", ""))) {
            firstname.setText(sharedPreferences.getString("Firstname", ""));
            lastname.setText(sharedPreferences.getString("Lastname", ""));
            emailid.setText(sharedPreferences.getString("Emailid", ""));
        } else {
            database.getReference().child("Users").child(Uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users users = new Users();
                    String Firstname = snapshot.child("firstname").getValue(String.class);
                    String Lastname = snapshot.child("lastname").getValue(String.class);
                    String Emailid = snapshot.child("email").getValue(String.class);
                    firstname.setText(Firstname);
                    lastname.setText(Lastname);
                    emailid.setText(Emailid);
                    editor.putString("Firstname", Firstname);
                    editor.putString("Lastname", Lastname);
                    editor.putString("Emailid", Emailid);
                    editor.putString("uniqueid", Uid);
                    editor.apply();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        learn = findViewById(R.id.learnmoreabotthisapp);
        learn.setOnClickListener(v -> {
            String url = "https://hkcodes.kesug.com/taskmate.html";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        });

        forgotpassword = findViewById(R.id.forgotpasswordproffile);
        forgotpassword.setOnClickListener(v -> {
            Dialog dialog1 = new Dialog(profilepage.this);
            dialog1.setContentView(R.layout.dialogtoresetpassword);
            dialog1.setCanceledOnTouchOutside(false);
            MaterialButton sendresetmail = dialog1.findViewById(R.id.submitmail);
            EditText mailedittext = dialog1.findViewById(R.id.mailtextbox);
            dialog1.show();
            sendresetmail.setOnClickListener(v1 -> {
                mailreset = mailedittext.getText().toString();
                if (!mailreset.equals("")) {
                    if (mailreset.equals(emailid.getText().toString())) {
                        mAuth.sendPasswordResetEmail(mailreset).addOnCompleteListener(task -> {
                            dialog1.dismiss();
                            Dialog loaddialog = new Dialog(profilepage.this);
                            loaddialog.setContentView(R.layout.dialogloading);
                            loaddialog.setCanceledOnTouchOutside(false);
                            loaddialog.show();
                            new Handler().postDelayed(() -> {
                                loaddialog.dismiss();
                                Toast.makeText(profilepage.this, "Reset Mail Sent!!", Toast.LENGTH_SHORT).show();
                            }, 4000);
                        });
                    } else {
                        Toast.makeText(profilepage.this, "Invalid Email!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    dialog1.dismiss();
                }
            });
            ImageView closefilterdialog = dialog1.findViewById(R.id.closefilterdialog);
            closefilterdialog.setOnClickListener(v12 -> dialog1.dismiss());
        });

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(v -> showLogoutDialog());

//        synctocloud = findViewById(R.id.gdrivesync);
//        Dialog syncdialog = new Dialog(this);
//        syncdialog.setContentView(R.layout.dialogforsync);
//        loads = new Dialog(this);
//        loads.setContentView(R.layout.dialogloading);
//        loads.setCanceledOnTouchOutside(false);
//
//        synctocloud.setOnClickListener(v -> {
//            syncdialog.show();
//            upload = syncdialog.findViewById(R.id.uploadbtn);
//            upload.setOnClickListener(v1 -> {
//                uploadTasksToDrive();
//                syncdialog.dismiss();
//                loads.show();
//            });
//            download = syncdialog.findViewById(R.id.downloadbtn);
//            download.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    retrieveTaskFromDrive();
//                    syncdialog.dismiss();
//                }
//            });
//            requestSignIn();
//        });

        goback.setOnClickListener(v -> {
            Intent gobacktohome = new Intent(profilepage.this, MainActivity.class);
            gobacktohome.putExtra("key", "second");
            startActivity(gobacktohome);
            finishAffinity();
        });

        MobileAds.initialize(this, initializationStatus -> {
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

//        autoSignInCheck();
    }

    private void showLogoutDialog() {
        Dialog dialog1 = new Dialog(profilepage.this);
        dialog1.setContentView(R.layout.confirmlogout);
        dialog1.setCanceledOnTouchOutside(false);
        MaterialButton yes = dialog1.findViewById(R.id.yeslogout);
        MaterialButton no = dialog1.findViewById(R.id.nologout);
        dialog1.show();
        yes.setOnClickListener(v -> {
//            status = "returned";
//            sharedprefeditor.putString("statusvalue",status);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAuth.signOut();
                    editor2.clear();
                    editor2.apply();
                    Intent logout = new Intent(profilepage.this, videoscreen.class);
                    logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logout);
                    finish();
                }
            },1000);

        });
        no.setOnClickListener(v -> dialog1.dismiss());
    }


//
//    private void uploadTasksToDrive() {
//        String futureDataJson = sharedforsync.getString("futuredata" + mAuth.getUid(), "");
//        String todayDataJson = sharedforsync.getString("todaydata" + mAuth.getUid(), "");
//
//        if (futureDataJson.isEmpty() && todayDataJson.isEmpty()) {
//            Toast.makeText(this, "No tasks to upload!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        JSONObject combinedData = new JSONObject();
//        try {
//            combinedData.put("futuredata", new JSONArray(futureDataJson));
//            combinedData.put("todaydata", new JSONArray(todayDataJson));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        new UploadTasksToDriveTask().execute(combinedData.toString());
//    }
//
//    private class UploadTasksToDriveTask extends AsyncTask<String, Void, Void> {
//        @Override
//        protected Void doInBackground(String... params) {
//            try {
//                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(profilepage.this);
//                if (account == null) {
//                    return null;
//                }
//                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
//                        profilepage.this, Collections.singleton(DriveScopes.DRIVE_FILE));
//                credential.setSelectedAccount(account.getAccount());
//
//                Drive driveService = new Drive.Builder(
//                        GoogleNetHttpTransport.newTrustedTransport(),
//                        GsonFactory.getDefaultInstance(),
//                        credential
//                ).setApplicationName("TaskMate")
//                        .build();
//
//                File fileMetadata = new File();
//                fileMetadata.setName("TaskMateData.json");
//                fileMetadata.setMimeType("application/json");
//
//                java.io.File tempFile = new java.io.File(getCacheDir(), "TaskMateData.json");
//                try (FileWriter writer = new FileWriter(tempFile)) {
//                    writer.write(params[0]);
//                }
//
//                FileContent mediaContent = new FileContent("application/json", tempFile);
//
//                File file = driveService.files().create(fileMetadata, mediaContent)
//                        .setFields("id")
//                        .execute();
//
//                tempFile.delete();
//                loads.dismiss();
//
//            } catch (IOException | GeneralSecurityException e) {
//                e.printStackTrace();
//                loads.dismiss();
//            }
//            return null;
//        }
//    }
//
//
//    private void retrieveTaskFromDrive() {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("application/json");
//        startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
//    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//            } catch (ApiException e) {
//                e.printStackTrace();
//                Toast.makeText(this, "Google sign-in failed!", Toast.LENGTH_SHORT).show();
//            }
//        } else if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK) {
//            if (data != null) {
//                Uri uri = data.getData();
//                if (uri != null) {
//                    new DownloadTask().execute(uri);
//                }
//            }
//        }
//    }
//
//    private class DownloadTask extends AsyncTask<Uri, Void, String> {
//        @Override
//        protected String doInBackground(Uri... uris) {
//            Uri uri = uris[0];
//            StringBuilder stringBuilder = new StringBuilder();
//            try (InputStream inputStream = getContentResolver().openInputStream(uri);
//                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    stringBuilder.append(line);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return stringBuilder.toString();
//        }
//
//        @Override
//        protected void onPostExecute(String jsonString) {
//            if (jsonString != null && !jsonString.isEmpty()) {
//                SharedPreferences sharedforsync = getSharedPreferences("todaystasks", MODE_PRIVATE);
//                SharedPreferences.Editor sharedforsynceditor = sharedforsync.edit();
//
//                try {
//                    JSONObject combinedData = new JSONObject(jsonString);
//
//                    String futureDataJson = combinedData.getJSONArray("futuredata").toString();
//                    String existingFutureDataJson = sharedforsync.getString("futuredata" + FirebaseAuth.getInstance().getUid(), "");
//                    JSONArray futureDataArray = new JSONArray(existingFutureDataJson.isEmpty() ? "[]" : existingFutureDataJson);
//                    JSONArray newFutureDataArray = new JSONArray(futureDataJson);
//                    for (int i = 0; i < newFutureDataArray.length(); i++) {
//                        futureDataArray.put(newFutureDataArray.get(i));
//                    }
//                    sharedforsynceditor.putString("futuredata" + FirebaseAuth.getInstance().getUid(), futureDataArray.toString());
//
//                    String todayDataJson = combinedData.getJSONArray("todaydata").toString();
//                    String existingTodayDataJson = sharedforsync.getString("todaydata" + FirebaseAuth.getInstance().getUid(), "");
//                    JSONArray todayDataArray = new JSONArray(existingTodayDataJson.isEmpty() ? "[]" : existingTodayDataJson);
//                    JSONArray newTodayDataArray = new JSONArray(todayDataJson);
//                    for (int i = 0; i < newTodayDataArray.length(); i++) {
//                        todayDataArray.put(newTodayDataArray.get(i));
//                    }
//                    sharedforsynceditor.putString("todaydata" + FirebaseAuth.getInstance().getUid(), todayDataArray.toString());
//
//                    sharedforsynceditor.apply();
//                    Toast.makeText(profilepage.this, "Tasks downloaded and saved!", Toast.LENGTH_SHORT).show();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(profilepage.this, "Failed to merge tasks!", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Toast.makeText(profilepage.this, "Failed to retrieve tasks!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//
//
//    private void requestSignIn() {
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
//                .build();
//
//        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
//        Intent signInIntent = googleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
//    }
//
//
//    private void autoSignInCheck() {
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account != null) {
//        } else {
//            requestSignIn();
//        }
//    }
}
