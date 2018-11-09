package com.seok.rxcryptocurrencyprac.storio;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

// SQLite Helper 클래스
public class StorIODbHelper extends SQLiteOpenHelper {

    public StorIODbHelper(@Nullable Context context) {
        super(context, "reactivestocks.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(StockUpdateTable.createTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
