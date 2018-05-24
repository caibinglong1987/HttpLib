package com.cblong.http;

import android.util.Log;


import com.cblong.http.callback.HttpCallback;

import org.json.JSONObject;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;

public abstract class HexHttpManager {

    protected boolean isDebug = true;
    protected final String TAG = HexHttpManager.class.getName();

    //模型设置
    public interface Method {
        int GET = 0;
        int POST = 1;
    }

    public abstract String getRequest(String url, Map<String, String> params);

    public abstract String postRequest(String url, Map<String, String> params);

    public abstract String postRequest(String url, JSONObject json);

    public abstract void Request(int method, String url, Map<String, String> params, HttpCallback callback);

    public abstract String upload(String urlStr, String uploadKey, File file, MediaType mediaType);

    public abstract String upload(String urlStr, Map<String, String> params, String uploadKey, File file, MediaType mediaType);

    public abstract void download(String urlStr);

    /**
     * 拼接地址
     *
     * @param method get 拼接 ? post 原地址返回
     * @param url    地址
     * @param params 参数
     * @return string
     */
    protected String restructureURL(int method, String url, Map<String, String> params) {
        if (method == Method.GET && params != null) {
            url = url + "?" + encodeParameters(params);
        }
        log_e(TAG, url);
        return url;
    }

    /**
     * 参数的键和值进行组装
     *
     * @param params 参数
     * @return 结果
     */
    private String encodeParameters(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        StringBuilder encodedParams = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            encodedParams.append(entry.getKey());
            encodedParams.append('=');
            encodedParams.append(entry.getValue());
            encodedParams.append('&');
        }
        String result = encodedParams.toString();
        log_e(TAG, result);
        return result.substring(0, result.length() - 1);
    }

    protected void log_e(String tag, String log) {
        if (isDebug) {
            Log.e(tag, log);
        }
    }

}
