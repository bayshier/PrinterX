package com.ctaiot.ctprinter.ctpl_demo;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.ctaiot.ctprinter.ctpl.CTPL;
import com.ctaiot.ctprinter.ctpl.RespCallback;

/**
 * @Author Jaco
 * @Date 2022/10/21
 * @Desc
 */
public class App extends Application {
    public static final String TAG = "CTPL";
    private static App instance;

    @Override public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }

    /**
     * 部分国产ROM 在安卓12不要求定位时,依然需要开启定位功能
     */
    public boolean checkBLEPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {//12以下
            boolean bt = PackageManager.PERMISSION_GRANTED == checkPermission(
                    Manifest.permission.BLUETOOTH, Process.myPid(), Process.myUid());
            boolean btAdmin = PackageManager.PERMISSION_GRANTED == checkPermission(
                    Manifest.permission.BLUETOOTH_ADMIN, Process.myPid(), Process.myUid());
            boolean location = PackageManager.PERMISSION_GRANTED == checkPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION, Process.myPid(), Process.myUid());
            boolean location2 = PackageManager.PERMISSION_GRANTED == checkPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION, Process.myPid(), Process.myUid());
            return (bt && btAdmin && location && location2);
        }

        boolean location = PackageManager.PERMISSION_GRANTED == checkPermission(
                Manifest.permission.ACCESS_FINE_LOCATION, Process.myPid(), Process.myUid());
        boolean advr = PackageManager.PERMISSION_GRANTED == checkPermission(
                Manifest.permission.BLUETOOTH_ADVERTISE, Process.myPid(), Process.myUid());
        boolean conn = PackageManager.PERMISSION_GRANTED == checkPermission(
                Manifest.permission.BLUETOOTH_CONNECT, Process.myPid(), Process.myUid());

        return (advr && conn && location);
    }

    /**
     * 部分国产ROM 在安卓13不要求定位时,依然需要开启定位功能
     */
    public boolean checkSPPPermission() {

        boolean bt = PackageManager.PERMISSION_GRANTED == checkPermission(
                Manifest.permission.BLUETOOTH, Process.myPid(), Process.myUid());
        boolean toggle = PackageManager.PERMISSION_GRANTED == checkPermission(
                Manifest.permission.BLUETOOTH_ADMIN, Process.myPid(), Process.myUid());
        boolean location = PackageManager.PERMISSION_GRANTED == checkPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION, Process.myPid(), Process.myUid());
        boolean location2 = PackageManager.PERMISSION_GRANTED == checkPermission(
                Manifest.permission.ACCESS_FINE_LOCATION, Process.myPid(), Process.myUid());

        boolean location3;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            location3 = ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isLocationEnabled();
        } else {
            try {
                location3 = Settings.Secure.LOCATION_MODE_OFF == Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                location3 = false;
                e.printStackTrace();
            }
        }
        return bt && toggle && location && location2 && location3;
    }


    interface InputListener {
        boolean onInputEnter(String str);
    }
}
