package com.cabral.marinho.meusfilmes;

import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by marinho on 03/12/17.
 */

public class Filme {

    private long vote_count;
    private long id;
    private int video;
    private double vote_average;
    private String title;
    private long popularity;
    private String poster_path;
    private String original_language;
    private String original_title;
    private String genre_ids;
    private String backdrop_path;
    private int adult;
    private String overview;
    private String release_date;

    public Filme(){

    }

    public Filme(long vote_count, long id, int video, double vote_average, String title, long popularity, String poster_path, String original_language, String original_title, String genre_ids, String backdrop_path, int adult, String overview, String release_date){
        this.vote_count = vote_count;
        this.id = id;
        this.video = video;
        this.vote_average = vote_average;
        this.title = title;
        this.popularity = popularity;
        this.poster_path = poster_path;
        this.original_language = original_language;
        this.original_title = original_title;
        this.genre_ids = genre_ids;
        this.backdrop_path = backdrop_path;
        this.adult = adult;
        this.overview = overview;
        this.release_date = release_date;
    }

    public static Filme fromJSON(JSONObject obj) throws JSONException {
        long vote_count = Long.parseLong(obj.getString("vote_count"));

        long id = -1;
        if(obj.has("id"))
            id = obj.getLong("id");

        boolean r1 = obj.getBoolean("video");
        int video;
        if(r1 == true) video = 1;
        else video = 0;

        double vote_average = obj.getDouble("vote_average");
        String title = obj.getString("title");
        long popularity = obj.getLong("popularity");
        String poster_path = obj.getString("poster_path");
        String original_language = obj.getString("original_language");
        String original_title = obj.getString("original_title");

        JSONArray generos = obj.getJSONArray("genre_ids");
        String genre_ids = new String();
        genre_ids = generos.toString();

        String backdrop_path = obj.getString("backdrop_path");
        boolean r2 = obj.getBoolean("adult");
        int adult;
        if(r2 == true) adult = 1;
        else adult = 0;

        String overview = obj.getString("overview");
        String release_date = obj.getString("release_date");

        return new Filme(vote_count, id, video, vote_average, title, popularity, poster_path, original_language, original_title, genre_ids, backdrop_path, adult, overview, release_date);
    }

    public long getVote_count(){
        return vote_count;
    }

    public long getId(){
        return this.id;
    }

    public int getVideo(){
        return this.video;
    }

    public double getVote_average(){
        return this.vote_average;
    }

    public String getTitle(){
        return this.title;
    }

    public long getPopularity(){
        return this.popularity;
    }

    public String getPoster_path(){
        return this.poster_path;
    }

    public String getOriginal_language(){
        return this.original_language;
    }

    public String getOriginal_title(){
        return this.original_title;
    }

    public String getGenre_ids(){
        return this.genre_ids;
    }

    public String getBackdrop_path(){
        return this.backdrop_path;
    }

    public int getAdult(){
        return this.adult;
    }

    public String getOverview(){
        return this.overview;
    }

    public String getRelease_date(){
        return this.release_date;
    }
}


