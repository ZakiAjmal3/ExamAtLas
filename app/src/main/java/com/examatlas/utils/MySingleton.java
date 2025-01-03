package com.examatlas.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleton {
    private static MySingleton mInstance;
    private RequestQueue mRequestqueue;
    private static Context mcontext;

    public MySingleton(Context context) {
        mcontext = context;
        mRequestqueue = getRequestQueue();
    }

    public static synchronized MySingleton getInstance(Context context){
        if(mInstance == null)
            mInstance = new MySingleton(context);
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if(mRequestqueue == null)
            mRequestqueue = Volley.newRequestQueue(mcontext.getApplicationContext());
        return mRequestqueue;
    }

    public<T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }
}
