package com.example.lukp.helloandroidjni;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import java.util.List;

public class MyService extends Service {
    static String TAG = "Service";

    static {
        System.loadLibrary("hello-android-jni");
    }

    public native void goNative();


    public void doNastyJavaStuffFromNative() {
        ContentResolver content_resolver;
        content_resolver = getApplicationContext().getContentResolver();
        Log.i(TAG, android.provider.Settings.System.getString(content_resolver,
                Settings.Global.AIRPLANE_MODE_ON));
    }

    public void findAndStartIdleService() {

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        // Enabled services as per Manifest
        Class[] enabled_services = {MyService1.class, MyService2.class};

        // Enabled but idle service
        Class idle_service = null;

        // Currently running service per ActivityManager
        List<ActivityManager.RunningServiceInfo> running_services =
                manager.getRunningServices(Integer.MAX_VALUE);

        find_idle_service_loop:
        for (Class enabled_service : enabled_services) {
            for (ActivityManager.RunningServiceInfo running_service : running_services) {
//                Log.i(TAG, String.format("Enabled '%s', Running '%s'", enabled_service.getName(), running_service.service.getClassName()));
                if (enabled_service.getName().equals(running_service.service.getClassName())) {
                    Log.i(TAG, String.format("Service '%s' is not idle", enabled_service.getName()));
                    // If we find the enabled service in the list of running services
                    // we can continue checking the next enabled service
                    continue find_idle_service_loop;
                }
            }
            // The enabled service was not found in the list of running services
            // ergo it is idle and we can use it
            idle_service = enabled_service;
            break;
        }

        if (idle_service != null) {
            // Start the idle Service
            Log.i(TAG, String.format("Before Service '%s' starts Service '%s' -- PID: %d",
                    getClass().getName(), idle_service.getName(), android.os.Process.myPid()));
            Intent intent = new Intent(getBaseContext(), idle_service);
            startService(intent);
            Log.i(TAG, String.format("After Service '%s' starts Service '%s' -- PID: %d",
                    getClass().getName(), idle_service.getName(), android.os.Process.myPid()));
        } else {
            Log.i(TAG, String.format("Service '%s' couldn't find an idle Service to start :(",
                    getClass().getName()));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        goNative();

        Log.i(TAG, String.format("Stopping service PID: %d", android.os.Process.myPid()));

        stopSelf();
        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
