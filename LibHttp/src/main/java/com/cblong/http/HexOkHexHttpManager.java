package com.cblong.http;

import android.os.StrictMode;
import android.text.TextUtils;


import com.cblong.http.callback.HttpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by caibinglong
 * on 2017/9/26.
 */

public class HexOkHexHttpManager extends HexHttpManager {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType XML = MediaType.parse("application/xml; charset=utf-8");
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    private static final boolean isDebug = false;

    private static HexOkHexHttpManager hexHttp;
    private OkHttpClient.Builder builder;
    private long connectTime = 60, writeTime = 30, readTime = 60;

    public static HexOkHexHttpManager getInstance() {
        if (hexHttp == null) {
            synchronized (HexOkHexHttpManager.class) {
                if (hexHttp == null) {
                    hexHttp = new HexOkHexHttpManager();
                }
            }
        }
        return hexHttp;
    }

    public HexOkHexHttpManager setConnectTimeout(long timeout) {
        this.connectTime = timeout;
        return hexHttp;
    }

    public HexOkHexHttpManager setWriteTimeout(long timeout) {
        this.writeTime = timeout;
        return hexHttp;
    }

    public HexOkHexHttpManager setReadTimeout(long timeout) {
        this.readTime = timeout;
        return hexHttp;
    }

    private HexOkHexHttpManager() {
        this.builder = new OkHttpClient.Builder();
    }

    private OkHttpClient getClient() {
        return this.builder
                .connectTimeout(connectTime, TimeUnit.SECONDS)
                .writeTimeout(writeTime, TimeUnit.SECONDS)
                .readTimeout(readTime, TimeUnit.SECONDS)
                .build();
    }

