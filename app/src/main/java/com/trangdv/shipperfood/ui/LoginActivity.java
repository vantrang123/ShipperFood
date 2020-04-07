package com.trangdv.shipperfood.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trangdv.shipperfood.R;
import com.trangdv.shipperfood.common.Common;
import com.trangdv.shipperfood.model.Shipper;
import com.trangdv.shipperfood.presenter.login.ILoginPresenter;
import com.trangdv.shipperfood.presenter.login.LoginPresenter;
import com.trangdv.shipperfood.retrofit.IAnNgonAPI;
import com.trangdv.shipperfood.retrofit.RetrofitClient;
import com.trangdv.shipperfood.utils.DialogUtils;
import com.trangdv.shipperfood.utils.SharedPrefs;
import com.trangdv.shipperfood.view.ILoginView;

import io.reactivex.disposables.CompositeDisposable;

import static com.trangdv.shipperfood.ui.VerifyPhoneActivity.SAVE_SHIPPER;


public class LoginActivity extends AppCompatActivity implements ILoginView {

    IAnNgonAPI anNgonAPI;
    CompositeDisposable compositeDisposable;
    ILoginPresenter iLoginPresenter;

    public static final int REQUEST_CODE = 2019;
    public static final String KEY_PHONENUMBER = "key phonenumber address";
    public static final String KEY_PASSWORD = "key password";
    private static final String TAG = "LoginActivity";

    private TextView dispatch_signup;
    private EditText edt_phonenumber;
    private EditText edt_password;
    private FloatingActionButton fab;

    private String phonenumber;
    private String password;

    DialogUtils dialogUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {
        anNgonAPI = RetrofitClient.getInstance(Common.API_ANNGON_ENDPOINT).create(IAnNgonAPI.class);
        compositeDisposable = new CompositeDisposable();
        iLoginPresenter = new LoginPresenter(this, anNgonAPI, compositeDisposable);
        dialogUtils = new DialogUtils();

        dispatch_signup = findViewById(R.id.dispatch_signup);
        setClickDispatchSignup();
        fab = findViewById(R.id.fab_login);
        setOnClickFab();
        edt_phonenumber = findViewById(R.id.phonenumber_edt_login);
        edt_password = findViewById(R.id.password_edt_login);
    }

    private void setClickDispatchSignup() {
        dispatch_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchSignup();
            }
        });
    }

    private void dispatchSignup() {
        dialogUtils.showProgress(this);
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
//        startActivityForResult(intent, REQUEST_CODE);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            phonenumber = data.getExtras().getString(KEY_PHONENUMBER, "");
            password = data.getExtras().getString(KEY_PASSWORD, "");

            setTextintoEdt();

        }
    }

    private void setOnClickFab() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTextfromEdt();

                if (phonenumber.equals("")==false && password.equals("")==false) {
                    dialogUtils.showProgress(LoginActivity.this);
                    iLoginPresenter.onLogin(phonenumber, password);
                }

            }
        });
    }

    private void getTextfromEdt() {
        phonenumber = edt_phonenumber.getText().toString();
        password = edt_password.getText().toString();
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        startActivity(intent);
        finish();
    }

    private void setTextintoEdt() {
        edt_phonenumber.setText(phonenumber);
        edt_password.setText(password);
    }

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

    //
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onLoginSuccess(Shipper user) {
        dialogUtils.dismissProgress();
        // save curreentUser
        Common.currentShipper = user;

        SharedPrefs.getInstance().put(SplashActivity.CHECK_ALREADLY_LOGIN, 2);

        //save user in share pref
        SharedPrefs.getInstance().put(SAVE_SHIPPER, Common.currentShipper);

        gotoMainActivity();
    }

    @Override
    public void onLoginError(String message) {
        Toast.makeText(this, "[ERROR]" + message, Toast.LENGTH_SHORT).show();
        dialogUtils.dismissProgress();
    }
}
