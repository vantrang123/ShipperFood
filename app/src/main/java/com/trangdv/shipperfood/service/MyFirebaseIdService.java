package com.trangdv.shipperfood.service;


import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.trangdv.shipperfood.common.Common;
import com.trangdv.shipperfood.model.Token;
import com.trangdv.shipperfood.ui.HomeActivity;
import com.trangdv.shipperfood.ui.MainActivity;


public class MyFirebaseIdService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseIdService";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener((Activity) getApplicationContext(),  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken",newToken);

                updateToServer(newToken);
            }
        });
    }

    private void updateToServer(String refreshedToken) {
        if (Common.currentShipper.getPhone() != null) {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference tokens = db.getReference("Tokens");
            Token data = new Token(refreshedToken, true);
            // false because token send from client app

            tokens.child(Common.currentShipper.getPhone()).setValue(data);
        }
    }
}
