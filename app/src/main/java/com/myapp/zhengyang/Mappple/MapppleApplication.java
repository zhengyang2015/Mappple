package com.myapp.zhengyang.Mappple;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class MapppleApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
