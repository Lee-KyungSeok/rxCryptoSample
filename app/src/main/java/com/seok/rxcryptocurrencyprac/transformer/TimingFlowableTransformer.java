package com.seok.rxcryptocurrencyprac.transformer;

import android.util.Pair;

import org.reactivestreams.Publisher;

import java.util.Date;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.functions.Consumer;

// 아이템의 방출 시간을 추적
public class TimingFlowableTransformer<R> implements FlowableTransformer<R, R> {

    private final Consumer<Long> timerAction;

    public TimingFlowableTransformer(Consumer<Long> timerAction) {
        this.timerAction = timerAction;
    }

    public static <R> TimingFlowableTransformer<R> timeItems(Consumer<Long> timerAction) {
        return new TimingFlowableTransformer<>(timerAction);
    }

    @Override
    public Publisher<R> apply(Flowable<R> upstream) {
        return Flowable.combineLatest(
                    Flowable.just(new Date()), // 구독시점의 현재 시간을 기록한다.
                    upstream,                  // 원본 업스트림 옵서버블에서 생성한 항복과 병합한다.
                    Pair::create) // Pair 를 이용해서!
                .doOnNext((pair) -> {
                    // 초기시간과 현재시각 사이의 시간차를 계산한다.
                    Date currentTime = new Date();
                    long diff = currentTime.getTime() - pair.first.getTime();
                    long diffSeconds = diff / 1000;

                    // 시간차를 컨슈머에게 전달하여 우리가 제공할 일부 작업이 실행된다.
                    timerAction.accept(diffSeconds);
                }).map(pair -> pair.second); // 적용되기 전과 같은 방식으로 적용할 수 있도록 날짜 관련 항목이 없는 업스트림 옵서버블의 원본 값을 반환한다.
    }
}
