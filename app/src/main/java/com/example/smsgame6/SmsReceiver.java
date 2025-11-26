package com.example.smsgame6;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction()))
            return;

        if (context.checkSelfPermission(android.Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        if (msgs == null || msgs.length == 0)
            return;

        String from = msgs[0].getOriginatingAddress();
        StringBuilder body = new StringBuilder();

        for (SmsMessage m : msgs) {
            if (m != null && m.getMessageBody() != null)
                body.append(m.getMessageBody());
        }

        // Forward to app
        Intent ui = new Intent(MissionActivity.ACTION_SMS_EVENT);
        ui.putExtra("from", from);
        ui.putExtra("body", body.toString());
        ui.setPackage(context.getPackageName()); // stays inside app
        context.sendBroadcast(ui);
    }
}
