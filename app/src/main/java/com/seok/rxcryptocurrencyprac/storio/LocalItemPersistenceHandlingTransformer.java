package com.seok.rxcryptocurrencyprac.storio;

import android.content.Context;

import com.seok.rxcryptocurrencyprac.StockUpdate;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;

public class LocalItemPersistenceHandlingTransformer implements FlowableTransformer<StockUpdate, StockUpdate> {

    private Context context;

    public LocalItemPersistenceHandlingTransformer(Context context) {
        this.context = context;
    }

    public static LocalItemPersistenceHandlingTransformer addLocalItemPersistenceHandling(Context context) {
        return new LocalItemPersistenceHandlingTransformer(context);
    }

    @Override
    public Publisher<StockUpdate> apply(Flowable<StockUpdate> upstream) {
        return upstream
                .doOnNext(this::saveStockUpdate)
                .onExceptionResumeNext(
                        StorIOFactory.createLocalDbStockUpdateRetrievalFlowable(context) // 네트워크 연결이 안되어 있으면 저장된 db 에서 가져온다.
                );
    }

    // 데이터 베이스에 저장
    private void saveStockUpdate(StockUpdate stockUpdate) {
        // 아이템 구독을 백그라운드에서 실행한다. (asRxSingle() 과 subscribe 에 의해 행해진다.)
        StorIOFactory.get(context)
                .put()
                .object(stockUpdate)
                .prepare()
                .asRxSingle()
                .subscribe();
    }
}
