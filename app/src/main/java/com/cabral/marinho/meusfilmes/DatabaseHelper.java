package com.cabral.marinho.meusfilmes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by marinho on 06/12/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int versao = 1;

    public DatabaseHelper(Context context) {
        super(context, "banco.db", null, versao);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE filme(" +
                "   vote_count INTEGER," +
                "   _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "   video INTEGER," +
                "   vote_average REAL," +
                "   title TEXT," +
                "   popularity INTEGER," +
                "   poster_path TEXT," +
                "   original_language TEXT," +
                "   original_title TEXT," +
                "   genre_ids TEXT," +
                "   backdrop_path TEXT," +
                "   adult INTEGER," +
                "   overview TEXT," +
                "   release_date TEXT," +
                "   codigo TEXT);"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
    }
}