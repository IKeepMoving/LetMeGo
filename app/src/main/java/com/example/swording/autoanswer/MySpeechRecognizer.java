package com.example.swording.autoanswer;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * 命令词识别
 */
public abstract class MySpeechRecognizer {
    /**
     * 初始化的回调
     *
     * @param flag
     *            true 初始化成功 false 初始化失败
     */
    public abstract void initListener(boolean flag);

    public abstract void resultData(String data);

    public abstract void speechLog(String log);

    // Log标签
    private static final String TAG = "KqwLocalCommandRecognizer";

    public static final String GRAMMAR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/test";
    // 上下文
    private Context mContext;
    // 语音识别对象
    private SpeechRecognizer mAsr;

    public MySpeechRecognizer(Context context) {
        mContext = context;

        // 初始化识别对象
        mAsr = SpeechRecognizer.createRecognizer(context, new InitListener() {

            @Override
            public void onInit(int code) {

                if (code != ErrorCode.SUCCESS) {
                    initListener(false);
                    Toast.makeText(mContext, "初始化失败,错误码：" + code, Toast.LENGTH_SHORT).show();
                } else {
                    initListener(true);
                }
            }
        });

    }

    /**
     * 参数设置
     */
    public void setParam() {
        // 清空参数
        mAsr.setParameter(SpeechConstant.PARAMS, null);
        // 设置识别引擎 本地引擎
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        // mAsr.setParameter(SpeechConstant.ENGINE_TYPE,
        // SpeechConstant.TYPE_MIX);
        // mAsr.setParameter(SpeechConstant.ENGINE_TYPE, "mixed");
        // // 设置本地识别资源
        mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
        // 设置语法构建路径
        mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, GRAMMAR_PATH);
        // 设置返回结果格式
        mAsr.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置本地识别使用语法id
        mAsr.setParameter(SpeechConstant.LOCAL_GRAMMAR, "kqw");
        // 设置识别的门限值
        mAsr.setParameter(SpeechConstant.MIXED_THRESHOLD, "60");
        // 使用8k音频的时候请解开注释
        // mAsr.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
        mAsr.setParameter(SpeechConstant.DOMAIN, "iat");
        mAsr.setParameter(SpeechConstant.NLP_VERSION, "2.0");
        mAsr.setParameter("asr_sch", "1");
        // mAsr.setParameter(SpeechConstant.RESULT_TYPE, "json");
    }

    // 获取识别资源路径
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        // 识别通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, RESOURCE_TYPE.assets, "asr/common.jet"));
        // 识别8k资源-使用8k的时候请解开注释
        // tempBuffer.append(";");
        // tempBuffer.append(ResourceUtil.generateResourcePath(this,
        // RESOURCE_TYPE.assets, "asr/common_8k.jet"));
        return tempBuffer.toString();
    }

    int ret = 0;// 函数调用返回值

    /**
     * 开始识别
     */
    public void startListening() {
        // 设置参数
        setParam();

        ret = mAsr.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {

        }
    }
    public void stoplistening() {
        // 设置参数
        mAsr.stopListening();

    }

    /**
     * 识别监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        StringBuffer stringBuffer = new StringBuffer();

        public void onVolumeChanged(int volume,byte[] bytes) {

            speechLog("当前正在说话，音量大小：" + volume);
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            /*
             * TODO 拼接返回的数据
             *
             * 这里返回的是Json数据，具体返回的是离线名命令词返回的Json还是语义返回的Json，需要做判断以后在对数据数据进行拼接
             */
            stringBuffer.append(result.getResultString()).append("\n\n");
            // isLast为true的时候，表示一句话说完，将拼接后的完整的一句话返回
            if (isLast) {
                // 数据回调
                resultData(stringBuffer.toString());
            }
        }

        @Override
        public void onEndOfSpeech() {

            speechLog("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            stringBuffer.delete(0, stringBuffer.length());

            speechLog("开始说话");
        }

        @Override
        public void onError(SpeechError error) {

            if (error.getErrorCode() == 20005) {
                // 本地命令词没有识别，也没有请求到网络
                resultData("没有构建的语法");
                speechLog("没有构建的语法");
                /*
                 * TODO
                 * 当网络正常的情况下是不会回调20005的错误，只有当本地命令词识别不匹配，网络请求也失败的情况下，会返回20005
                 * 这里可以自己再做处理，例如回复“没有听清”等回复
                 */
            } else {
                /*
                 * TODO
                 * 其他错误有很多，需要具体问题具体分析，正常在程序没有错误的情况下，只会回调一个没有检测到说话的错误，没记错的话错误码是10118
                 */
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }

    };

}