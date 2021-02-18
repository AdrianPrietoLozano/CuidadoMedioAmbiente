package com.example.cuidadodelambiente.data.interceptors;

import android.util.Log;

import com.example.cuidadodelambiente.AppContext;
import com.example.cuidadodelambiente.data.models.User;
import com.example.cuidadodelambiente.data.models.UserLocalStore;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private UserLocalStore userLocalStore;

    public AuthInterceptor() {
        userLocalStore = UserLocalStore.getInstance(AppContext.getContext());
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        if (userLocalStore.getUsuarioLogueado() != null) {
            Log.e("TOKEN", userLocalStore.getUsuarioLogueado().getToken());
            builder.addHeader("Authorization", "Bearer " + userLocalStore.getUsuarioLogueado().getToken());
        }

        return chain.proceed(builder.build());
    }
}
