package com.cblong.http.callback;

/**
 * @author long
 *         统一封装web接口中间层
 **/
public abstract class AbsHttpCallback implements HttpCallback {

    @Override
    public void onFailure(final Exception ex) {
    }

    @Override
    public void onSucceed(final String response) {
    }
}