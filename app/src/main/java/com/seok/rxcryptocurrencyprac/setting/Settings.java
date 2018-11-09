package com.seok.rxcryptocurrencyprac.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.f2prateek.rx.preferences2.RxSharedPreferences;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

public class Settings {

    private static Settings INSTANCE;

    private Subject<List<String>> keywordsSubject = BehaviorSubject.create();
    private Subject<List<String>> symbolsSubject = BehaviorSubject.create();

    private Settings(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        RxSharedPreferences rxPreferences = RxSharedPreferences.create(preferences);

        rxPreferences.getString("pref_keywords", "").asObservable()
                .filter(v-> !v.isEmpty()) // 비어있으면 새 값을 가져오지 않는다.
                .map(value -> value.split(" ")) // 값이 한줄에 입력되고 공백으로 구분됨을 가정했으므로 이를 split 해준다
                .map(Arrays::asList) // 리스트로 변환한다.
                .subscribe(keywordsSubject); // 서브젝트를 연결(구독) 한다.

        rxPreferences.getString("pref_symbols", "").asObservable()
                .filter(v-> !v.isEmpty())
                .map(String::toUpperCase)
                .map(value -> value.split(" "))
                .map(Arrays::asList)
                .subscribe(symbolsSubject);
    }

    public synchronized static Settings get(Context context) {
        if(INSTANCE != null) {
            return INSTANCE;
        }

        INSTANCE = new Settings(context);

        return INSTANCE;
    }
    public Observable<List<String>> getMonitoredKeywords() {
        return keywordsSubject;

    }

    public Observable<List<String>> getMonitoredSymbols() {
        return symbolsSubject;
    }
}
