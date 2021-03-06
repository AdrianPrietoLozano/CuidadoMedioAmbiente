package com.example.cuidadodelambiente.data.network;

import com.example.cuidadodelambiente.data.interceptors.AuthInterceptor;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClientInstance {

    private static Retrofit retrofit;
    //private static final String BASE_URL = "https://eventoslimpieza.000webhostapp.com/EventosLimpieza/";
    //private static final String BASE_URL = "http://10.0.0.6/EventosLimpieza/";
    private static final String BASE_URL = "http://10.0.0.6/EventosAPI/public/";

    public static Retrofit getRetrofitInstance() {


        AuthInterceptor authInterceptor = new AuthInterceptor();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(authInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build();



        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }
}
