package com.example.cuidadodelambiente.Entidades;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton instanceSingleton;
    private RequestQueue requestQueue;
    private static Context contexto;

    private VolleySingleton(Context context) {
        contexto = context;
        requestQueue = getRequestQueue();

    }

    public static synchronized VolleySingleton getinstance(Context context) {
        if (instanceSingleton == null) {
            instanceSingleton = new VolleySingleton(context);
        }
        return instanceSingleton;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(contexto.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }

}

