package com.examatlas.utils;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingletonFragment {
    private static MySingletonFragment mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    // Private constructor to prevent instantiation from other classes
    private MySingletonFragment(Context context) {
        mContext = context.getApplicationContext(); // Use application context to avoid memory leaks
        mRequestQueue = getRequestQueue();
    }

    public static synchronized MySingletonFragment getInstance(Fragment fragment) {
        if (mInstance == null) {
            mInstance = new MySingletonFragment(fragment.requireActivity());
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        // Set the retry policy
        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, // Timeout in ms
                3, // Number of retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Backoff multiplier
        ));

        // Add the request to the queue
        getRequestQueue().add(request);
    }
}