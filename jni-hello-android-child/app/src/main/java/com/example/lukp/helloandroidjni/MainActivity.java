package com.example.lukp.helloandroidjni;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.os.Process;


public class MainActivity extends AppCompatActivity {

    static String TAG = "Main";

    final static int NUM_SERVICES = 2;
    public static MyService[] services = new MyService[NUM_SERVICES];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, String.format("Before start Service1 PID: %d", android.os.Process.myPid()));
        Intent intent = new Intent(getBaseContext(), MyService1.class);
        intent.putExtra("iterations", 5);
        startService(intent);
        Log.i(TAG, String.format("After start Service1 PID: %d", android.os.Process.myPid()));


    }

}
