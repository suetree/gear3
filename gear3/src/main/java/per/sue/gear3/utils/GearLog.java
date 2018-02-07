package per.sue.gear3.utils;

import android.text.TextUtils;
import android.util.Log;

import per.sue.gear3.BuildConfig;







/*
* 文件名：
* 描 述：
* 作 者：su
* 时 间：2015/11/12
*/
public class GearLog {

    public static  boolean LOG_DEBUG = BuildConfig.DEBUG;
    public static final String SEPARATOR = ",";

    public static void v(String message) {
        if (LOG_DEBUG) {
            v(null, message);
        }
    }

    public static void v(String tag, String message) {
        if (LOG_DEBUG) {

            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag();
            }
            Log.v(tag, message);
        }
    }

    public static void d(String message) {
        if (LOG_DEBUG) {
            d(null, message);
        }
    }

    public static void d(String tag, String message) {
        if (LOG_DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag();
            }
            Log.d(tag, message);
        }
    }

    public static void i(String message) {
        if (LOG_DEBUG) {
            i(null, message);
        }
    }

    public static void i(String tag, String message) {
        if (LOG_DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag();
            }
            Log.i(tag, message);
        }
    }

    public static void w(String message) {
        if (LOG_DEBUG) {
            w(null, message);
        }
    }

    public static void w(String tag, String message) {
        if (LOG_DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag();
            }
            Log.w(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (LOG_DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag();
            }
             Log.e(tag,message);
        }
    }

    /**
     * 输出日志所包含的信息
     */
    public static String getDefaultTag() {
        //取第四个的原因是前两个分别为vm和Thread的方法，下标2是当前的v()方法，调用v()的方法的下标为3。
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        StringBuilder logInfoStringBuilder = new StringBuilder();
        // 获取线程名
        String threadName = Thread.currentThread().getName();
        // 获取线程ID
        long threadID = Thread.currentThread().getId();
        // 获取文件名.即xxx.java
        String fileName = stackTraceElement.getFileName();
        // 获取类名.即包名+类名
        String className = stackTraceElement.getClassName();
        // 获取方法名称
        String methodName = stackTraceElement.getMethodName();
        // 获取生日输出行数
        int lineNumber = stackTraceElement.getLineNumber();

        logInfoStringBuilder.append("[ ");
        logInfoStringBuilder.append("threadID=" + threadID).append(SEPARATOR);
        logInfoStringBuilder.append("threadName=" + threadName).append(SEPARATOR);
        logInfoStringBuilder.append("fileName=" + fileName).append(SEPARATOR);
      //  logInfoStringBuilder.append("className=" + className).append(SEPARATOR);
        logInfoStringBuilder.append("methodName=" + methodName).append(SEPARATOR);
        logInfoStringBuilder.append("lineNumber=" + lineNumber);
        logInfoStringBuilder.append(" ] ");
        return logInfoStringBuilder.toString();
    }

}