package com.trangdv.shipperfood.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.trangdv.shipperfood.R;
import com.trangdv.shipperfood.common.Common;
import com.trangdv.shipperfood.model.UpdateShipperModel;
import com.trangdv.shipperfood.retrofit.IAnNgonAPI;
import com.trangdv.shipperfood.retrofit.RetrofitClient;
import com.trangdv.shipperfood.utils.DialogUtils;
import com.trangdv.shipperfood.utils.SharedPrefs;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class VerifyPhoneActivity extends AppCompatActivity {
    public static final String SAVE_SHIPPER= "save shipper";

    private String verificationId;
    private String phoneNumber;
    private String phoneFormat;
    private String userName;
    private String password;
    private FirebaseAuth mAuth;

    ProgressBar progressBar;
    TextInputEditText editText;
    AppCompatButton buttonSignIn;

    IAnNgonAPI anNgonAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    DialogUtils dialogUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);
        
        init();

        findViewById();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            phoneNumber = bundle.getString("phoneNumber", "");
            userName = bundle.getString("userName", "");
            password = bundle.getString("password", "");
            phoneFormat = bundle.getString("phoneFormat", "");
        }
//        phoneNumber = getIntent().getStringExtra("phoneNumber");
        sendVerificationCode(phoneFormat);

        /*// save phone number
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("USER_PREF",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("phoneNumber", phoneNumber);
        editor.apply();*/

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = editText.getText().toString().trim();

                if (code.isEmpty() || code.length() < 6) {

                    editText.setError("Enter code...");
                    editText.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        });

    }

    private void findViewById() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode(Locale.getDefault().getLanguage());

        progressBar = findViewById(R.id.progressbar);
        editText = findViewById(R.id.editTextCode);
        buttonSignIn = findViewById(R.id.buttonSignIn);
    }

    private void init() {
        anNgonAPI = RetrofitClient.getInstance(Common.API_ANNGON_ENDPOINT).create(IAnNgonAPI.class);
        dialogUtils = new DialogUtils();
    }

    private void verifyCode(String code) {
        progressBar.setVisibility(View.VISIBLE);
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithCredential(credential);
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error, please try later", Toast.LENGTH_SHORT).show();
        }

    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

//                            Intent intent = new Intent(VerifyPhoneActivity.this, MainActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//                            startActivity(intent);

                            //dialog thông báo xác nhận sdt thành công
                            //update user info
                            updateUserInfo(phoneNumber, userName, password);

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(VerifyPhoneActivity.this, "Done", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(VerifyPhoneActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void updateUserInfo(String phoneNumber, String userName, String password) {
        dialogUtils.showProgress(this);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            compositeDisposable.add(
                    anNgonAPI.updateShipper(Common.API_KEY,
                            phoneNumber,
                            userName,
                            password,
                            1,
                            firebaseUser.getUid())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<UpdateShipperModel>() {
                                           @Override
                                           public void accept(UpdateShipperModel updateShipperModel) throws Exception {
                                               if (updateShipperModel.isSuccess()) {
                                                   // save curreentUser
                                                   compositeDisposable.add(
                                                           anNgonAPI.getShipper(Common.API_KEY, phoneNumber, password)
                                                                   .subscribeOn(Schedulers.io())
                                                                   .observeOn(AndroidSchedulers.mainThread())
                                                                   .subscribe(shipperModel -> {
                                                                               if (shipperModel.isSuccess()) {
                                                                                   // save curreentUser
                                                                                   Common.currentShipper = shipperModel.getResult().get(0);

                                                                                   SharedPrefs.getInstance().put(SplashActivity.CHECK_ALREADLY_LOGIN, 2);

                                                                                   //save user in share pref
                                                                                   SharedPrefs.getInstance().put(SAVE_SHIPPER, Common.currentShipper);

                                                                                   dialogUtils.dismissProgress();
                                                                                   VerifyPhoneActivity.this.gotoMainActivity();
                                                                               } else {
                                                                                   dialogUtils.dismissProgress();
                                                                               }

                                                                           },
                                                                           throwable -> {
                                                                               dialogUtils.dismissProgress();
                                                                           }
                                                                   ));
//                                            Toast.makeText(this, "[UPDATE USER API SUCCESS!!!]" + updateUserModel.getMessage(), Toast.LENGTH_SHORT).show();
//                                            getCurrentUser();
                                               } else {
                                                   Toast.makeText(VerifyPhoneActivity.this, "[UPDATE USER API RETURN]" + updateShipperModel.getMessage(), Toast.LENGTH_SHORT).show();
                                                   dialogUtils.dismissProgress();
                                               }
                                           }
                                       },
                                    throwable -> {
                                        Toast.makeText(this, "[UPDATE USER API]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        dialogUtils.dismissProgress();
                                    }
                            )
            );
        } else {
//            sendResult();

        }
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(VerifyPhoneActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void sendVerificationCode(String number) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );

        progressBar.setVisibility(View.GONE);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                editText.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onStop() {
        dialogUtils.dismissProgress();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
