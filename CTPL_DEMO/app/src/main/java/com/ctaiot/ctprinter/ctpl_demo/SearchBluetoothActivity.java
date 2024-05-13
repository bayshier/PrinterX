package com.ctaiot.ctprinter.ctpl_demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * @Author Jaco
 * @Date 2022/1/7
 * @Desc
 */
public class SearchBluetoothActivity extends AppCompatActivity {

    ListView listView;
    DeviceAdapter adapter;
    ScanCallback leCallBack;

    boolean isSPP = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSPP = "SPP".equals(getIntent().getStringExtra("search"));
        setContentView(R.layout.activity_search_bluetooth);
        adapter = new DeviceAdapter(this);
        listView = findViewById(R.id.search_bt_lv);
        listView.setAdapter(adapter);
        if (!App.getInstance().checkBLEPermission() || App.getInstance().checkSPPPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissions(new String[]{
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH_ADVERTISE,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                }, 0);
            } else {
                requestPermissions(new String[]{
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                }, 0);
            }
            return;
        }
        init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    @SuppressLint("MissingPermission")
    private void init() {
        if (isSPP) {
            setupBluetoothReceiver();
            BluetoothAdapter.getDefaultAdapter().startDiscovery();
        } else {
            BluetoothLeScanner leScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
            leScanner.startScan((leCallBack = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    BluetoothDevice device = result.getDevice();
                    if (!TextUtils.isEmpty(device.getName()) &&
                            (device.getType() == BluetoothDevice.DEVICE_TYPE_LE ||
                                    device.getType() == BluetoothDevice.DEVICE_TYPE_DUAL)) {
                        if (!adapter.deviceMacList.contains(device.getName() + "\n" + device.getAddress())) {
                            adapter.deviceMacList.add(device.getName() + "\n" + device.getAddress());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }));
        }
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String name = null, mac = null;
            try {
                String[] split = ((String) adapter.getItem(position)).split("\n");
                name = split[0];
                mac = split[1];
            } catch (Exception e) {
                Log.wtf(App.TAG, e);
            }
            if (mac == null)
                return;
            Intent intent = new Intent();
            intent.putExtra("searchType", isSPP ? "SPP" : "BLE");
            intent.putExtra("searchName", name);
            intent.putExtra("searchMac", mac);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchReceiver != null)
            unregisterReceiver(searchReceiver);
        if (isSPP)
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        else
            BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner().stopScan(leCallBack);
    }

    static class DeviceAdapter extends BaseAdapter {
        Context c;
        ArrayList<String> deviceMacList;

        public DeviceAdapter(Context c) {
            deviceMacList = new ArrayList<>();
            this.c = c;
        }

        @Override
        public int getCount() {
            return deviceMacList.size();
        }

        @Override
        public Object getItem(int position) {
            return deviceMacList.get(position);
        }

        @Override public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(c).inflate(android.R.layout.simple_list_item_1, null);
            }
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(deviceMacList.get(position));
            return convertView;
        }
    }

    BluetoothSearchReceiver searchReceiver;

    private void setupBluetoothReceiver() {
        if (searchReceiver != null)
            return;
        searchReceiver = new BluetoothSearchReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);//开始搜索
        filter.addAction(BluetoothDevice.ACTION_FOUND);
//        filter.addAction(PrintConnectController.BLUETOOTH_BOND_CHANGE);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//结束搜索广播
        registerReceiver(searchReceiver, filter);
    }

    class BluetoothSearchReceiver extends BroadcastReceiver {

        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED: {
                    adapter.deviceMacList.clear();
                    adapter.notifyDataSetChanged();
                    break;
                }
                case BluetoothDevice.ACTION_FOUND: {
                    final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (TextUtils.isEmpty(device.getName()))
                        break;
                    if (device.getType() != BluetoothDevice.DEVICE_TYPE_CLASSIC && device.getType() != BluetoothDevice.DEVICE_TYPE_DUAL)
                        break;
                    if (!adapter.deviceMacList.contains(device.getName() + "\n" + device.getAddress())) {
                        adapter.deviceMacList.add(device.getName() + "\n" + device.getAddress());
                        adapter.notifyDataSetChanged();
                    }
                    break;
                }
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED: {
                    Toast.makeText(SearchBluetoothActivity.this, "SPP搜索结束!", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }
}
