package com.haetae.contactapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;

public class SMSInfoAdapter extends ArrayAdapter<SMSInfo> {
    public SMSInfoAdapter(@NonNull Context context, int resource, @NonNull List<SMSInfo> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;
        TextView message = null;
        SMSInfo smsInfo = getItem(position);
        if (smsInfo.getType() == 1) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.sms_item_l, parent, false);
            message = view.findViewById(R.id.message_l);
        } else {
            view = LayoutInflater.from(getContext()).inflate(R.layout.sms_item_r, parent, false);
            message = view.findViewById(R.id.message_r);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(smsInfo.getDate());
        message.setText(smsInfo.getBody()+"\n"+time);
        return view;
    }
}
