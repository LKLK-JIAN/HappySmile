package com.lg.happysmile;

import android.app.Application;
import com.facebook.drawee.backends.pipeline.Fresco;

public class App extends Application {
//    public static RequestQueue queues; // Volley加载队列

    @Override
    public void onCreate()
    {
        super.onCreate();
        Fresco.initialize(this);
//        queues = Volley.newRequestQueue(getApplicationContext()); // 实列话
    }
}
