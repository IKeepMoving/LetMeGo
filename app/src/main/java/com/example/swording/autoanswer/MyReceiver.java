package com.example.swording.autoanswer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

/**
 * 接收来电广播类
 */

public class MyReceiver extends BroadcastReceiver {

    private AudioManager audioManager;
    private MySpeechRecognizer mySpeechRecognizer;
    public MyReceiver(MySpeechRecognizer mySpeechRecognizer) {
        this.mySpeechRecognizer = mySpeechRecognizer;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //来电自动打开扬声器
        audioManager = (AudioManager)MyApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(true);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL ),
                AudioManager.STREAM_VOICE_CALL);


        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            //如果是去电（拨出）
        } else {
            mySpeechRecognizer.startListening();

        }
    }

//        PhoneStateListener listener = new PhoneStateListener() {
//
//            @Override
//            public void onCallStateChanged(int state, String incomingNumber) {
//                // TODO Auto-generated method stub
//                //state 当前状态 incomingNumber,貌似没有去电的API
//                super.onCallStateChanged(state, incomingNumber);
//                switch (state) {
//                    case TelephonyManager.CALL_STATE_IDLE:
//                        //System.out.println("挂断");
//                        break;
//                    case TelephonyManager.CALL_STATE_OFFHOOK:
//                        //System.out.println("接听");
//                        break;
//                    case TelephonyManager.CALL_STATE_RINGING:
//                        //System.out.println("响铃:来电号码"+incomingNumber);
//                        //输出来电号码
//                        break;
//                }
//            }
//        };
}
