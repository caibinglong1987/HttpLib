package com.cblong.http.callback;

/**
 * @author by long
 *         on 16-3-2.
 */
public interface HttpCallback {

    /****
     * 失败处理
     ***/

    void onFailure(Exception ex);

    /***
     * 成功返回
     ***/
    void onSucceed(String response);
}
