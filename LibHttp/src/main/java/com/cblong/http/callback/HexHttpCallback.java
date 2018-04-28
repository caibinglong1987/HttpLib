package com.cblong.http.callback;


import java.util.Map;

/**
 * Created by caibinglong
 * on 17-3-2.
 */
public interface HexHttpCallback {

    /****失败处理***/
    void onFailure(Map<String, ?> errorMap);

    /***成功返回***/
    void onSuccess(String response);
    /**请求错误返回 **/
}