    //严格控制http请求
    private void init() {
        if (isDebug) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads().detectDiskWrites().detectNetwork()
                    .penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                    .penaltyLog().penaltyDeath().build());
        }
    }


    /**
     * post body json数据
     * 回调出结果
     *
     * @param url      请求地址
     * @param callback 反馈结果
     * @param params   参数
     */
    @Override
    public void Request(int method, String url, Map<String, String> params, final HttpCallback callback) {
        if (url == null || url.equals("")) {
            return;
        }
        if (params == null) {
            return;
        }
        init();//Android 2.3提供一个称为严苛模式（StrictMode）的调试特性
        OkHttpClient client = getClient();
        Request request;
        if (method == HexHttpManager.Method.GET) {
            String strUrl = restructureURL(HexHttpManager.Method.GET, url, params);
            request = new Request.Builder().url(strUrl).build();
        } else {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
            RequestBody requestBody = builder.build();
            request = new Request.Builder().url(url).post(requestBody).build();
        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String result = response.body().string();
                    callback.onSucceed(result);
                }
            }
        });
    }

    /**
     * get 请求
     *
     * @param url    地址
     * @param params 参数
     * @return 结果
     */
    @Override
    public String getRequest(String url, Map<String, String> params) {
        if (url == null || url.equals("")) {
            return null;
        }
        init();//Android 2.3提供一个称为严苛模式（StrictMode）的调试特性
        String strUrl = restructureURL(HexHttpManager.Method.GET, url, params);
        //创建okHttpClient对象
        OkHttpClient client = getClient();
        //创建一个Request
        Request request = new Request.Builder().url(strUrl).build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Post Json 格式请求
     *
     * @param url        地址
     * @param jsonObject json
     * @return string
     */
    public String postRequest(String url, JSONObject jsonObject) {
        if (url == null || url.equals("")) {
            return null;
        }
        init();//Android 2.3提供一个称为严苛模式（StrictMode）的调试特性
        OkHttpClient client = getClient();
        String json = jsonObject.toString();
        if (json != null) {
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder().url(url).post(body).build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Post Json 格式请求
     *
     * @param url       地址
     * @param xmlString string
     * @return string
     */
    public String postXmlRequest(String url, String xmlString) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(xmlString)) {
            return null;
        }
        init();//Android 2.3提供一个称为严苛模式（StrictMode）的调试特性
        OkHttpClient client = getClient();

        RequestBody body = RequestBody.create(XML, xmlString);
        Request request = new Request.Builder().url(url).post(body).build();
        try {
            Response response = client.newCall(request).execute();
            try {
                return response.body().string();
            } catch (IOException ex) {
                ex.getMessage();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String postRequest(String url, Map<String, String> params) {
        if (url == null || url.equals("")) {
            return null;
        }
        init();//Android 2.3提供一个称为严苛模式（StrictMode）的调试特性
        OkHttpClient client = getClient();
        String json = getJson(params);
        if (json != null) {
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder().url(url).post(body).build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * post from 提交请求(一般使用这个)
     */
    public String postRequest(String url, RequestBody params) {
        if (url == null || url.equals("")) {
            return null;
        }
        init();//Android 2.3提供一个称为严苛模式（StrictMode）的调试特性
        OkHttpClient client = getClient();
        Request request = new Request.Builder().url(url).post(params).build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * post  from 请求(一般使用这个)
     */
    public void postRequest(String url, RequestBody params, final HttpCallback callback) {
        if (url == null || url.equals("")) {
            return;
        }
        init();//Android 2.3提供一个称为严苛模式（StrictMode）的调试特性
        OkHttpClient client = getClient();
        Request request = new Request.Builder().url(url).post(params).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    callback.onSucceed(result);
                }
            }
        });
    }

    /**
     * 转换json 字符串
     *
     * @param params Map<String, String>
     * @return string
     */
    private String getJson(Map<String, String> params) {
        JSONObject jsonObject = new JSONObject();
        boolean error = false;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key == null || value == null) {
                continue;
            }
            try {
                jsonObject.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
                error = true;
            }
            if (error) {
                break;
            }
        }
        return jsonObject.toString();
    }

    /**
     * 上传单个文件
     *
     * @param urlStr    地址
     * @param uploadKey key
     * @param file      文件
     * @param mediaType type
     * @return str
     */
    @Override
    public String upload(String urlStr, String uploadKey, File file, MediaType mediaType) {
        Request request = getFileUploadRequest(urlStr, uploadKey, file, mediaType);
        String result = null;
        try {
            Response response = getClient().newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String upload(String urlStr, Map<String, String> params, String uploadKey, File file, MediaType mediaType) {
        Request request = getFileUploadRequest(urlStr, params, uploadKey, file, mediaType);
        String result = null;
        try {
            Response response = getClient().newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 下载
     * 文件存在的情况下可判断服务端文件是否已经更改
     *
     * @param url        地址
     * @param lastModify Range
     * @return Response
     * @throws IOException
     */
    public Response initRequest(String url, String lastModify) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Range", "bytes=0-")
                .header("If-Range", lastModify)
                .build();

        return builder.build().newCall(request).execute();
    }

    /**
     * 异步（根据断点请求）
     *
     * @param url      地址
     * @param start    开始
     * @param end      结束
     * @param callback 回调
     */
    public void downloadEnqueue(String url, long start, long end, final HttpCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .header("Range", "bytes=" + start + "-" + end)
                .build();

        Call call = builder.build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSucceed(response.body().string());
                }
            }
        });
    }

    /**
     * 下载
     *
     * @param url 地址
     * @return Response
     */
    public Response downloadEnqueue(String url) {
        Request request = new Request.Builder().url(url).build();
        try {
            return getClient().newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载
     *
     * @param url 地址
     * @return Response
     */
    public boolean downloadAndSave(String url, String path, String fileName) {
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = getClient().newCall(request).execute();
            if (response != null) {
                File file = this.createFile(path, fileName);
                if (file == null) {
                    return false;
                }
                InputStream is = response.body().byteStream();
                BufferedInputStream input = new BufferedInputStream(is);
                try {
                    OutputStream output = new FileOutputStream(file);
                    byte[] data = new byte[1024];
                    int count;
                    try {
                        while ((count = input.read(data)) != -1) {
                            output.write(data, 0, count);
                        }
                        output.flush();
                        output.close();
                        input.close();
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 同步请求
     *
     * @param url 地址
     * @return Response
     * @throws IOException
     */
    public Response initRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Range", "bytes=0-")
                .build();

        return builder.build().newCall(request).execute();
    }


    @Override
    public void download(String url) {
    }

    /**
     * 文件上传
     *
     * @param url       地址
     * @param name      名称
     * @param file      文件
     * @param mediaType Media Type
     * @return Request
     */
    private static Request getFileUploadRequest(String url, String name, File file, MediaType mediaType) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if (file.exists() && name != null && !"".equals(name)) {
            builder.addFormDataPart(name, file.getName(), RequestBody.create(mediaType, file));
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    /**
     * @param url       地址
     * @param params    Map<String, String>
     * @param name      文件名称
     * @param file      文件
     * @param mediaType 类型
     * @return Request
     */
    private static Request getFileUploadRequest(String url, Map<String, String> params, String name, File file, MediaType mediaType) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if (file.exists() && name != null && !"".equals(name)) {
            builder.addFormDataPart(name, file.getName(), RequestBody.create(mediaType, file));
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.addFormDataPart(key, value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }


    /**
     * @param url  地址
     * @param name 名字
     * @param file 文件
     * @return Request
     */
    public static Request getPicUploadRequest(String url, String name, File file) {
        MediaType PNG_TYPE = MediaType.parse("image/png");
        MediaType JPG_TYPE = MediaType.parse("image/jpg");

        MediaType mediaType = PNG_TYPE;
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if (file.exists() && name != null && !"".equals(name)) {
            if (".jpg".endsWith(file.getName())) {
                mediaType = JPG_TYPE;
            }
            if (".png".endsWith(file.getName())) {
                mediaType = PNG_TYPE;
            }
            builder.addFormDataPart(name, file.getName(), RequestBody.create(mediaType, file));
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    /**
     * 图片上传
     *
     * @param url    地址
     * @param name   名字
     * @param file   文件
     * @param params 参数
     * @return request
     */
    public static Request getPicUploadRequest(String url, String name, File file, Map<String, String> params) {
        MediaType PNG_TYPE = MediaType.parse("image/png");
        MediaType JPG_TYPE = MediaType.parse("image/jpg");

        MediaType mediaType = PNG_TYPE;
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if (file.exists() && name != null && !"".equals(name)) {
            if (".jpg".endsWith(file.getName())) {
                mediaType = JPG_TYPE;
            }
            if (".png".endsWith(file.getName())) {
                mediaType = PNG_TYPE;
            }
            builder.addFormDataPart(name, file.getName(), RequestBody.create(mediaType, file));
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.addFormDataPart(key, value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    /**
     * 创建文件
     *
     * @param path 路径
     * @param name 文件名称
     * @return File
     */
    private synchronized File createFile(String path, String name) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(name)) {
            return null;
        }

        File parentFile = new File(path);
        if (!parentFile.exists()) {
            parentFile.mkdir();
        }

        File file = new File(parentFile, name);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

}
