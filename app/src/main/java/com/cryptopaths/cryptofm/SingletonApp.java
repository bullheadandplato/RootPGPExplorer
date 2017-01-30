package com.cryptopaths.cryptofm;

/**
 * Created by Shadow on 1/26/2017.
 *
 */

public class SingletonApp {
    private static SingletonApp instance;

    public static SingletonApp get() {
        if(instance == null) instance = getSync();
        return instance;
    }

    private static synchronized SingletonApp getSync() {
        if(instance == null) instance = new SingletonApp();
        return instance;
    }

    private SingletonApp(){
        // here you can directly access the Application context calling

    }
}
