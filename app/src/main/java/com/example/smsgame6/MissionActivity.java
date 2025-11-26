package com.example.smsgame6;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

public class MissionActivity extends AppCompatActivity {

    public static final String ACTION_SMS_EVENT =
            "com.example.smsgame6.SMS_EVENT";

    private static final int REQ_SEND_SMS = 15001;
    private static final int REQ_RECEIVE_SMS = 15002;

    private TextView txtMission, txtTimer, txtStatus;


    private static class MissionDef {
        String text;
        String answer;

        MissionDef(String t, String a) {
            text = t;
            answer = a;
        }
    }

    private final MissionDef[] missions = new MissionDef[] {

            new MissionDef(
                    "Mission 1: Decode this.\nWhat is 7 + 5?",
                    "12"
            ),

            new MissionDef(
                    "Mission 2: Double trouble.\nWhat is 2 × 9?",
                    "18"
            ),

            new MissionDef(
                    "Mission 3: Subtraction cipher.\nWhat is 15 − 7?",
                    "8"
            ),

            new MissionDef(
                    "Mission 4: Division code.\nWhat is 81 ÷ 9?",
                    "9"
            ),

            new MissionDef(
                    "Mission 5: Order of operations.\nWhat is 3 + 4 × 2?",
                    "11"
            ),

            new MissionDef(
                    "Mission 6: Mixed operation.\nWhat is (18 ÷ 3) + 7?",
                    "13"
            ),

            new MissionDef(
                    "Mission 7: Square the intel.\nWhat is 5 × 5?",
                    "25"
            ),

            new MissionDef(
                    "Mission 8: Sum of codes.\nWhat is 12 + 15 + 3?",
                    "30"
            ),

            new MissionDef(
                    "Mission 9: Multiplication in disguise.\nWhat is 6 × 7?",
                    "42"
            ),

            new MissionDef(
                    "Mission 10: Final encryption.\nWhat is (9 + 6) ÷ 3?",
                    "5"
            )
    };

    private final Random random = new Random();


    private String missionText = "";
    private String correctAnswer = "";

    private boolean missionActive = false;
    private CountDownTimer timer;

    private final BroadcastReceiver smsUiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {

            String from = intent.getStringExtra("from");
            String body = intent.getStringExtra("body");
            if (from == null || body == null) return;

            txtStatus.setText("Reply from " + from + ": " + body);

            MissionResult r = new MissionResult();
            r.setMission(missionText);
            r.setReply(body);


            if (body.trim().equalsIgnoreCase(correctAnswer)) {
                missionActive = false;
                if (timer != null) timer.cancel();

                txtStatus.setText("Mission Success!");
                r.setSuccess(true);

                saveMission(r);

                Intent i = new Intent(MissionActivity.this, MissionLogActivity.class);
                i.putExtra("Result", r);
                startActivity(i);

            } else {
                missionActive = false;
                if (timer != null) timer.cancel();

                txtStatus.setText("Incorrect reply.");
                r.setSuccess(false);

                saveMission(r);

            }
        }
    };

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_mission);

        txtMission = findViewById(R.id.txtMission);
        txtTimer   = findViewById(R.id.txtTimer);
        txtStatus  = findViewById(R.id.txtStatus);

        pickRandomMission();
        ensureReceivePermission();
    }


    private void pickRandomMission() {
        int index = random.nextInt(missions.length);
        MissionDef m = missions[index];

        missionText = m.text;
        correctAnswer = m.answer;

        txtMission.setText(missionText);
        txtStatus.setText("Ready to send mission.");
    }

    // XML button: android:onClick="sendMission"
    public void sendMission(View v) {
        if (missionActive) {
            Toast.makeText(this, "Mission already active. Wait for reply.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!hasPermission(Manifest.permission.SEND_SMS)) {
            requestPermission(new String[]{Manifest.permission.SEND_SMS}, REQ_SEND_SMS);
            return;
        }

        pickRandomMission();
        actuallySendMission();
    }

    private void actuallySendMission() {
        try {
            SmsManager sms = SmsManager.getDefault();


            String targetNumber = "YOUR_NUMBER_HERE";

            sms.sendTextMessage(targetNumber, null, missionText, null, null);

            txtStatus.setText("Mission sent! Waiting for reply...");
            missionActive = true;
            startMissionTimer();

        } catch (Exception e) {
            Toast.makeText(this, "SMS failed: " + e, Toast.LENGTH_LONG).show();
        }
    }

    private void startMissionTimer() {
        if (timer != null) timer.cancel();

        timer = new CountDownTimer(45000, 1000) {
            public void onTick(long ms) {
                txtTimer.setText("Time: " + (ms / 1000));
            }

            public void onFinish() {
                missionActive = false;
                txtStatus.setText("Mission FAILED (time up)");

                MissionResult r = new MissionResult();
                r.setMission(missionText);
                r.setReply("no response was given");
                r.setSuccess(false);
                saveMission(r);
            }
        }.start();
    }


    private void saveMission(MissionResult result) {
        try {
            File file = new File(getFilesDir(), "missions.dat");
            boolean exists = file.exists();

            FileOutputStream fos;
            ObjectOutputStream oos;

            if (!exists) {
                fos = openFileOutput("missions.dat", MODE_PRIVATE);
                oos = new ObjectOutputStream(fos);
            } else {
                fos = openFileOutput("missions.dat", MODE_APPEND);
                oos = new AppendableObjectOutputStream(fos);
            }

            oos.writeObject(result);
            oos.flush();
            oos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class AppendableObjectOutputStream extends ObjectOutputStream {
        public AppendableObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            // no header for append
        }
    }


    private boolean hasPermission(String perm) {
        return ContextCompat.checkSelfPermission(this, perm) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String[] perms, int code) {
        ActivityCompat.requestPermissions(this, perms, code);
    }

    private void ensureReceivePermission() {
        if (!hasPermission(Manifest.permission.RECEIVE_SMS)) {
            requestPermission(
                    new String[]{Manifest.permission.RECEIVE_SMS},
                    REQ_RECEIVE_SMS
            );
        }
    }

    @Override
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    protected void onStart() {
        super.onStart();
        IntentFilter f = new IntentFilter(ACTION_SMS_EVENT);

        if (android.os.Build.VERSION.SDK_INT >= 33)
            registerReceiver(smsUiReceiver, f, Context.RECEIVER_NOT_EXPORTED);
        else
            registerReceiver(smsUiReceiver, f);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try { unregisterReceiver(smsUiReceiver); } catch (Exception ignore) {}
        if (timer != null) timer.cancel();
    }

    @Override
    public void onRequestPermissionsResult(int code,
                                           @NonNull String[] perms,
                                           @NonNull int[] results) {
        super.onRequestPermissionsResult(code, perms, results);

        if (results.length == 0) return;

        if (code == REQ_SEND_SMS) {
            if (results[0] == PackageManager.PERMISSION_GRANTED)
                actuallySendMission();
            else
                Toast.makeText(this, "SMS send permission denied.", Toast.LENGTH_LONG).show();
        }

        if (code == REQ_RECEIVE_SMS) {
            if (results[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Can receive SMS.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Cannot receive SMS.", Toast.LENGTH_LONG).show();
        }
    }
}