package com.example.swording.autoanswer;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

/**
 * 构建离线命令词语法
 *
 * @author kongqw
 *
 */
public abstract class BuildLocalGrammar {

    /**
     * 构建语法的回调
     *
     * @param errMsg
     *            null 构造成功
     */
    public abstract void result(String errMsg, String grammarId);

    // Log标签
    //private static final String TAG = "BuildLocalGrammar";

    public static final String GRAMMAR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/test";
    // 上下文
    private Context mContext;
    // 语音识别对象
    private SpeechRecognizer mAsr;

    public BuildLocalGrammar(Context context) {
        mContext = context;

        // 初始化识别对象
        mAsr = SpeechRecognizer.createRecognizer(context, new InitListener() {

            @Override
            public void onInit(int code) {
                //Log.d(TAG, "SpeechRecognizer init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    result(code + "", null);
                    //Log.d(TAG, "初始化失败,错误码：" + code);
                    Toast.makeText(mContext, "初始化失败,错误码：" + code, Toast.LENGTH_SHORT).show();
                }
            }
        });
    };

    /**
     * 构建语法
     *
     * @return
     */
    public void buildLocalGrammar() {
        try {
            /*
             * TODO 如果你要在程序里维护bnf文件，可以在这里加上你维护的一些逻辑
             * 如果不嫌麻烦，要一直改bnf文件，这里的代码可以不用动，不过我个人不建议一直手动修改bnf文件
             * ，内容多了以后很容易出错，不好找Bug，建议每次改之前先备份。 建议用程序维护bnf文件。
             */

            /*
             * 构建语法
             */
            String mContent;// 语法、词典临时变量
            String mLocalGrammar = FucUtil.readFile(mContext, "kqw.bnf", "utf-8");
            mContent = new String(mLocalGrammar);
            mAsr.setParameter(SpeechConstant.PARAMS, null);
            // 设置文本编码格式
            mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
            // 设置引擎类型
            mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置语法构建路径
            mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, GRAMMAR_PATH);
            // 使用8k音频的时候请解开注释
            // mAsr.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
            // 设置资源路径
            mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
            // 构建语法
            int ret = mAsr.buildGrammar("bnf", mContent, new GrammarListener() {

                @Override
                public void onBuildFinish(String grammarId, SpeechError error) {
                    if (error == null) {
                        //Log.d(TAG, "语法构建成功");
                        result(null, grammarId);
                    } else {
                        //Log.d(TAG, "语法构建失败,错误码:" + error.getErrorCode());
                        result(error.getErrorCode() + "", grammarId);
                    }
                }
            });
            if (ret != ErrorCode.SUCCESS) {
                //Log.d(TAG, "语法构建失败,错误码:" + ret);
                result(ret + "", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

}
