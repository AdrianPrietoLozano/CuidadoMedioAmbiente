package com.example.cuidadodelambiente;

import android.content.Intent;
import android.util.Log;

import com.example.cuidadodelambiente.data.models.User;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        if (!UserLocalStore.getInstance(getApplicationContext()).isUsuarioLogueado())
            return;

        if (!remoteMessage.getData().isEmpty()) {
            String title = remoteMessage.getData().get("title");
            String msg = remoteMessage.getData().get("message");

            MyNotificationManager myNotificationManager = new MyNotificationManager(getApplicationContext());
            myNotificationManager.showBigNotification(title, msg);
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Log.e("TOKEN", s);
    }
}
