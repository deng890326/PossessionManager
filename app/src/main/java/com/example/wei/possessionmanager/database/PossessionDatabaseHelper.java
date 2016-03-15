package com.example.wei.possessionmanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.wei.possessionmanager.database.PossessionDatabase.ItemTable;

/**
 * Created by wei on 2016/2/28 0028.
 */
public class PossessionDatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    public PossessionDatabaseHelper(Context context) {
        super(context, PossessionDatabase.NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + ItemTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                ItemTable.Cols.UUID + ", " +
                ItemTable.Cols.NAME + ", " +
                ItemTable.Cols.DATE + ", " +
                ItemTable.Cols.OWNER +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
