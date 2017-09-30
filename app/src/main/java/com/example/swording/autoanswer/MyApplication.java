package com.example.swording.autoanswer;

import android.app.Application;
import android.content.Context;

import com.example.swording.R;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by SwordIng on 2017/7/8.
 */

public class MyApplication extends Application {
    private static Context context;

    public static Context getContext(){
        return context;
    }
    public void onCreate(){
        context=getApplicationContext();
        SpeechUtility.createUtility(MyApplication.this, "appid="+getString(R.string.app_id));
        super.onCreate();
    }
}
