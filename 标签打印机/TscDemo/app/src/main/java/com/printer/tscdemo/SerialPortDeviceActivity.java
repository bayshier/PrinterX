package com.printer.tscdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.gprinter.utils.SerialPortFinder;


/**
 * Copyright (C), 2012-2019, 打印机有限公司
 * FileName: Printer
 * Author: Circle
 * Date: 2019/12/25 19:46
 * Description: 打印机使用单例
 */
public class SerialPortDeviceActivity extends Activity {
    public  static  String SERIALPORT_PATH="SERIAL_PORT_PATH";
    public  static  String SERIALPORT_BAUDRATE="SERIAL_PORT_BAUD_RATE";
    private SerialPortFinder mSerialPortFinder;
    private String[] entries;
    private String[] entryValues;
    private Spinner spSerialPortPath;
    private Spinner spBaudrate;
    private String path;
    private int selectBaudrate;
    private String[] baudrates;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_port);
        baudrates = this.getResources().getStringArray(R.array.baudrate);
        mSerialPortFinder = new SerialPortFinder();
        entries = mSerialPortFinder.getAllDevices();
        //获取串口路径
        entryValues = mSerialPortFinder.getAllDevicesPath();
        initView();
        initListener();
    }

    private void initView() {
        //串口路径初始化
        spSerialPortPath = (Spinner) findViewById(R.id.sp_serialport_path);
        ArrayAdapter arrayAdapter;
        if (entries != null) {
            arrayAdapter = new ArrayAdapter(this, R.layout.text_item, entries);
        } else {
            arrayAdapter = new ArrayAdapter(this, R.layout.text_item, new String[]{this.getString(R.string.str_no_serialport)});
        }
        spSerialPortPath.setAdapter(arrayAdapter);
        //波特率数据初始化
        spBaudrate = (Spinner) findViewById(R.id.sp_baudrate);
        ArrayAdapter portAdapter = new ArrayAdapter(this, R.layout.text_item, this.getResources().getStringArray(R.array.baudrate));
        spBaudrate.setAdapter(portAdapter);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);
    }

    private void initListener() {
        spBaudrate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //保存当前选择的波特率
                selectBaudrate = Integer.parseInt(baudrates[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spSerialPortPath.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (entryValues != null) {
                    path = entryValues[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(path)) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString(SERIALPORT_PATH, path);
                    bundle.putInt(SERIALPORT_BAUDRATE, selectBaudrate);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        });
    }
}
