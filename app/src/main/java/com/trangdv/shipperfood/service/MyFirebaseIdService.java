package com.trangdv.shipperfood.service;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.trangdv.shipperfood.common.Common;
import com.trangdv.shipperfood.model.Token;


public class MyFirebaseIdService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseIdService";
    String token = "";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }
                token = task.getResult().getToken();
            }
        });
        updateToServer(token);
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

