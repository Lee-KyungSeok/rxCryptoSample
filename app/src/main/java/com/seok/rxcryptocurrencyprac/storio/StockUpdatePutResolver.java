package com.seok.rxcryptocurrencyprac.storio;

import android.content.ContentValues;

import com.pushtorefresh.storio3.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio3.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio3.sqlite.queries.UpdateQuery;
import com.seok.rxcryptocurrencyprac.StockUpdate;

import androidx.annotation.NonNull;

public class StockUpdatePutResolver extends DefaultPutResolver<StockUpdate> {

    @NonNull
    @Override
    protected InsertQuery mapToInsertQuery(@NonNull StockUpdate object) { // 데이터를 삽입할 올바른 테이블을 찾는데 사용됨
        return InsertQuery.builder()
                .table(StockUpdateTable.TABLE)
                .build();
    }

    @NonNull
    @Override
    protected UpdateQuery mapToUpdateQuery(@NonNull StockUpdate object) { // 객체가 이미 데이터베이스에 있는지 조회하는 쿼리를 실행 (해당ID 를 발견하면 새요소를 삽입하는 대신 업데이트 한다)
        return UpdateQuery.builder()
                .table(StockUpdateTable.TABLE)
                .where(StockUpdateTable.Columns.ID + " = ?")
                .whereArgs(object.getId())
                .build();
    }

    @NonNull
    @Override
    protected ContentValues mapToContentValues(@NonNull StockUpdate entity) { // 도메인 객체의 값을 SQLite 데이터베이스가 이해할 수 있는 ContentValues 객체로 매핑한다.
        final ContentValues contentValues = new ContentValues();

        contentValues.put(StockUpdateTable.Columns.ID, entity.getId());
        contentValues.put(StockUpdateTable.Columns.STOCK_SYMBOL, entity.getStockSymbol());
        contentValues.put(StockUpdateTable.Columns.PRICE, getPrice(entity));
        contentValues.put(StockUpdateTable.Columns.DATE, entity.getDate());
        contentValues.put(StockUpdateTable.Columns.TWITTER_STATUS, entity.getTwitterStatus());

        return contentValues;
    }

//    private long getDate(@NonNull StockUpdate entity) {
//        return entity.getDate().getTime();
//    }

    private long getPrice(@NonNull StockUpdate entity) {
        // 소수점 8개를 앞으로 땡겨서 long type 으로 저장한다.(sqlite 타입 때문)
        return entity.getPrice().scaleByPowerOfTen(8).longValue();
    }
}
