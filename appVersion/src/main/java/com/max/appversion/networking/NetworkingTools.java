package com.max.appversion.networking;

import android.os.Environment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.max.appversion.update.ToolClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkingTools {

    public interface NetworkingResponseListener {
        public void onSuccess(JSON data);

        public void onFail();
    }


    public interface NetworkingResponseListenerString {
        public void onSuccess(String data);

        public void onFail();
    }



    public NetworkingTools() {

    }



    /*
     * url:请求路径
     * parameter：参数
     * responseListener：监听
     * */
    public static void get(String url, Map<String, Object> parameter, final NetworkingResponseListener responseListener) {

        OkHttpClient client = new OkHttpClient();
        client.newBuilder().connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        String mUrl = url + getParameter(parameter);

        final Request request = new Request.Builder().url(mUrl).build();
        //.addHeader("cookie","node01j8vwbeu56jlr1d9c2hvcbxvu97.node0").

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseListener.onFail();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.code() == 200) {
                    final JSON json = (JSON) JSON.parse(response.body().string());
                    ToolClass.mainTread(new ToolClass.ToolMainThreadCall() {
                        @Override
                        public void call() {
                            if (json != null){
                                responseListener.onSuccess(json);
                            }else {
                                responseListener.onFail();
                            }
                        }
                    });
                } else {
                    responseListener.onFail();
                }
            }
        });
    }

    /*
     * url:请求路径
     * parameter：参数
     * responseListener：监听
     * */
    public static void get2(String url, Map<String, Object> parameter, final NetworkingResponseListener responseListener) {

        OkHttpClient client = new OkHttpClient();
        client.newBuilder().connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
//        url = changeUrlComponent(url);
        String mUrl = url + getParameter(parameter);
        final Request request = new Request.Builder().url(mUrl).build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseListener.onFail();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.code() == 200) {
                    final JSON json = (JSON) JSON.parse(response.body().string());
                    ToolClass.mainTread(new ToolClass.ToolMainThreadCall() {
                        @Override
                        public void call() {
                            if (json != null){
                                responseListener.onSuccess(json);
                            }else {
                                responseListener.onFail();
                            }
                        }
                    });
                } else {
                    responseListener.onFail();
                }
            }
        });
    }


    /*
     * url:请求路径
     * parameter：参数
     * responseListener：监听
     * */
    public static void getString(String url, Map<String, Object> parameter, final NetworkingResponseListenerString responseListener) {

        OkHttpClient client = new OkHttpClient();
        client.newBuilder().connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
//        url = changeUrlComponent(url);
        String mUrl = url + getParameter(parameter);
        final Request request = new Request.Builder().url(mUrl).build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseListener.onFail();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.code() == 200) {
                    String string = response.body().string();
                    responseListener.onSuccess(string);
                } else {
                    responseListener.onFail();
                }
            }
        });
    }

    /*
     * 将参数转换成字符串
     * */
    private static String getParameter(Map<String, Object> parameter) {

        String mParam = "";
        if (parameter != null) {
            for (String key : parameter.keySet()) {
                String mValue = parameter.get(key).toString();
                mParam += "&" + key + "=" + mValue;
            }
            mParam = mParam.substring(1, mParam.length());
            mParam = "?" + mParam;
        }
        return mParam;
    }


    public static void post(String url, Map<String, Object> parameter, final NetworkingResponseListener
            responseListener) {

        OkHttpClient client = new OkHttpClient();
        RequestBody body = getBody(parameter);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
//                .addHeader("Content-Type", "multipart/form-data")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseListener.onFail();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                JSON json = null;

                try {
                    json = (JSON) JSON.parse(response.body().string());

                }catch (Exception e){
                    e.printStackTrace();

                }
                final JSON finalJson = json;
                ToolClass.mainTread(new ToolClass.ToolMainThreadCall() {
                    @Override
                    public void call() {
                        if (finalJson != null){
                            responseListener.onSuccess(finalJson);
                        }else {
                            responseListener.onFail();
                        }
                    }
                });
            }
        });
    }





    private static RequestBody getBody(Map<String, Object> param) {

        MultipartBody.Builder body = new MultipartBody.Builder();
        body.setType(MultipartBody.FORM);
        for (String key : param.keySet()) {
            body.addFormDataPart(key, param.get(key).toString());
        }
        return body.build();
    }


    public static void getUrl(String url, Map param, final NetMapListener listener) {
        OkHttpClient client = new OkHttpClient();
        client.newBuilder().connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        String mUrl = url + getParameter(param);
        final Request request = new Request.Builder().url(mUrl).build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFail();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.code() == 200) {
                    JSON json = (JSON) JSON.parse(response.body().string());
                    listener.onSuccess(json);
                } else {
                    listener.onFail();
                }
            }
        });
    }

    public interface NetMapListener {
        public void onSuccess(JSON json);

        public void onFail();
    }



    /**(暂未完善)
     * post请求上传对应多种key的多张文件 包括图片....流的形式传任意文件...
     * @param url 接口地址
     * @param mediaFiles map集合 String是文件对应的key,List<String> 是对应的文件路径的集合
     * @param params 传递除了file文件 其他的参数放到map集合
     */
    public static void uploadAllFile(String url, Map<String,List<String>> mediaFiles, Map<String, Object>
            params, final NetMapListener listener) {
        //创建OkHttpClient请求对象

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newBuilder().connectTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(4000, TimeUnit.SECONDS)
                .readTimeout(4000, TimeUnit.SECONDS);

        //MultipartBody多功能的请求实体对象,,,formBody只能传表单形式的数据
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        //参数
        if (params != null) {
            for (String key : params.keySet()) {
                builder.addFormDataPart(key, params.get(key).toString());
            }
        }


        //文件...参数key指的是请求路径中所接受的参数
        for (String key : mediaFiles.keySet()) {
            List<String> strings = mediaFiles.get(key);
            if(strings!=null && strings.size()>=1){
                for (String filePath: strings) {
                    if(!TextUtils.isEmpty(filePath)){
                        File file = new File(filePath);
                        builder.addFormDataPart(key,file.getName() , RequestBody.create
                                (MediaType.parse("application/octet-stream"), file));
                    }
                }

            }
        }

        //构建
        MultipartBody multipartBody = builder.build();

        //创建Request
        Request request = new Request.Builder().url(url).post(multipartBody).build();

        //得到Call
        Call call = okHttpClient.newCall(request);
        //执行请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                listener.onFail();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSON json = (JSON) JSON.parse(response.body().string());
                listener.onSuccess(json);
            }
        });
    }


    /**
     * 判断下载目录是否存在......并返回绝对路径
     *
     * @param saveDir
     * @return
     * @throws IOException
     */
    public static String isExistDir(String saveDir) throws IOException {
        // 下载位置
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File downloadFile = new File(saveDir);
            if (!downloadFile.mkdirs()) {
                downloadFile.createNewFile();
            }
            String savePath = downloadFile.getAbsolutePath();
            return savePath;
        }
        return null;
    }

    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
    private static String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }


    public interface NetDownLoadListener {

        public void onSuccess(String status, String path);

        public void onResponse(Map map);

        public void onDowloadProges(int progress);

        public void onFail();

    }


    public static void download(final String url, final String fileName, final String saveDir,
                                final NetDownLoadListener listener) {


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectionSpecs(Arrays.asList(
                        ConnectionSpec.MODERN_TLS,
                        ConnectionSpec.COMPATIBLE_TLS,
                        ConnectionSpec.CLEARTEXT)).build();
        final Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFail();
            }
            @Override
            public void onResponse(Call call, final Response response)
                    throws IOException {
                InputStream is = null;
                byte[] bytes=new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                String type = response.body().contentType().subtype();
                try {

                    if (response.code() != 200){
                        listener.onFail();
                        return;
                    }
                    if (type.equals("json")){
                        listener.onFail();
                        return;
                    }
                    is = response.body().byteStream();//以字节流的形式拿回响应实体内容
                    long total = response.body().contentLength();
                    //apk保存路径
                    final String fileDir = isExistDir(saveDir);
                    //文件
                    File file = new File(fileDir, fileName);
                    fos = new FileOutputStream(file);

                    long sum = 0;
                    while ((len = is.read(bytes)) != -1) {
                        fos.write(bytes, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        listener.onDowloadProges(progress);
                    }
                    fos.flush();
                    listener.onSuccess("0", saveDir + "/" + fileName);

                } catch (IOException e) {
                    listener.onFail();
                    e.printStackTrace();
                } finally {
                    if (is != null) is.close();
                    if (fos != null) fos.close();
                }
            }
        });

    }

}
