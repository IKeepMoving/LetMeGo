package com.example.swording.autoanswer;

import java.io.InputStream;
import android.content.Context;
import android.util.Log;

/**
 * 功能性函数扩展类
 *
 * @author kongqw
 *
 */
public class FucUtil {

    // Log标签
    private static final String TAG = "FucUtil";

    /**
     * 读取asset目录下文件内容
     *
     * @return content
     */
    public static String readFile(Context mContext, String file, String code) {
        int len = 0;
        byte[] buf = null;
        String result = "";
        try {
            InputStream in = mContext.getAssets().open(file);
            len = in.available();
            buf = new byte[len];
            in.read(buf, 0, len);

            result = new String(buf, code);
        } catch (Exception e) {
            //Log.e(TAG, e.toString());
            e.printStackTrace();
        }
        return result;
    }

}
