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
    static Context app_context;

    static {
        System.loadLibrary("hello-android-jni");
    }
    public native String getMsgFromJni();
    // new code done

    public static void callFromJni(){
        Log.i("Main", String.format("Look a child is calling - PID %d",
                android.os.Process.myPid()));
        Log.i("Main", "Let's try some monkey business, while the parent's outta town");

        ContentResolver content_resolver;
        AudioManager audio_manager;

        content_resolver = app_context.getContentResolver();

        audio_manager = (AudioManager)app_context.getSystemService(
                app_context.AUDIO_SERVICE);


        while(true) {
            Log.i("LocalService", String.format("PID: %d", android.os.Process.myPid()));
            Boolean airplane_mode = (android.provider.Settings.System
                    .getString(content_resolver,
                            Settings.Global.AIRPLANE_MODE_ON) ==
                    Settings.Global.AIRPLANE_MODE_ON);
            Integer ringer_mode = audio_manager.getRingerMode();


            Log.i("Ringer Mode", ringer_mode.toString());
            Log.i("Airplane Mode", airplane_mode.toString());


            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
//        app_context = getBaseContext();
//        Intent intent2 = new Intent(getBaseContext(), com.example.lukp.helloandroidjni.MyService.class);
//        startService(intent2);


        new Thread(new Runnable() {
            @Override
            public void run() {
                //getMsgFromJni();

                Log.i("Service", String.format("Child - PID: %d", android.os.Process.myPid()));

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
