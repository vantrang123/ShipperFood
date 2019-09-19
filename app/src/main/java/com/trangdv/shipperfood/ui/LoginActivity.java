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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trangdv.shipperfood.R;
import com.trangdv.shipperfood.common.Common;
import com.trangdv.shipperfood.model.Shipper;
import com.trangdv.shipperfood.utils.SharedPrefs;


public class LoginActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 2019;
    public static final String KEY_PHONENUMBER = "key phonenumber address";
    public static final String KEY_PASSWORD = "key password";
    public static final String SAVE_USER = "save user";

    private TextView dispatch_signup;
    private EditText edt_phonenumber;
    private EditText edt_password;
    private FloatingActionButton fab;

    private String phonenumber;
    private String password;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference table_user = database.getReference("Shippers");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inits();
    }

    private void inits() {


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
                DispatchSignup();
            }
        });
    }

    private void DispatchSignup() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
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
                    authLogin();
                }

            }
        });
    }

    private void getTextfromEdt() {
        phonenumber = edt_phonenumber.getText().toString();
        password = edt_password.getText().toString();
    }

    private void authLogin() {
        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check if user not exist in database
                if (dataSnapshot.child(phonenumber).exists()) {
                    Shipper shipper = dataSnapshot.child(phonenumber).getValue(Shipper.class);
                    shipper.setPhone(phonenumber);

                    if (shipper.getPassword().equals(password)) {
                        SharedPrefs.getInstance().put(SplashActivity.CHECK_ALREADLY_LOGIN, 1);

                        //save shipper in share pref
                        SharedPrefs.getInstance().put(SAVE_USER, shipper);
                        intoHome(shipper);
                    } else {
                        Toast.makeText(LoginActivity.this, "Wrong Password !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Shipper not exist in Database !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void intoHome(Shipper shipper) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        Common.currentShipper = shipper;

        startActivity(intent);
        finish();
    }

    private void setTextintoEdt() {
        edt_phonenumber.setText(phonenumber);
        edt_password.setText(password);
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
}
