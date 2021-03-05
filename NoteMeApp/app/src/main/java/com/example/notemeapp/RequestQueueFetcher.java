package com.example.notemeapp;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueFetcher {
    private static RequestQueueFetcher requestQueueFetcher;
    private RequestQueue reqQueue;

    public RequestQueueFetcher(Context context){
        reqQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized RequestQueueFetcher getInstance(Context context){
        if (requestQueueFetcher == null){
            requestQueueFetcher = new RequestQueueFetcher(context);
        }
        return requestQueueFetcher;
    }

    public RequestQueue getQueue() {
        return reqQueue;
    }
}
