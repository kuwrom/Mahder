package com.habeshastudio.mahder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.habeshastudio.mahder.MainActivity;
import com.habeshastudio.mahder.R;

public class Dashboard extends AppCompatActivity {

    public String profession;
    CardView moreBtn;
    CardView doctorBtn;
    CardView lawyerBtn;
    CardView engineerBtn;
    CardView technicianBtn;
    CardView teacherBtn;
    CardView merchantBtn;
    CardView driverBtn;
    CardView artistBtn;
    private Toolbar mToolbar;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    Intent messagesIntent = new Intent(Dashboard.this, MainActivity.class);
                    startActivity(messagesIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    return true;
                case R.id.navigation_notifications:
                    Intent profileIntent = new Intent(Dashboard.this, SettingsActivity.class);
                    startActivity(profileIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        moreBtn = findViewById(R.id.btn_more);

        doctorBtn = findViewById(R.id.btn_doctor);
        lawyerBtn = findViewById(R.id.btn_lawyer);
        engineerBtn = findViewById(R.id.btn_engineer);
        technicianBtn = findViewById(R.id.btn_technician);
        teacherBtn = findViewById(R.id.btn_teacher);
        merchantBtn = findViewById(R.id.btn_merchant);
        driverBtn = findViewById(R.id.btn_driver);
        artistBtn = findViewById(R.id.btn_artist);

        mToolbar = findViewById(R.id.dashboard_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Professionals in your city");


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profession = "all";
                Intent usersIntent = new Intent(Dashboard.this, UsersActivity.class);
                usersIntent.putExtra("profession", profession);
                startActivity(usersIntent);
            }
        });

        doctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profession = "Doctor";
                Intent usersIntent = new Intent(Dashboard.this, Professionals.class);
                usersIntent.putExtra("profession", profession);
                startActivity(usersIntent);
            }
        });

        engineerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profession = "Engineer";
                Intent usersIntent = new Intent(Dashboard.this, Professionals.class);
                usersIntent.putExtra("profession", profession);
                startActivity(usersIntent);
            }
        });

        technicianBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profession = "Technician";
                Intent usersIntent = new Intent(Dashboard.this, Professionals.class);
                usersIntent.putExtra("profession", profession);
                startActivity(usersIntent);
            }
        });

        lawyerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profession = "Lawyer";
                Intent usersIntent = new Intent(Dashboard.this, Professionals.class);
                usersIntent.putExtra("profession", profession);
                startActivity(usersIntent);
            }
        });

        teacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profession = "Teacher";
                Intent usersIntent = new Intent(Dashboard.this, Professionals.class);
                usersIntent.putExtra("profession", profession);
                startActivity(usersIntent);
            }
        });

        merchantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profession = "Merchant";
                Intent usersIntent = new Intent(Dashboard.this, Professionals.class);
                usersIntent.putExtra("profession", profession);
                startActivity(usersIntent);
            }
        });

        driverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profession = "Driver";
                Intent usersIntent = new Intent(Dashboard.this, Professionals.class);
                usersIntent.putExtra("profession", profession);
                startActivity(usersIntent);
            }
        });

        artistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profession = "Artist";
                Intent usersIntent = new Intent(Dashboard.this, Professionals.class);
                usersIntent.putExtra("profession", profession);
                startActivity(usersIntent);
            }
        });

    }


}
