package com.example.lukp.helloandroidjni;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String FILES_ROOT = getApplicationContext().getFilesDir().getPath() + "/";

        Log.i(TAG, String.format("Unpacking python archive to %s", FILES_ROOT));
        try {
            Utils.unzip(getResources().openRawResource(R.raw.python_lib), FILES_ROOT, true);
        } catch (IOException e) {
            Log.i(TAG, "Couldn't unpack python archive");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, String.format("Before Activity starts MyService1 in Activity -- PID: %d",
                android.os.Process.myPid()));
        Intent intent = new Intent(getBaseContext(), MyService1.class);
        startService(intent);
        Log.i(TAG, String.format("After Activity starts MyService1 in Activity -- PID: %d",
                android.os.Process.myPid()));
    }
}