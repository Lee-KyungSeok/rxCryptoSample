package com.seok.rxcryptocurrencyprac;


import android.util.Log;

import io.reactivex.functions.Consumer;

// 에러 처리를 전역에서 할 수 있다.
public class ErrorHandler implements Consumer<Throwable> {

    private static final ErrorHandler INSTANCE = new ErrorHandler();

    public static ErrorHandler getInstance() {
        return INSTANCE;
    }

    private ErrorHandler() {

    }

    @Override
    public void accept(Throwable throwable) throws Exception {
        Log.e("App", "Error on " + Thread.currentThread().getName() + ":", throwable);
    }
}
