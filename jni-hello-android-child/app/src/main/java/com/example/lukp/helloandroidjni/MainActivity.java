package com.example.lukp.helloandroidjni;

import android.app.ActivityManager;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class MainActivity extends AppCompatActivity {

    static Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getApplicationContext();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Log.i("Main", String.format("PID: %d", android.os.Process.myPid()));

//        Intent intent = new Intent(getBaseContext(), MyService.class);
//        startService(intent);

        Class clazz;
        Object obj;
        try {
            clazz = Class.forName("android.os.Process");
            Method m = clazz.getMethod("start", new Class[] {String.class,
                String.class, int.class, int.class, int[].class,
                int.class, int.class, int.class, String.class,
                String.class, String.class, String.class,
                String[].class});

            final String processClass = "FunkyProcess";
            final String niceName = "myreallyfunkproc";
            int uid = Process.myUid();
            int gid = Process.getGidForName("com.sensibility_testbed");
            int[] gids = null;
            int debugFlags = 1;
            int mountExternal = 0;
            int targetSdkVersion = 23;
            String seInfo = null;
            String abi = "armeabi";
            String instructionSet = null;
            String appDataDir = null;
            String[] zygoteArgs = null;

            obj = m.invoke(null, new Object[] {processClass, niceName, uid, gid, gids, debugFlags,
                    mountExternal, targetSdkVersion, seInfo, abi,  instructionSet, appDataDir,
                    zygoteArgs});

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //getMsgFromJni();
//                for (int i = 0; i < 10; i++) {
//                    try {
//                        MySingleton.getInstance().shared.add(String.format("Parent - PID: %d", android.os.Process.myPid()));
//                        Thread.sleep(160);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                Log.i("Activity", MySingleton.getInstance().shared.toString());
//            }
//        }).start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
