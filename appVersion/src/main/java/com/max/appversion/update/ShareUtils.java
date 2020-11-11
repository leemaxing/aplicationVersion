package com.max.appversion.update;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by WHL on 2019/4/25 11:29.
 * Class 简单封装SharedPreferences的覆盖式存储,追加式存储,取得数据三个方法
 */

public class ShareUtils {
   static String fileName = "password.txt";
    /**
     * 覆盖式存储
     * @param mContext
     * @param key
     * @param content
     */

    public static void save(Context mContext, String key, String content){
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor  editor= mSharedPreferences.edit();//SharedPreferences编辑的Editor对象
        editor.putString(key,content);
        editor.commit();//提交
    }

    /**
     * 追加式存储
     * @param mContext
     * @param key
     * @param content
     */
    public  static  void saveAdd(Context mContext, String key, String content){
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String value = mSharedPreferences.getString(key, "");
        String values = value + content;
        SharedPreferences.Editor  editor= mSharedPreferences.edit();//SharedPreferences编辑的Editor对象
        editor.putString(key,values);
        editor.commit();//提交
    }

    /**
     * 取
     * @param mContext
     * @param key
     * @return
     */
    public static String get(Context mContext, String key){
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String value = mSharedPreferences.getString(key, "");
        return  value;
    }
}
