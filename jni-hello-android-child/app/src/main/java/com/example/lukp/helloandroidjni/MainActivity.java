package com.example.lukp.helloandroidjni;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import java.util.List;


public class MainActivity extends AppCompatActivity {

    static String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, String.format("Before Activity starts MyService1 in Activity -- PID: %d",
                android.os.Process.myPid()));
        Intent intent = new Intent(getBaseContext(), MyService1.class);
        startService(intent);
        Log.i(TAG, String.format("After Activity starts MyService1 in Activity -- PID: %d",
                android.os.Process.myPid()));
    }
}