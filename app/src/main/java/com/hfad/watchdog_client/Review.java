package com.hfad.watchdog_client;

/**
 * Created by Hertz on 28/02/2016.
 */
public class Review extends Post{

    public String fRating;

    public Review(String username, String post,String rating){
        super(username, post);
        fRating = rating;
    }
}

