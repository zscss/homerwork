package com.haetae.contactapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Object[] pdus = (Object[]) bundle.get("pdus");
        SmsMessage[] msgs = new SmsMessage[pdus.length];
        for (int i = 0; i < pdus.length; i++) {
            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        }
        StringBuilder strb = new StringBuilder();
        for (SmsMessage msg : msgs) {
            strb.append("发信人：\n");
            strb.append(msg.getDisplayOriginatingAddress());
            strb.append("\n信息内容\n");
            strb.append(msg.getDisplayMessageBody());
        }

        Log.d("TAG", "onReceive: "+strb.toString());
        Toast.makeText(context, strb.toString(), Toast.LENGTH_LONG).show();
    }
}
