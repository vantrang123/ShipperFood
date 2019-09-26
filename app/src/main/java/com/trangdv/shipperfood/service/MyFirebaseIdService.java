package com.trangdv.shipperfood.service;


import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.trangdv.shipperfood.common.Common;
import com.trangdv.shipperfood.model.Token;


public class MyFirebaseIdService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        /*InstanceIdResult instanceIdResult = null;
        instanceIdResult.getToken();*/
        updateToServer(refreshedToken);
    }

    private void updateToServer(String refreshedToken) {

        if (Common.currentShipper != null) {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference tokens = db.getReference("Tokens");
            Token data = new Token(refreshedToken, true);
            // false because token send from client app

            tokens.child(Common.currentShipper.getPhone()).setValue(data);
        }
    }
}

