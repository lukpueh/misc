package com.example.lukp.helloandroidjni;

import android.os.Process;
import android.util.Log;

/**
 * Created by lukp on 8/16/16.
 */

public class FunkyProcess {

    public static void main(String[] args) throws InterruptedException {
        // Prints "Hello, World" to the terminal window.
        Log.i("Funky Processini", String.format("Running forever PID - %d", Process.myPid()));
        Thread.sleep(2000);
    }
}
