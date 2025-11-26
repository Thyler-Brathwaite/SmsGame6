package com.example.smsgame6;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MissionLogActivity extends AppCompatActivity {

    private TextView logView;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_mission_log);

        logView = findViewById(R.id.txtLog);


            logView.setText("Mission Success! \n file has been saved at mission.dat");

    }
}
