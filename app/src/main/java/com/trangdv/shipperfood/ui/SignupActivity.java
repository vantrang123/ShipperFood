package com.trangdv.shipperfood.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.trangdv.shipperfood.model.Shipper;

public class SignupActivity extends AppCompatActivity {

    private EditText edt_username;
    private EditText edt_phonenumber;
    private EditText edt_password;
    private FloatingActionButton fab;

    private String name;
    private String phonenumber;
    private String password;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference table_user = database.getReference("Shippers");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        inits();


    }

    private void inits() {
        edt_username = findViewById(R.id.username_edt);
        edt_phonenumber = findViewById(R.id.phonenumber_edt_signup);
        edt_password = findViewById(R.id.password_edt_signup);

        fab = findViewById(R.id.fab_back_login);
        setClickNext();
    }

    private void setClickNext() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTextfromEdt();
                if (name.equals("")==false && phonenumber.equals("")==false && password.equals("")==false) {
                    createUser();
                }
            }
        });
    }

    private void createUser() {
        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check if user not exist in database
                if (dataSnapshot.child(phonenumber).exists()) {
                    Shipper shipper = dataSnapshot.child(phonenumber).getValue(Shipper.class);
                    Toast.makeText(SignupActivity.this, "Phone number already registered !", Toast.LENGTH_SHORT).show();
                }
                else {
                    Shipper shipper = new Shipper(name, password);
                    table_user.child(phonenumber).setValue(shipper);
                    Toast.makeText(SignupActivity.this, "Sign Up Successfully !", Toast.LENGTH_SHORT).show();
                    sendResult();
                    //finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getTextfromEdt() {
        phonenumber = edt_phonenumber.getText().toString();
        password = edt_password.getText().toString();
        name = edt_username.getText().toString();

    }

    private void sendResult() {

        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(LoginActivity.KEY_PHONENUMBER, phonenumber);
        bundle.putString(LoginActivity.KEY_PASSWORD, password);
        intent.putExtras(bundle);

        setResult(RESULT_OK, intent);

        finish();
    }


}
