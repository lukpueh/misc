package com.example.lukp.helloandroidjni;

import java.util.ArrayList;

/**
 * Created by lukp on 8/15/16.
 */

public class MySingleton {

    public ArrayList<String> shared;

    private MySingleton() {
        shared = new ArrayList<>();
    }

    private static class LazyHolder {
        private static final MySingleton INSTANCE = new MySingleton();
    }

    public static MySingleton getInstance() {
        return LazyHolder.INSTANCE;
    }

}
