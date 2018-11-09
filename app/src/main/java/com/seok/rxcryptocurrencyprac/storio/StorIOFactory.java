package com.seok.rxcryptocurrencyprac.storio;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio3.sqlite.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResolver;
import com.pushtorefresh.storio3.sqlite.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio3.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio3.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio3.sqlite.queries.Query;
import com.seok.rxcryptocurrencyprac.StockUpdate;
import com.seok.rxcryptocurrencyprac.bittrex.BittrexService;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

public class StorIOFactory {

    private static StorIOSQLite INSTANCE;

    // 싱글턴으로 설정
    public synchronized static StorIOSQLite get(Context context) {
        if(INSTANCE != null) {
            return INSTANCE;
        }

        INSTANCE = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(new StorIODbHelper(context)) // storIO 가 원본 SQLite 데이터베이스에 엑세스할 수 있도록 한다.
                .addTypeMapping( // StockUpdate 클래스를 다룰 구성을 지정
                        StockUpdate.class,
                        SQLiteTypeMapping.<StockUpdate>builder()
                            .putResolver(new StockUpdatePutResolver())          // 쓰기 작업을 처리하는 데 사용할 클래스 지정
                            .getResolver(new StockUpdateGetResolver())          // 읽기가 처리되는지 지정하는 구성을 설정
                            .deleteResolver(new StockUpdateDeleteResolver())    // 항목이 코드에서 삭제될 때 데이터베이스의 레코드를 삭제하는 동작 구성
                            .build()
                ).build();

        return INSTANCE;
    }

    public static Flowable<StockUpdate> createLocalDbStockUpdateRetrievalFlowable(Context context) {
        return StorIOFactory.get(context)
                .get() // SELECT 쿼리 작성을 시작하도록 한다.
                .listOfObjects(StockUpdate.class) // 반환될 객체 유형 지정
                .withQuery(Query.builder() // SELECT 쿼리 생성
                        .table(StockUpdateTable.TABLE)
                        .orderBy("date DESC")
                        .limit(50)
                        .build())
                .prepare()
                .asRxFlowable(BackpressureStrategy.LATEST)
                .take(1) // 쿼리를 한번만 수신한다.
                .flatMap(Flowable::fromIterable);
    }
}
