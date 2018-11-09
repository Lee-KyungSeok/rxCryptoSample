package com.seok.rxcryptocurrencyprac.bittrex;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBittrexServiceFactory {

    private static final String BASE_URL = "https://bittrex.com/api/v1.1/";

    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient client = new OkHttpClient
            .Builder()
            .addInterceptor(interceptor)
            .build();

    Retrofit retrofit = new Retrofit
            .Builder()
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build();

    public BittrexService create() {
        return retrofit.create(BittrexService.class);
    }
}
