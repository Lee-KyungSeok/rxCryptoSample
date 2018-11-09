package com.seok.rxcryptocurrencyprac.storio;

import com.pushtorefresh.storio3.sqlite.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio3.sqlite.queries.DeleteQuery;
import com.seok.rxcryptocurrencyprac.StockUpdate;

import androidx.annotation.NonNull;

public class StockUpdateDeleteResolver extends DefaultDeleteResolver<StockUpdate> {

    @NonNull
    @Override
    protected DeleteQuery mapToDeleteQuery(@NonNull StockUpdate object) {
        return DeleteQuery.builder()
                .table(StockUpdateTable.TABLE)
                .where(StockUpdateTable.Columns.ID + " = ?")
                .whereArgs(object.getId())
                .build();
    }
}
