package com.printer.tscdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Copyright (C), 2012-2020, 打印机有限公司
 * FileName: UsbDeviceListActivity
 * Author: Circle
 * Date: 2020/7/18 16:17
 * Description: 获取USB设备列表
 */
public class UsbDeviceActivity extends AppCompatActivity {

    private static final String TAG = UsbDeviceActivity.class.getSimpleName();
    /**
     * Member fields
     */
    private ListView lvDevices = null;
    private ArrayAdapter<String> adapter;
    public static final String USB_NAME = "usb_name";
    UsbManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb);
        setTitle(getString(R.string.usb_label));
        initView();
        getUsbDeviceList();
    }
    /**
     * 初始化视图、控件
     */
    private void initView() {
        lvDevices = (ListView) findViewById(R.id.lv_usb);
        adapter = new ArrayAdapter<String>(this,R.layout.text_item);
        lvDevices.setOnItemClickListener(mDeviceClickListener);
        lvDevices.setAdapter(adapter);
    }

    /**
     * 检查USB设备的PID与VID
     * @param dev
     * @return
     */
    boolean checkUsbDevicePidVid(UsbDevice dev) {
        int pid = dev.getProductId();
        int vid = dev.getVendorId();
        return ((vid == 34918 && pid == 256) || (vid == 1137 && pid == 85)
                || (vid == 6790 && pid == 30084)
                || (vid == 26728 && pid == 256) || (vid == 26728 && pid == 512)
                || (vid == 26728 && pid == 256) || (vid == 26728 && pid == 768)
                || (vid == 26728 && pid == 1024) || (vid == 26728 && pid == 1280)
                || (vid == 26728 && pid == 1536));
    }

    /**
     * 获取USB设备列表
     */
    public void  getUsbDeviceList() {
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        // Get the list of attached devices
        HashMap<String, UsbDevice> devices = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = devices.values().iterator();
        int count = devices.size();
        Log.d(TAG, "count " + count);
        if (count > 0) {
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                String devicename = device.getDeviceName();
                if (checkUsbDevicePidVid(device)) {
                    adapter.add(devicename);
                }
            }
        } else {
            String noDevices = getResources().getText(R.string.none_usb_device).toString();
            Log.d(TAG, "noDevices " + noDevices);
            adapter.add(noDevices);
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String noDevices = getResources().getText(R.string.none_usb_device).toString();
            if (!info.equals(noDevices)) {
                String address = info;
                // Create the result Intent and include the MAC address
                Intent intent = new Intent();
                intent.putExtra(USB_NAME, address);
                // Set result and finish this Activity
                setResult(Activity.RESULT_OK, intent);
            }
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (manager!=null){
            manager=null;
        }
    }
}