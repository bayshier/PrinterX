package com.sdk.cpcl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;


/**
 * Created by NO on 2018/7/24.
 */

public class Bluetooth {
    Context context;
    static Bluetooth bluetooth;
    private BluetoothAdapter mBluetoothAdapter;
    toData mTodata;
    private static RxPermissions rxPermissions;
    public static boolean isStaBond = false;
    private static BluetoothDevice mBluetoothDevice;
    private static OnBondState mOnBondState;


    private Bluetooth(Context context){
        this.context=context;
    }
    public static Bluetooth getBluetooth(Context context){
//         if (bluetooth==null){
             bluetooth= new Bluetooth(context);
//             rxPermissions = new RxPermissions((Activity)context);
//         }
        return bluetooth;
    }
    private void registerBroadcast() {
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(mReceiver, intent);
    }
    @SuppressLint("MissingPermission")
    public void doDiscovery(){
        if (context!=null){
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Utility.show(context, "Device does not support Bluetooth");
                return;
            } else if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
            registerBroadcast();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                rxPermissions = new RxPermissions((Activity)context);
                rxPermissions.request(Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_SCAN
                    ).subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            if (null == mBluetoothAdapter) {
                                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            }
                            if (!mBluetoothAdapter.isEnabled()) {
                                mBluetoothAdapter.enable();
                            }
                            if (mBluetoothAdapter.isDiscovering()) {
                                mBluetoothAdapter.cancelDiscovery();
                            }
                            mBluetoothAdapter.startDiscovery();
                        } else {
                            Utility.show(context,"no bluetooth permission");
                        }
                    }
                });
            }else{
                rxPermissions = new RxPermissions((Activity)context);
                rxPermissions.request(Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ).subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            if (null == mBluetoothAdapter) {
                                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            }
                            if (!mBluetoothAdapter.isEnabled()) {
                                mBluetoothAdapter.enable();
                            }
                            if (mBluetoothAdapter.isDiscovering()) {
                                mBluetoothAdapter.cancelDiscovery();
                            }
                            mBluetoothAdapter.startDiscovery();
                        } else {
                            Utility.show(context,"no bluetooth permission");
                        }
                    }
                });
            }
        }
    }
    public void getData(toData todata){
        mTodata=todata;
    }
    public interface toData{
        public void succeed(BluetoothDevice bluetoothDevice);
    }
    @SuppressLint("MissingPermission")
    public void disReceiver(){
        isStaBond = false;
        if (mReceiver != null&&context!=null)
            context.unregisterReceiver(mReceiver);
        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = null;
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(device.getBluetoothClass().getMajorDeviceClass()==1536){
                        if (mTodata!=null){
                            mTodata.succeed(device);
                        }
                    }
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    switch (device.getBondState()) {
                        case BluetoothDevice.BOND_BONDING:
                            Log.d("Print", "正在配对......");
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            Log.d("Print", "完成配对");
                            if (mOnBondState!=null&&mBluetoothDevice!=null&&isStaBond){
                                if (mBluetoothDevice.getAddress().equals(device.getAddress()))
                                    mOnBondState.bondSuccess();
                            }
                            break;
                        case BluetoothDevice.BOND_NONE:
                            Log.d("Print", "取消配对");
                        default:
                            break;
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.d("Print", "搜索完成");
                    break;
            }
        }
    };
    public static void setOnBondState(BluetoothDevice bluetoothDevice,OnBondState onBondState){
        isStaBond=true;
        mBluetoothDevice = bluetoothDevice;
        mOnBondState = onBondState;
    }
    interface OnBondState {
        void bondSuccess();
    }
}
