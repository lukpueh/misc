package com.example.lukp.helloandroidjni;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class MyService extends Service {
    static String TAG = "Service";

    public void start_service(int iterations, Class service_class) {

        //Get free service

        Log.i(TAG, String.format("Before start Service PID: %d", android.os.Process.myPid()));
        Intent intent = new Intent(getBaseContext(), service_class);
        intent.putExtra("iterations", iterations);
        startService(intent);
        Log.i(TAG, String.format("After start Service PID: %d", android.os.Process.myPid()));

    }

    public void do_service_stuff(int iterations) throws InterruptedException {
        for (int i = 0; i < iterations; i++){
            Log.i(TAG, String.format("PID: %d, Iteration: %d",  android.os.Process.myPid(), i));
            Thread.sleep(1000);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        int iterations = intent.getIntExtra("iterations", 10);
        try {
            do_service_stuff(iterations);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, String.format("Stopping service PID: %d", android.os.Process.myPid()));

        stopSelf();
        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
