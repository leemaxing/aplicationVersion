package com.max.appversion.update;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.max.appversion.networking.NetworkingTools;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by max on 2018/4/21.
 */

public class UpdateTools {

    static String testHtml = "com.shujuzhili.test";
    static String useHtml = "com.shujuzhili.html";
    static String localHtmlVersionName = "htmlVersion";

    public static String kUPDADATEINFO = "updateInfokey";
    Context mContext;
    String updateUrl;
    Handler senderHander;

    private UpdateToolsCallBack mCallBack;


    private static String API_TOKEEN = "7dce12df57afe269e2632083f5f970d7";
    private static String APP_ID = "5e858cc423389f5c4da793d6";
    String htmlVersion = "";

    public interface UpdateToolsCallBack{
        void needUpdate(String msg,boolean must,String serverVersion);
        void onNoUpdate();
        void onDownLoadProgress(int progress);
        void onDownLoaded(String path);
    }

    public UpdateTools(Context context){
        super();
        this.mContext = context;
        this.senderHander = new Handler();
    }


    public String getLocolVersion(){
        String lcoPath = getDiskCachePath(mContext) + "/" + "app.apk";
        File file = new File(lcoPath);
        if (file.exists()){
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo info = packageManager.getPackageArchiveInfo(lcoPath,PackageManager.GET_ACTIVITIES);
            if (info != null){
                String version = info.versionName;
                return version;
            }
        }
        return null;
    }


    public void checkVersion(final UpdateToolsCallBack callBack){

        this.mCallBack = callBack;
        String packgeName = mContext.getApplicationInfo().packageName;
        String api = "http://113.107.253.58:8890/updateMsg/getMsg";
        Map param = new HashMap();
//        param.put("appName","liicon.com.hzspotcheck");
        param.put("appName",packgeName);

        NetworkingTools.post(api, param, new NetworkingTools.NetworkingResponseListener() {
            @Override
            public void onSuccess(JSON data) {
                JSONObject map = (JSONObject) JSON.parse(data.toString());
                JSONArray res = (JSONArray) map.get("result");
                if (res.size() <= 0){
                    return;
                }
                JSONObject result = (JSONObject) res.get(0);
                String localVersion = getVersion(mContext);
                result.put("localVersion",localVersion);
                int code = conpareVersion(result);
                ShareUtils.save(mContext,kUPDADATEINFO,result.toJSONString());
                if (code == 1){
                    updateUrl = result.getString("filePath");
                    String msg = result.getString("remark");
                    String isMust = result.getString("isMust");
                    String mesage = msg == null?"":msg;
                    String serverversion = result.getString("version");
                    callBack.needUpdate(mesage,isMust.equals("true"),serverversion);
                }else {
                    callBack.onNoUpdate();
                }
            }

            @Override
            public void onFail() {
                callBack.onNoUpdate();
            }
        });
    }


    public void getDowLoadToken(){

        if (!TextUtils.isEmpty(updateUrl)){
            download(updateUrl);
            return;
        }

        String api = "http://api.bq04.com/apps/" + APP_ID +"/download_token?api_token="
                + API_TOKEEN;

        NetworkingTools.get2(api, null, new NetworkingTools.NetworkingResponseListener() {
            @Override
            public void onSuccess(JSON data) {
                Map map = (Map) JSON.parse(data.toString());
                String downloadToken = (String) map.get("download_token");
                instalApk(downloadToken);
            }

            @Override
            public void onFail() {

            }
        });
    }


    private void instalApk(final String downloadToken){
        //切换到主线程
        final android.os.Handler handler=new android.os.Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                download((String )msg.obj);
            }
        };

        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String  api = "http://download.fir" +
                            ".im/apps/"+ APP_ID + "/install?download_token=" + downloadToken;

                    URL serverUrl = new URL(api);
                    HttpURLConnection conn = (HttpURLConnection) serverUrl
                            .openConnection();
                    conn.setRequestMethod("GET");
                    // 必须设置false，否则会自动redirect到Location的地址
                    conn.setInstanceFollowRedirects(false);

                    conn.addRequestProperty("Accept-Charset", "UTF-8;");
                    conn.addRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");
                    conn.addRequestProperty("Referer", "http://zuidaima.com/");
                    conn.connect();
                    String location = conn.getHeaderField("Location");
                    if (conn.getResponseCode() == 302){
                        handler.sendMessage(handler.obtainMessage(0,location));
                    }
                    Intent intent = new Intent();
                    intent.setData(Uri.parse(api));
                    intent.setAction(Intent.ACTION_VIEW);
                    mContext.startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public int conpareVersion(Map response){
        if (response == null){
            return 0;
        }
        if (response.size() == 0){
            return 0;
        }
        String versionShort = (String) response.get("version");
        String localVersion = getVersion(mContext);
        if (versionShort == null){
            return 0;
        }
        return versionCompare(versionShort,localVersion);
    }

    public int versionCompare(String versionServer,String versionLoc){
        return  compareVersion(versionServer,versionLoc);
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号，默认是1.0.0
     */
    public static String getVersion(Context mContext) {
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 版本号比较
     *
     * @param version1
     * @param version2
     * @return
     */
    public static int compareVersion(String version1, String version2) {
        if (version1.equals(version2)) {
            return 0;
        }
        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");
        int index = 0;
        // 获取最小长度值
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;
        while (index < minLen
                && (diff = Integer.parseInt(version1Array[index])
                - Integer.parseInt(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            // 如果位数不一致，比较多余位数
            for (int i = index; i < version1Array.length; i++) {
                if (Integer.parseInt(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }


    void download(String url){

        String urlStr = "http://113.107.253.58:8890/updateMsg/download?filePath=" + updateUrl;
        NetworkingTools.download(urlStr, "app.apk", getDiskCachePath(mContext), new NetworkingTools.NetDownLoadListener() {
            @Override
            public void onSuccess(String status, String path) {
                if (mCallBack != null){
                    mCallBack.onDownLoaded(path);
                }
            }

            @Override
            public void onDowloadProges(int progress) {
                if (mCallBack != null){
                    mCallBack.onDownLoadProgress(progress);
                }
            }

            @Override
            public void onResponse(Map map) {

            }

            @Override
            public void onFail() {

            }
        });
    }



    /**
     * 获取cache路径
     *
     * @param context
     * @return
     */
    public static String getDiskCachePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            return context.getExternalCacheDir().getPath();
        } else {
            return context.getCacheDir().getPath();
        }
    }
}
