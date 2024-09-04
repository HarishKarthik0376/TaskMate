package com.HkCodes.Todolist;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;

import com.HkCodes.Todolist.Models.tasks;

import java.util.ArrayList;
import java.util.Random;

public class reciever extends BroadcastReceiver {
    private static final String CHANNEL_ID = "testingid";
    private static final int notificationid = 81;
    int notifid;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String newtype;
    String description;
    String reminderId;
    ArrayList<tasks> arrayList;
    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences = context.getSharedPreferences("reminderhis", context.MODE_PRIVATE);
        reminderId = intent.getStringExtra("keyactual");
        String messageTypeKey = reminderId + "title";
        String messageDescriptionKey;
        newtype = sharedPreferences.getString(messageTypeKey, null);
        description = "Task Notification";
        if (newtype != null && description != null) {
            tasks tasks = new tasks();
            tasks.setTaskname(tasks.getTaskname());
            Intent newintent = new Intent(context, videoscreen.class);
            newintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent onnotificationclick = PendingIntent.getActivity(context, 0, newintent, PendingIntent.FLAG_IMMUTABLE);
            Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.goldencal, null);
            if (drawable != null) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap largeIcon = bitmapDrawable.getBitmap();
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(R.drawable.alarmclock)
                        .setContentText(newtype)
                        .setSubText(description)
                        .setChannelId(CHANNEL_ID)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
                builder.setContentIntent(onnotificationclick);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                Random randome = new Random();
                notifid = randome.nextInt();
                notificationManager.notify(notifid, builder.build());
            }
            else
            {
                Toast.makeText(context, "drawable error", Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
//            Toast.makeText(context, "error!!", Toast.LENGTH_SHORT).show();
        }

    }
}
