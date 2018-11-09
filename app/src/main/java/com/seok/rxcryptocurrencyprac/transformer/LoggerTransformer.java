package com.seok.rxcryptocurrencyprac.transformer;

import android.util.Log;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;

public class LoggerTransformer<R> implements FlowableTransformer<R, R> {

    private final String tag;

    public LoggerTransformer(String tag) {
        this.tag = tag;
    }

    public static <R> LoggerTransformer<R> debugLog(String tag) {
        return new LoggerTransformer<>(tag);
    }

    @Override
    public Publisher<R> apply(Flowable<R> upstream) {
        return upstream
                .doOnNext(v -> log("doOnNext", v))
                .doOnError(error -> this.log("doOnError", error))
                .doOnComplete(() -> this.log("doOnComplete", upstream.toString()))
                .doOnTerminate(() -> this.log("doOnTerminate", upstream.toString()))
                .doOnSubscribe(v -> this.log("doOnSubscribe", upstream.toString()));
    }

    private void log(String stage, Object item) {
        Log.d("App-DEBUG:" + tag, stage + Thread.currentThread().getName() + ":" + item);
    }

    private void log(String stage, Throwable throwable) {
        Log.d("App-DEBUG:" + tag, stage + Thread.currentThread().getName() + ": error", throwable);
    }
}
