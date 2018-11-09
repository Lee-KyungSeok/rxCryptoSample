package com.seok.rxcryptocurrencyprac.transformer;

import android.content.Context;

import org.reactivestreams.Publisher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;

// 범용 파일 캐싱 관련 코드를 관장한다.
public class FileCacheFlowableTransformer<R> implements FlowableTransformer<R, R> {
    
    private final String filename;
    private final Context context;

    public FileCacheFlowableTransformer(String filename, Context context) {
        this.filename = filename;
        this.context = context;
    }

    public static <R> FileCacheFlowableTransformer<R> cacheToLocalFileNamed(String filename, Context context) {
        return new FileCacheFlowableTransformer<R>(filename, context);
    }

    @Override
    public Publisher<R> apply(Flowable<R> upstream) {
        return readFromFile()
                .onExceptionResumeNext(
                        upstream.take(1) // 실행이 실패하면 원래의 upstream flowable 에서 아이템을 가져오고 (take1 을 사용하여 아이템을 지속해서 저장시키지 않는다 => 파일을 덮어쓰지 않게 함)
                                .doOnNext(this::saveToFile)); // 결과를 저장한다.
    }


    private Flowable<R> readFromFile() {
        return Flowable.create(emitter -> {
            ObjectInputStream input = null;
            try {
                final FileInputStream fileInputStream = new FileInputStream(getFileName());
                input = new ObjectInputStream(fileInputStream);
                R foundObject = (R) input.readObject();
                emitter.onNext(foundObject);
            } catch (Exception e) {
                emitter.onError(e);
            } finally {
                if (input != null) {
                    input.close();
                }
                emitter.onComplete();
            }
        }, BackpressureStrategy.LATEST);
    }

    private void saveToFile(R r) throws IOException {
        ObjectOutputStream objectOutputStream = null;
        try {
            final FileOutputStream fileOutputStream =  new FileOutputStream(getFileName());
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(r);
        } finally {
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
        }
    }

    private String getFileName() {
        return context.getFilesDir().getAbsolutePath() + File.separator + filename;
    }
}
