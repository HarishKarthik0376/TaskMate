package com.HkCodes.Todolist;

import android.app.AlarmManager;
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

import java.util.Calendar;
import java.util.Random;

public class reciever1 extends BroadcastReceiver {
    private static final String CHANNEL_ID = "testingid";
    SharedPreferences sharedPreferences;
    String reminderId;
    String title;
    String description;
    int notifid;

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences = context.getSharedPreferences("reminderhis", Context.MODE_PRIVATE);
        reminderId = intent.getStringExtra("keyactual");
        String messageTypeKey = reminderId + "title";
        title = sharedPreferences.getString(messageTypeKey, null);
        description = "Daily Reminder";

        if (title != null) {
            Intent newIntent = new Intent(context, videoscreen.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent onNotificationClick = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_IMMUTABLE);
            Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.goldencal, null);

            if (drawable != null) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap largeIcon = bitmapDrawable.getBitmap();
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(R.drawable.alarmclock)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setChannelId(CHANNEL_ID)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(onNotificationClick);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                Random random = new Random();
                notifid = random.nextInt();
                notificationManager.notify(notifid, builder.build());

                // Cancel existing alarm
                PendingIntent existingIntent = PendingIntent.getBroadcast(context, notifid, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
                if (existingIntent != null) {
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(existingIntent);
                }

                // Reschedule for next day
                Intent rescheduleIntent = new Intent(context, reciever1.class);
                rescheduleIntent.putExtra("keyactual", reminderId);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notifid, rescheduleIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                long alarmTimeMillis = getNextAlarmTimeMillis();
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent);
            } else {
                Toast.makeText(context, "Drawable error", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "No reminder data found", Toast.LENGTH_SHORT).show();
        }
    }

    private long getNextAlarmTimeMillis() {
        int hour = sharedPreferences.getInt("reminderHour", 0);
        int minute = sharedPreferences.getInt("reminderMinute", 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long alarmTimeMillis = calendar.getTimeInMillis();
        // If the time is in the past, schedule it for the next day
        if (System.currentTimeMillis() > alarmTimeMillis) {
            alarmTimeMillis += 24 * 60 * 60 * 1000;
        }

        return alarmTimeMillis;
    }
}