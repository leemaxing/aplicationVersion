package com.max.appversion.update;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolClass {

    /**
     * 获取cache路径
     */
    public static String getDiskCachePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            return context.getExternalCacheDir().getPath();
        } else {
            return context.getCacheDir().getPath();
        }
    }

    public interface ToolMainThreadCall{
        void call();
    }

    public static void mainTread(final ToolMainThreadCall mCall){
        if (isMainThread()){
            mCall.call();
        }else{
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    //已在主线程中，可以更新UI
                    mCall.call();
                }
            });
        }
    }

    public static Boolean isMainThread(){
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    /*
    * 判断是否是空
    * */
    public static boolean stringIsEmpty(String text){
        return text == null || text.length() == 0;
    }


    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    private static boolean isPhoneNumber(String phoneStr) {
        // "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String regex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$";
        //设定查看模式
        Pattern p = Pattern.compile(regex);
        //判断Str是否匹配，返回匹配结果
        Matcher m = p.matcher(phoneStr);
        return m.find();
    }
}
