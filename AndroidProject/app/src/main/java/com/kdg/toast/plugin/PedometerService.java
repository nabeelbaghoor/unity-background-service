package com.kdg.toast.plugin;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Timer;
import java.util.TimerTask;

public class PedometerService extends Service {

    String TAG = "PEDOMETER";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationChannel notificationChannel = new NotificationChannel(
                    "PedometerLib",
                    "Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void startNotification(){
        String input = "Counting your steps...";
        Intent notificationIntent = new Intent(this, Bridge.myActivity.getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, "PedometerLib")
                .setContentTitle("Background Walking Service")
                .setContentText(input)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(112, notification);
    }


    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: CREATED");
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("testing background...");
            }
        };

        // Schedule the task to run every 5 seconds (5000 milliseconds)
        timer.schedule(task, 0, 5000);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved: REMOVED");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: STARTED");
        createNotificationChannel();
        startNotification();
        super.onCreate();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: DESTROYED");
    }
}