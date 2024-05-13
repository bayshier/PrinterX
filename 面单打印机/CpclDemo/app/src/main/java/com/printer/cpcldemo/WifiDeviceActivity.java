package com.printer.cpcldemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * Copyright (C), 2012-2019, 打印机有限公司
 * FileName: Printer
 * Author: Circle
 * Date: 2019/12/25 19:46
 * Description: WIFI打印机设备
 */
public class WifiDeviceActivity extends Activity {
    public static String IP="IP";
    public Context context;
    public EditText edWifi;
    public Button btnOK;
    SharedPreferencesUtil sharedPreferencesUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        context=WifiDeviceActivity.this;
        sharedPreferencesUtil= SharedPreferencesUtil.getInstantiation(context);
        initView();
    }

    private void initView() {
        //串口路径初始化
        edWifi = (EditText)findViewById(R.id.et_wifi_ip);
        btnOK=(Button)findViewById(R.id.btn_confirm) ;
        String ip = sharedPreferencesUtil.getString("192.168.123.100", IP);
        edWifi.setText(ip);
        initListener();
    }

    private void initListener() {
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip=edWifi.getText().toString().trim();
                if (TextUtils.isEmpty(ip)&&!Utils.checkIP(ip)){//ip不合法
                    Utils.shortToast(context,context.getString(R.string.ip_is_illegal));
                    return;
                }
                sharedPreferencesUtil.putString(ip,IP);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(IP, ip);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
