package com.lon.rxjavaretrofit;


import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;

/*
 * 项目名:
 * 文件名:    BaseApi
 * 创建者:    long
 * 创建时间:  2017/5/3 on 14:08
 * 描述:     TODO
 */
public interface BaseApi {

    Retrofit getRetrofit();

    OkHttpClient.Builder setInterceptor(Interceptor interceptor);

    Retrofit.Builder setConverterFactory(Converter.Factory factory);

}
