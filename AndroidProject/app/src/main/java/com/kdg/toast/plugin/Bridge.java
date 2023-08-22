package com.kdg.toast.plugin;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public final class Bridge extends Application {
    static Activity myActivity;
    static Context appContext;
    public static final Intent[] POWERMANAGER_INTENTS = new Intent[]{
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),
            new Intent().setComponent(new ComponentName("com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity"))
    };

    public static void ReceiveActivityInstance(Activity tempActivity) {
        myActivity = tempActivity;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            String[] perms = new String[1];
            perms[0] = Manifest.permission.ACTIVITY_RECOGNITION;
            if (ContextCompat.checkSelfPermission(myActivity, Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.i("PEDOMETER", "Permision isnt granted!");
                ActivityCompat.requestPermissions(Bridge.myActivity,
                        perms,
                        1);
            }
        }
    }

    public static void StartService() {
        if (myActivity != null) {
            final SharedPreferences sharedPreferences = myActivity.getSharedPreferences("service_settings", MODE_PRIVATE);
            if (!sharedPreferences.getBoolean("auto_start", false)) {
                for (final Intent intent : POWERMANAGER_INTENTS) {
                    if (myActivity.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                        AlertDialog alertDialog = new AlertDialog.Builder(myActivity).create();
                        alertDialog.setTitle("Auto start is required");
                        alertDialog.setMessage("Please enable auto start to provide correct work");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        sharedPreferences.edit().putBoolean("auto_start", true).apply();
                                        myActivity.startActivity(intent);
                                    }
                                });
                        alertDialog.show();
                        break;
                    }
                }
            }
            start();
        }
        else{
            start();
        }
    }

    private static void start(){
        myActivity.startForegroundService(new Intent(myActivity, PedometerService.class));

    }
    public static void StopService(){
        Intent serviceIntent = new Intent(myActivity, PedometerService.class);
        myActivity.stopService(serviceIntent);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Bridge.appContext=getApplicationContext();
    }
}
