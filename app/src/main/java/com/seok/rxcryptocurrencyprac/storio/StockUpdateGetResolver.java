package com.seok.rxcryptocurrencyprac.storio;

import android.database.Cursor;

import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.operations.get.DefaultGetResolver;
import com.seok.rxcryptocurrencyprac.StockUpdate;

import java.math.BigDecimal;

import androidx.annotation.NonNull;

public class StockUpdateGetResolver extends DefaultGetResolver<StockUpdate> {

    @NonNull
    @Override
    public StockUpdate mapFromCursor(@NonNull StorIOSQLite storIOSQLite, @NonNull Cursor cursor) {

        final int id = cursor.getInt(cursor.getColumnIndexOrThrow(StockUpdateTable.Columns.ID));
        final String dateLong = cursor.getString(cursor.getColumnIndexOrThrow(StockUpdateTable.Columns.DATE));
        final long priceLong = cursor.getLong(cursor.getColumnIndexOrThrow(StockUpdateTable.Columns.PRICE));
        final String stockSymbol = cursor.getString(cursor.getColumnIndexOrThrow(StockUpdateTable.Columns.STOCK_SYMBOL));
        final String twitterStatus = cursor.getString(cursor.getColumnIndexOrThrow(StockUpdateTable.Columns.TWITTER_STATUS));

        BigDecimal price = getPrice(priceLong);

        final StockUpdate stockUpdate = new StockUpdate(stockSymbol, price, dateLong, twitterStatus);
        stockUpdate.setId(id);

        return stockUpdate;
    }

    private BigDecimal getPrice(long priceLong) {
        // 8개를 되돌려서 가져온다.
        return new BigDecimal(priceLong).scaleByPowerOfTen(-8);
    }
}
