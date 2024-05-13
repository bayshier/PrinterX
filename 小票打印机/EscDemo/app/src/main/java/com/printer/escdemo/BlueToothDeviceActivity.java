package com.printer.escdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.printer.escdemo.bean.BluetoothParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BlueToothDeviceActivity extends AppCompatActivity {
    private String TAG= BlueToothDeviceActivity.class.getSimpleName();
    private ListView lvDevices = null;
    private BluetoothDeviceAdapter adapter;
    //已配对列表
    private List<BluetoothParameter> pairedDevices =new ArrayList<>();
    //新设备列表
    private List<BluetoothParameter> newDevices = new ArrayList<>();
    private BluetoothAdapter mBluetoothAdapter;
    public static final String EXTRA_DEVICE_ADDRESS ="address";
    public static final int    REQUEST_ENABLE_BT = 2;
    public static final int    REQUEST_ENABLE_GPS = 3;
    private PermissionUtils permissionUtils;
    private  LocationManager manager ;
    private Button btn_search;
    /**
     * changes the title when discovery is finished
     */
    private final BroadcastReceiver mFindBlueToothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed
                // already
                BluetoothParameter parameter = new BluetoothParameter();
                int rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);//获取蓝牙信号强度
                if (device != null && device.getName() != null) {
                    parameter.setBluetoothName(device.getName());
                } else {
                    parameter.setBluetoothName("unKnow");
                }
                parameter.setBluetoothMac(device.getAddress());
                parameter.setBluetoothStrength(rssi+ "");
                Log.e(TAG,"\nBlueToothName:\t"+device.getName()+"\nMacAddress:\t"+device.getAddress()+"\nrssi:\t"+rssi);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {//未配对
                    for (BluetoothParameter p:newDevices) {
                        if (p.getBluetoothMac().equals(parameter.getBluetoothMac())){//防止重复添加
                            return;
                        }
                    }
                    newDevices.add(parameter);
                    Collections.sort(newDevices,new Signal());
                    adapter.notifyDataSetChanged();
                } else {//更新已配对蓝牙
                    for (int i = 0; i < pairedDevices.size(); i++) {
                        if (pairedDevices.get(i).getBluetoothMac().equals(parameter.getBluetoothMac())){
                            pairedDevices.get(i).setBluetoothStrength(parameter.getBluetoothStrength());
                            adapter.notifyDataSetChanged();
                            return;
                        }
                    }
                    pairedDevices.add(parameter);
                    adapter.notifyDataSetChanged();
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.complete);
                Log.i("tag", "finish discovery" + (adapter.getCount()-2));
            }else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                int bluetooth_state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                if (bluetooth_state==BluetoothAdapter.STATE_OFF) {//关闭
                    finish();
                }
                if (bluetooth_state==BluetoothAdapter.STATE_ON) {//开启

                }
            }
        }
    };



    // 自定义比较器：按信号强度排序
    static class Signal implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            BluetoothParameter p1 = (BluetoothParameter) object1; // 强制转换
            BluetoothParameter p2 = (BluetoothParameter) object2;
            return p1.getBluetoothStrength().compareTo(p2.getBluetoothStrength());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Request progress bar
        //启用窗口特征
        requestWindowFeature(Window.FEATURE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_bluetooth);
        setTitle(getString(R.string.blue_label));
        initView();
        initBluetooth();
        initBroadcast();
    }


    /**
     * 搜索蓝牙
     */
    public void searchBlueTooth(){
        btn_search.setVisibility(View.GONE);
        setTitle(getString(R.string.searching));
        setProgressBarIndeterminateVisibility(true);
        mBluetoothAdapter.startDiscovery();
    }
    /**
     * 初始化广播
     */
    private void initBroadcast() {
        try {
            // Register for broadcasts when a device is discovered
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            // Register for broadcasts when discovery has finished
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//蓝牙状态改变
            this.registerReceiver(mFindBlueToothReceiver, filter);
        }catch (Exception e){

        }
    }

    private void initView() {
        lvDevices=(ListView)findViewById(R.id.lv_devices);
        btn_search=(Button)findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initBluetooth();
            }
        });
        adapter = new BluetoothDeviceAdapter(pairedDevices,newDevices, BlueToothDeviceActivity.this);
        lvDevices.setAdapter(adapter);
        lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击已配对设备、新设备title不响应
                if (position == 0 || position == pairedDevices.size() + 1) {
                    return;
                }
                String mac=null;
                if (position <= pairedDevices.size()) {//点击已配对设备列表
                    mac= pairedDevices.get(position-1).getBluetoothMac();
                }
                else {//点击新设备列表
                    mac=newDevices.get(position-2- pairedDevices.size()).getBluetoothMac();
                }
                mBluetoothAdapter.cancelDiscovery();

                // Create the result Intent and include the MAC address
                 Intent intent = new Intent();
                 intent.putExtra(EXTRA_DEVICE_ADDRESS, mac);
                 // Set result and finish this Activity
                 setResult(Activity.RESULT_OK, intent);
                 finish();

            }
        });
    }
    private void initBluetooth(){
        // Get the local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
           Toast.makeText(this, "Bluetooth is not supported by the device",Toast.LENGTH_LONG).show();
        } else {
            // If BT is not on, request that it be enabled.
            // setupChat() will then be called during onActivityResult
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else {
                manager= (LocationManager)BlueToothDeviceActivity.this .getSystemService(LOCATION_SERVICE);
                permissionUtils=new PermissionUtils(BlueToothDeviceActivity.this);
                permissionUtils.requestPermissions(getString(R.string.permission),
                        new PermissionUtils.PermissionListener(){
                            @Override
                            public void doAfterGrand(String... permission) {
//                                if ((Build.VERSION.SDK_INT>=29)&&! manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//                                    AlertDialog alertDialog = new AlertDialog.Builder(BlueToothDeviceActivity.this)
//                                            .setTitle(getString(R.string.tip))
//                                            .setMessage(getString(R.string.gps_permission))
//                                            .setIcon(R.mipmap.ic_launcher)
//                                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {//添加"Yes"按钮
//                                                @Override
//                                                public void onClick(DialogInterface dialogInterface, int i) {
//                                                    Intent intent = new Intent();
//                                                    intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                    startActivityForResult(intent,REQUEST_ENABLE_GPS);
//                                                }
//                                            })
//                                            .create();
//                                    alertDialog.show();
//                                }else {
                                    searchBlueTooth();
//                                }
                            }
                            @Override
                            public void doAfterDenied(String... permission) {
                                for (String p:permission) {
                                    switch (p){
                                        case Manifest.permission.ACCESS_FINE_LOCATION:
                                            Utils.shortToast(BlueToothDeviceActivity.this,getString(R.string.no_permission));
                                            break;

                                    }
                                }
                            }
                        },  Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                // bluetooth is opened
                initBluetooth();
            } else {
                // bluetooth is not open
                Toast.makeText(this, R.string.bluetooth_is_not_enabled, Toast.LENGTH_SHORT).show();
                finish();
            }
        }else if (requestCode==REQUEST_ENABLE_GPS){
            if (resultCode == Activity.RESULT_OK) {
                // bluetooth is opened
                initBluetooth();
            } else {
                // bluetooth is not open
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // Unregister broadcast listeners
        if (mFindBlueToothReceiver != null) {
            unregisterReceiver(mFindBlueToothReceiver);
        }
        }catch (Exception e){

        }
    }
}