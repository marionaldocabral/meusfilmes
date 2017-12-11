package com.cabral.marinho.meusfilmes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marinho on 06/12/17.
 */

public class FilmeDao {

    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    public FilmeDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }
    public void close() {
        dbHelper.close();
        db = null;
    }
    
    public List<Filme> getAll() {
        List<Filme> lista = new ArrayList<>();

        Cursor cursor = db.query(false, "Filme", null, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long vote_count = cursor.getLong(0);
            long id = cursor.getLong(1);
            int video = cursor.getInt(2);
            double vote_average = cursor.getDouble(3);
            String title = cursor.getString(4);
            long popularity = cursor.getLong(5);
            String poster_path = cursor.getString(6);
            String original_language = cursor.getString(7);
            String original_title = cursor.getString(8);
            String genre_ids = cursor.getString(9);
            String backdrop_path = cursor.getString(10);
            int adult = cursor.getInt(11);
            String overview = cursor.getString(12);
            String release_date = cursor.getString(13);
            Filme Filme = new Filme(vote_count, id, video, vote_average, title, popularity, poster_path, original_language, original_title, genre_ids, backdrop_path, adult, overview, release_date);
            lista.add(Filme);
            cursor.moveToNext();
        }

        return lista;
    }

    public void insert(Filme filme) {
        ContentValues cv = new ContentValues();

        cv.put("vote_count", filme.getVote_count());
        cv.put("video", filme.getVideo());
        cv.put("vote_average", filme.getVote_average());
        cv.put("title", filme.getTitle());
        cv.put("popularity", filme.getPopularity());
        cv.put("poster_path", filme.getPoster_path());
        cv.put("original_language", filme.getOriginal_language());
        cv.put("original_title", filme.getOriginal_title());
        cv.put("genre_ids", filme.getGenre_ids());
        cv.put("backdrop_path", filme.getBackdrop_path());
        cv.put("adult", filme.getAdult());
        cv.put("overview", filme.getOverview());
        cv.put("release_date", filme.getRelease_date());

        long id = db.insert("Filme", null, cv);
        Log.d("teste", "inserido Filme com id = " + id);
    }

    public void remove(long id) {
        db.delete("Filme", "_id = " + id, null);
    }
}