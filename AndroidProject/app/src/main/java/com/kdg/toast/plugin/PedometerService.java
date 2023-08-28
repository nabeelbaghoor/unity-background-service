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

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import com.unity3d.player.UnityPlayer;

public class PedometerService extends Service {

    String TAG = "PEDOMETER";
    private static PluginCallback pluginCallback = null;
    private UnityPlayer mUnityPlayer;
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

    public void SetPluginCallback(PluginCallback callback) {
        Log.i(TAG, "SetPluginCallback: CALLED");
        pluginCallback = callback;
    }

    public void myPluginMethod() {
        if(pluginCallback == null) {
            Log.i(TAG, "myPluginMethod: pluginCallback is null");
            return;
        }

        Log.i(TAG, "myPluginMethod: CALLED");
        // Do something
        pluginCallback.onSuccess("onSuccess");
        // Do something horrible
        pluginCallback.onError("onError");
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: CREATED");

        // C# approach
        // /Users/nabeelbaghoor/Github Projects/unity-background-service/AndroidProject/ConsoleApplication2.exe
        try {
            // Replace with the actual path to your C# executable or DLL
            System.out.println("Current working directory: " + System.getProperty("user.dir"));
            String csharpBinary = "/Users/nabeelbaghoor/Github\\ Projects/unity-background-service/AndroidProject/app/ConsoleApplication2.exe";

            Process process = Runtime.getRuntime().exec(csharpBinary);
            int exitCode = process.waitFor();

            System.out.println("C# program exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // android java proxy approach
        myPluginMethod();

        // unity send message approach
        mUnityPlayer = new UnityPlayer(this);
        mUnityPlayer.UnitySendMessage("UnityServiceBridgeGameObject", "OnAndroidServiceMessage", "calling OnAndroidServiceMessage");

        // Android background
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Android sending message...");
                mUnityPlayer.UnitySendMessage("UnityServiceBridgeGameObject", "OnAndroidServiceMessage", "calling OnAndroidServiceMessage");
            }
        };

        // Schedule the task to run every 5 seconds (5000 milliseconds)
        timer.schedule(task, 0, 5000);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved: REMOVED");

        // Unity background
        myPluginMethod();
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
//        mUnityPlayer.quit();
        super.onDestroy();
        Log.i(TAG, "onDestroy: DESTROYED");
    }
}