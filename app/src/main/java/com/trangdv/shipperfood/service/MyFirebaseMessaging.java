package com.trangdv.shipperfood.service;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.trangdv.shipperfood.common.Common;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> dataRecv = remoteMessage.getData();
        if (dataRecv != null) {
            Common.showNotification(this,
                    new Random().nextInt(),
                    dataRecv.get(Common.NOTIFI_TITLE),
                    dataRecv.get(Common.NOTIFI_CONTENT),
                    null);
        }
    }
}
