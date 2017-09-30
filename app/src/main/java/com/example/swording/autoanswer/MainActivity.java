package com.example.swording.autoanswer;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;

import com.android.internal.telephony.ITelephony;
import com.example.swording.R;

import java.lang.reflect.Method;

public class MainActivity extends Activity {
    private IntentFilter intentFilter;
    private MyReceiver myReceiver;
    private TextView mTvResult;
    private TextView mTvLog;
    private BuildLocalGrammar buildLocalGrammar;
    private MySpeechRecognizer mySpeechRecognizer;
    private ITelephony  iTelephony;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mTvResult = (TextView) findViewById(R.id.tv_result);
        mTvLog = (TextView) findViewById(R.id.tv_log);

        //为MyReceiver类注册动态注册广播
        intentFilter=new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        myReceiver=new MyReceiver(mySpeechRecognizer);
        registerReceiver(myReceiver,intentFilter);


        //利用发射机制获得ITelephony对象，其方法endCall()为挂电话
        try {
            Method method = Class.forName("android.os.ServiceManager")
                    .getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{TELEPHONY_SERVICE});
            iTelephony = ITelephony.Stub.asInterface(binder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 初始化本地语法构造器
         */
        buildLocalGrammar = new BuildLocalGrammar(this) {
            @Override
            public void result(String errMsg, String grammarId) {
                // errMsg为null 构造成功
                if (TextUtils.isEmpty(errMsg)) {
                    Toast.makeText(MainActivity.this, "构造成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "构造失败", Toast.LENGTH_SHORT).show();
                }
            }
        };

        /**
         * 初始化离线命令词识别器
         */
        mySpeechRecognizer = new MySpeechRecognizer(this) {

            @Override
            public void speechLog(String log) {
                // 录音Log信息的回调
                mTvLog.setText(log);
            }

            @Override
            public void resultData(String data) {

                if(data=="没有构建的语法")
                {
                    return;
                }
                else
                {
                    String judge=JsonParser.parseIatResult(data);
                    mTvResult.setText(judge);
                    if (judge.equals("接听")||judge.equals("接听电话"))
                    {
                        answerRingingCall();

                    }
                    else if (judge.equals("挂断电话"))
                    {
                        try {
                            //当电话接入时，自动挂断。
                            iTelephony.endCall();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                //停止识别
                stoplistening();
            }

            @Override
            public void initListener(boolean flag) {
                // 初始化的回调
                if (flag) {
                    Toast.makeText(MainActivity.this, "初始化成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "初始化失败", Toast.LENGTH_SHORT).show();
                }
            }
        };

        /**
         * 构造本地语法文件，只有语法文件有变化的时候构造成功一次即可，不用每次都构造
         */
        buildLocalGrammar.buildLocalGrammar();

    }

    /**
     *自动接听电话
     */
    private void answerRingingCall() {
        Intent intentauto = new Intent("android.intent.action.MEDIA_BUTTON");
        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
        intentauto.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
        MyApplication.getContext().sendOrderedBroadcast(intentauto, "android.permission.CALL_PRIVILEGED");
    }

    /**
     * 开始识别按钮
     */
    public void start(View view) {
        mTvResult.setText(null);
        // 开始识别
        mySpeechRecognizer.startListening();
    }

}