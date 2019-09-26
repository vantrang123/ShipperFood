package com.trangdv.shipperfood.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.trangdv.shipperfood.R;
import com.trangdv.shipperfood.common.Common;
import com.trangdv.shipperfood.model.Shipper;
import com.trangdv.shipperfood.utils.SharedPrefs;

import static com.trangdv.shipperfood.ui.LoginActivity.SAVE_USER;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    FragmentManager fragmentManager;
    Toolbar toolbar;
    private TextView txtUserName;
    String sFragment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);
        fragmentManager = getSupportFragmentManager();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //get user from share pref
        Shipper shipper = SharedPrefs.getInstance().get(SAVE_USER, Shipper.class);
        Common.currentShipper = shipper;

        final View headerView = navigationView.getHeaderView(0);
        txtUserName = headerView.findViewById(R.id.tv_username);
        txtUserName.setText(Common.currentShipper.getName());

    }
    /*public void replace(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }*/

    public void Home() {
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }


    public void OrderStatus() {
        /*fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new OrderStatusFragment())
                .commit();*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
//                transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
                Home();
                Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_location:
                OrderStatus();
                Toast.makeText(MainActivity.this, "Location", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_exit:
                SharedPrefs.getInstance().clear();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
