package com.haetae.contactapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SMSActivity extends AppCompatActivity {

    ArrayAdapter<SMSInfo> adapter;
    List<SMSInfo> smsInfoList =new ArrayList<>();
    String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        number = getIntent().getStringExtra("number").replaceAll("[^0-9\\+]","");
        Log.d("tag", "sendMessage: "+number);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        TextView textNameNumber = findViewById(R.id.text_name_number);
        textNameNumber.setText("收件人->"+getIntent().getStringExtra("name")+":"+getIntent().getStringExtra("number"));
        ListView smsList = findViewById(R.id.sms_list);
        adapter = new SMSInfoAdapter(this,R.layout.sms_item_l,smsInfoList);
        smsList.setAdapter(adapter);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.READ_SMS },1);
        } else {
            readSMS();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.SEND_SMS },1);
        }
        Button send = findViewById(R.id.btn_send);
        send.setOnClickListener(view -> {
            sendMessage();
        });
        Button photo = findViewById(R.id.btn_photo);
        photo.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            } else {
                // 启动相机程序
                takePhoto();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Activity.RESULT_OK) {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                Log.i("TestFile",
                        "SD card is not avaiable/writeable right now.");
                return;
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat();
            String name = dateFormat.format("yyyyMMdd_hhmmss"+ ".jpg");
            Toast.makeText(this, name, Toast.LENGTH_LONG).show();
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

            FileOutputStream b = null;
            File file = new File("/sdcard/Image/");
            file.mkdirs();// 创建文件夹
            String fileName = "/sdcard/Image/"+name;

            try {
                b = new FileOutputStream(fileName);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void takePhoto() {
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),1);
    }

    private void sendMessage() {
        EditText input = findViewById(R.id.edit_text_input);
        String message = input.getText().toString();
        if (message == null || "".equals(message)) return;
        String number = getIntent().getStringExtra("number").replaceAll("[^0-9\\+]","");
        Log.d("tag", "sendMessage: "+number);
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(number,null,message,null,null);
        input.setText("");
        readSMS();
    }

    private void readSMS() {
        Cursor cursor = null;
        try {
            //注意：设置成下面格式会因为模拟器号码和联系人不匹配导致无法看到信息，实际上是可以收发的
            cursor = getContentResolver().query(Uri.parse("content://sms/"), new String[]{"address", "body", "date", "type"},"address=?", new String[]{number}, "date");
//            cursor = getContentResolver().query(Uri.parse("content://sms/"), new String[]{"address", "body", "date", "type"},null,null, "date");
            if (cursor != null) {
                smsInfoList.clear();
                while(cursor.moveToNext()) {
                    String address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.ADDRESS));
                    String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.BODY));
                    long date = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.DATE));
                    int type = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.TYPE));
                    Log.w("tag", "readSMS: "+address+" " +body+" " +date+" " +type);
                    smsInfoList.add(new SMSInfo(address,body,date,type));
                }
                adapter.notifyDataSetChanged();
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
    }
}