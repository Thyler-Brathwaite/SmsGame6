package com.example.smsgame6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
    }

    public void startMission(View v) {
        startActivity(new Intent(this, MissionActivity.class));
    }

    public void viewLog(View v) {
        startActivity(new Intent(this, MissionLogActivity.class));
    }

    public void exitApp(View v) {
        finishAffinity();    // EXIT BUTTON REQUIREMENT
    }
}
