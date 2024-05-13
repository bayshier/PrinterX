package com.ctaiot.ctprinter.ctpl_demo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ctaiot.ctprinter.ctpl.CTPL;
import com.ctaiot.ctprinter.ctpl.Device;
import com.ctaiot.ctprinter.ctpl.RespCallback;
import com.ctaiot.ctprinter.ctpl.param.BarCode;
import com.ctaiot.ctprinter.ctpl.param.Direction;
import com.ctaiot.ctprinter.ctpl.param.Mirror;
import com.ctaiot.ctprinter.ctpl.param.PaperType;
import com.ctaiot.ctprinter.ctpl.param.PrintMode;
import com.ctaiot.ctprinter.ctpl.param.QREncodeMode;
import com.ctaiot.ctprinter.ctpl.param.QRLevel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Jaco
 * @Date 2022/10/21
 * @Desc
 */
public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView title;
    DrawerLayout drawerLayout;

    ViewGroup drawerContent, queryContainer, settingContainer, printContainer;

    ViewGroup prepareContainer, sendContainer, respContainer;

    TextView searchSelected;

    EditText input;

    View send, prepareClear, sendClear, respClear, mask, inputEnter, inputCancel;

    String bluetoothType, bluetoothName, bluetoothMac;

    public static MainActivity instance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.header);
        title = findViewById(R.id.title);
        drawerLayout = findViewById(R.id.content);
        drawerContent = drawerLayout.findViewById(R.id.drawer_content);
        prepareContainer = drawerLayout.findViewById(R.id.prepareContent);
        send = drawerLayout.findViewById(R.id.send);
        prepareClear = drawerLayout.findViewById(R.id.clear);
        sendClear = drawerLayout.findViewById(R.id.sendClear);
        respClear = drawerLayout.findViewById(R.id.respClear);
        sendContainer = drawerLayout.findViewById(R.id.sendContent);
        respContainer = drawerLayout.findViewById(R.id.respContent);
        mask = findViewById(R.id.mask);
        input = findViewById(R.id.input);
        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                inputEnter.performClick();
                return true;
            }
            return false;
        });
        inputEnter = findViewById(R.id.input_enter);
        inputCancel = findViewById(R.id.input_cancel);
        CTPL.getInstance().init(App.getInstance(), new RespCallback() {
            @Override
            public void onConnectRespsonse(int port, int reason) {
                Log.d(App.TAG, "端口=" + port + ",结果=" + reason);
                Toast.makeText(MainActivity.instance, "端口=" + port + ",结果=" + reason, Toast.LENGTH_SHORT).show();
            }

            public void onDataResponse(HashMap<String, String> result) {
                Iterator<Map.Entry<String, String>> it = result.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> next = it.next();
                    insertItem(respContainer, next.getKey() + " = " + next.getValue());
                }
                if (respContainer.getParent() instanceof NestedScrollView) {
                    NestedScrollView parent = (NestedScrollView) respContainer.getParent();
                    parent.post(() -> parent.fullScroll(View.FOCUS_DOWN));
                }
            }

            @Override
            public boolean autoSPPBond() {
                return true;
            }
        });

        mask.setOnClickListener(v -> {

        });

        inputCancel.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            View focusView = getCurrentFocus();
            if (focusView == null || imm == null)
                return;
            imm.hideSoftInputFromWindow(focusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            input.setText("");
            mask.setVisibility(View.GONE);
        });

        send.setOnClickListener(v -> {
            if (!CTPL.getInstance().isConnected()) {
                Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
                return;
            }

            while (prepareContainer.getChildCount() > 0) {
                View childAt = prepareContainer.getChildAt(0);
                prepareContainer.removeViewAt(0);
                sendContainer.addView(childAt);
            }
            NestedScrollView scrollView = (NestedScrollView) sendContainer.getParent();
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
            CTPL.getInstance().execute();
        });

        prepareClear.setOnClickListener(v -> {
            prepareContainer.removeAllViews();
            CTPL.getInstance().clean();
        });
        sendClear.setOnClickListener(v -> {
            sendContainer.removeAllViews();
        });
        respClear.setOnClickListener(v -> {
            respContainer.removeAllViews();
        });

        queryContainer = new LinearLayout(this);
        ((LinearLayout) queryContainer).setOrientation(LinearLayout.VERTICAL);
        TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_drawer_menu, null);
        tv.setBackgroundColor(0x80259B24);
        tv.setText("查询指令");
        queryContainer.addView(tv);

        settingContainer = new LinearLayout(this);
        ((LinearLayout) settingContainer).setOrientation(LinearLayout.VERTICAL);
        tv = (TextView) getLayoutInflater().inflate(R.layout.item_drawer_menu, null);
        tv.setBackgroundColor(0x80259B24);
        tv.setText("设置指令");
        settingContainer.addView(tv);

        printContainer = new LinearLayout(this);
        ((LinearLayout) printContainer).setOrientation(LinearLayout.VERTICAL);
        tv = (TextView) getLayoutInflater().inflate(R.layout.item_drawer_menu, null);
        tv.setBackgroundColor(0x80259B24);
        tv.setText("打印指令");
        printContainer.addView(tv);

        SharedPreferences cache = getSharedPreferences("cache", MODE_PRIVATE);
        bluetoothType = cache.getString("defaultType", "无");
        bluetoothMac = cache.getString("defaultMac", "无");
        bluetoothName = cache.getString("defaultName", "无");
        initToolBar();
        initDrawerItem();
        if (!App.getInstance().checkBLEPermission()) {
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
        }
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, 0, 0) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (drawerContent.getParent() instanceof NestedScrollView && !CTPL.getInstance().isConnected()) {
                    NestedScrollView parent = (NestedScrollView) drawerContent.getParent();
                    parent.post(() -> parent.fullScroll(View.FOCUS_UP));
                }
            }
        };
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            bluetoothType = data.getStringExtra("searchType");
            bluetoothName = data.getStringExtra("searchName");
            bluetoothMac = data.getStringExtra("searchMac");

            StringBuilder sb = new StringBuilder();
            sb.append("默认蓝牙:")
                    .append(bluetoothType)
                    .append("\n")
                    .append(bluetoothName)
                    .append("\n")
                    .append(bluetoothMac);
            searchSelected.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            searchSelected.setText(sb.toString());

            SharedPreferences.Editor editor = getSharedPreferences("cache", MODE_PRIVATE).edit();
            editor.putString("defaultType", bluetoothType);
            editor.putString("defaultName", bluetoothName);
            editor.putString("defaultMac", bluetoothMac);
            editor.commit();
        }
    }

    private void initDrawerItem() {
        drawerContent.removeAllViews();
        TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_drawer_menu, null);
        StringBuilder sb = new StringBuilder();
        sb.append("默认蓝牙:")
                .append(bluetoothType)
                .append("\n")
                .append(bluetoothName)
                .append("\n")
                .append(bluetoothMac);
        tv.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        tv.setBackgroundColor(0x40_00_00_00);
        tv.setText(sb.toString());
        drawerContent.addView(searchSelected = tv);

        tv = (TextView) getLayoutInflater().inflate(R.layout.item_drawer_menu, null);
        tv.setText("蓝牙SPP搜索");
        tv.setOnClickListener(this::onFuncClick);
        drawerContent.addView(tv);

        tv = (TextView) getLayoutInflater().inflate(R.layout.item_drawer_menu, null);
        tv.setOnClickListener(this::onFuncClick);
        tv.setText("蓝牙BLE搜索");
        drawerContent.addView(tv);

        tv = (TextView) getLayoutInflater().inflate(R.layout.item_drawer_menu, null);
        tv.setOnClickListener(this::onFuncClick);
        tv.setText("蓝牙连接");
        drawerContent.addView(tv);

        tv = (TextView) getLayoutInflater().inflate(R.layout.item_drawer_menu, null);
        tv.setOnClickListener(this::onFuncClick);
        tv.setText("USB连接");
        drawerContent.addView(tv);

        tv = (TextView) getLayoutInflater().inflate(R.layout.item_drawer_menu, null);
        tv.setOnClickListener(this::onFuncClick);
        tv.setText("断开连接");
        drawerContent.addView(tv);

        int dp48 = DensityUtil.dp2px(this, 48);

        queryContainer.setTag(null);
        queryContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp48));
        queryContainer.setOnClickListener(this::onContainerClick);
        drawerContent.addView(queryContainer);

        settingContainer.setTag(null);
        settingContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp48));
        settingContainer.setOnClickListener(this::onContainerClick);
        drawerContent.addView(settingContainer);

        printContainer.setTag(null);
        printContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp48));
        printContainer.setOnClickListener(this::onContainerClick);
        drawerContent.addView(printContainer);

        String[] strArray = getResources().getStringArray(R.array.queryArray);
        for (int i = 0; i < strArray.length; i++) {
            tv = (TextView) getLayoutInflater().inflate(R.layout.item_drawer_menu, null);
            tv.setOnClickListener(this::onQueryClick);
            tv.setText(strArray[i]);
            tv.setTag(i);
            queryContainer.addView(tv);
        }

        strArray = getResources().getStringArray(R.array.settingArray);
        for (int i = 0; i < strArray.length; i++) {
            tv = (TextView) getLayoutInflater().inflate(R.layout.item_drawer_menu, null);
            tv.setOnClickListener(this::onSettingClick);
            tv.setText(strArray[i]);
            tv.setTag(i);
            settingContainer.addView(tv);
        }

        strArray = getResources().getStringArray(R.array.printArray);
        for (int i = 0; i < strArray.length; i++) {
            tv = (TextView) getLayoutInflater().inflate(R.layout.item_drawer_menu, null);
            tv.setOnClickListener(this::onPrintClick);
            tv.setText(strArray[i]);
            tv.setTag(i);
            printContainer.addView(tv);
        }
    }

    private void onContainerClick(View v) {
        int dp48 = DensityUtil.dp2px(this, 48);
        if (v instanceof ViewGroup) {
            ViewGroup.LayoutParams p = v.getLayoutParams();
            v.setTag(v.getTag() == null ? 1 : null);
            p.height = v.getTag() == null ? dp48 : LinearLayout.LayoutParams.WRAP_CONTENT;
            v.setBackgroundColor(v.getTag() == null ? Color.TRANSPARENT : 0X20_00_00_00);
            v.setLayoutParams(p);
        }
    }

    private void onFuncClick(View v) {
        int index = drawerContent.indexOfChild(v);
        if (index < 0)
            return;
        switch (index) {
            case 1: {//蓝牙SPP搜索
                Intent intent = new Intent(this, SearchBluetoothActivity.class);
                intent.putExtra("search", "SPP");
                startActivityForResult(intent, 100);
                break;
            }
            case 2: {//蓝牙BLE搜索
                Intent intent = new Intent(this, SearchBluetoothActivity.class);
                intent.putExtra("search", "BLE");
                startActivityForResult(intent, 100);
                break;
            }
            case 3: {//蓝牙连接
                Device d = new Device();
                CTPL.Port port = "SPP".equals(bluetoothType) ? CTPL.Port.SPP : CTPL.Port.BLE;
                d.setPort(port);
                d.setBluetoothMacAddr(bluetoothMac);
                if (port == CTPL.Port.BLE) {
                    d.setBleServiceUUID("49535343-fe7d-4ae5-8fa9-9fafd205e455");
                }
                CTPL.getInstance().connect(d);
                break;
            }
            case 4: {//USB连接
                Device d = new Device();
                d.setPort(CTPL.Port.USB);
                //CT320B
                d.setUsbDeviceVendorId(10473);
                d.setUsbDeviceProductId(644);
                CTPL.getInstance().connect(d);
                break;
            }
            case 5: {//断开连接
                CTPL.getInstance().disconnect();
                break;
            }
        }
    }

    private void onQueryClick(View v) {
        Object tag = v.getTag();
        if (!(tag instanceof Number)) {
            return;
        }
        int index = (int) tag;
        String[] strArray = getResources().getStringArray(R.array.queryArray);

        StringBuilder sb = new StringBuilder();
        sb.append("查询:").append(strArray[index]);
        Log.d(App.TAG, sb.toString());

        switch (index) {
            case 0: {//查询硬件配置(合并)
                CTPL.getInstance().queryHardwareConfig();
                insertItem(prepareContainer, sb.toString());
                break;
            }
            case 1: {//查询打印机型号
                CTPL.getInstance().queryHardwareModel();
                insertItem(prepareContainer, sb.toString());
                break;
            }
            case 2: {//查询支持压缩算法
                CTPL.getInstance().queryCompressPrint();
                insertItem(prepareContainer, sb.toString());
                break;
            }
            case 3: {//查询固件信息(合并)
                CTPL.getInstance().queryFirmwareInfo();
                insertItem(prepareContainer, sb.toString());
                break;
            }
            case 4: {//查询纸张类型
                CTPL.getInstance().queryPaperType();
                insertItem(prepareContainer, sb.toString());
                break;
            }
            case 5: {//查询记忆打印状态
                CTPL.getInstance().queryMemoryPrint();
                insertItem(prepareContainer, sb.toString());
                break;
            }
            case 6: {//查询打印浓度
                CTPL.getInstance().queryDensity();
                insertItem(prepareContainer, sb.toString());
                break;
            }
            case 7: {//查询打印速度
                CTPL.getInstance().querySpeed();
                insertItem(prepareContainer, sb.toString());
                break;
            }
            case 8: {//查询自动关机(分钟)
                CTPL.getInstance().queryAutoShutdown();
                insertItem(prepareContainer, sb.toString());
                break;
            }
            case 9: {//查询展示参数(合并)
                CTPL.getInstance().queryDisplayInfo();
                insertItem(prepareContainer, sb.toString());
                break;
            }
            case 10: {//查询硬件版本号
                CTPL.getInstance().queryHardwareVersion();
                insertItem(prepareContainer, sb.toString());
                break;
            }
            case 11: {//查询硬件版本号
                CTPL.getInstance().queryBattery();
                insertItem(prepareContainer, sb.toString());
                break;
            }
            case 12: {//查询打印模式
                CTPL.getInstance().queryPrintMode();
                insertItem(prepareContainer, sb.toString());
                break;
            }
        }
    }

    private void onSettingClick(View v) {
        Object tag = v.getTag();
        if (!(tag instanceof Number)) {
            return;
        }
        int index = (int) tag;
        String[] strArray = getResources().getStringArray(R.array.settingArray);
        if (index > strArray.length - 1)
            return;
        StringBuilder sb = new StringBuilder();
        sb.append("设置:").append(strArray[index]);
        Log.d(App.TAG, sb.toString());

        switch (index) {
            case 0: {//设置打印模式

                showInputEditText(str -> {
                    Matcher isNum = Pattern.compile("\\d").matcher(str);
                    if (!isNum.matches()) {
                        Toast.makeText(this, "纯数字0~3", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    sb.append(str);
                    CTPL.getInstance().setPrintMode(PrintMode.valueOf(Integer.parseInt(str.charAt(0) + "")));
                    insertItem(prepareContainer, sb.toString());
                    return true;
                });
                break;
            }
            case 1: {//设置纸张类型
                showInputEditText(str -> {
                    Matcher isNum = Pattern.compile("\\d").matcher(str);
                    if (!isNum.matches()) {
                        Toast.makeText(this, "纯数字0~2", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    sb.append(str);
                    CTPL.getInstance().setPaperType(PaperType.valueOf(Integer.parseInt(str.charAt(0) + "")));
                    insertItem(prepareContainer, sb.toString());
                    return true;
                });
                break;
            }
            case 2: {//设置记忆打印
                showInputEditText(str -> {
                    boolean toggle = !TextUtils.isEmpty(str);
                    sb.append(str);
                    CTPL.getInstance().setMemoryPrint(toggle);
                    insertItem(prepareContainer, sb.toString());
                    return true;
                });
                break;
            }
            case 3: {//设置自动关机时间
                showInputEditText(str -> {
                    Matcher isNum = Pattern.compile("\\d{1,2}").matcher(str);
                    if (!isNum.matches()) {
                        Toast.makeText(this, "纯数字0~60", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    sb.append(str);
                    CTPL.getInstance().setAutoShutdown(Math.max(0, Math.min(60, Integer.parseInt(str))));
                    insertItem(prepareContainer, sb.toString());
                    return true;
                });
                break;
            }
            case 4: {//设置恢复出厂设置
                CTPL.getInstance().resetFirmware();
                insertItem(prepareContainer, sb.toString());
                break;
            }

            case 5: {//设置标签的打印份数
                Toast.makeText(this, "无效功能,打印功能包含此选项", Toast.LENGTH_SHORT).show();
//                CTPL.getInstance().setPrintSize(40,40);
//                insertItem(prepareContainer, sb.toString());
                break;
            }
            case 6: {//设置打印速度
                showInputEditText(str -> {
                    Matcher isNum = Pattern.compile("\\d{1,2}").matcher(str);
                    if (!isNum.matches()) {
                        Toast.makeText(this, "纯数字1~8", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    sb.append(str);
                    CTPL.getInstance().setPrintSpeed(Integer.parseInt(str));
                    insertItem(prepareContainer, sb.toString());
                    return true;
                });
                break;
            }
            case 7: {//设置打印浓度
                showInputEditText(str -> {
                    Matcher isNum = Pattern.compile("\\d{1,2}").matcher(str);
                    if (!isNum.matches()) {
                        Toast.makeText(this, "纯数字1~15", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    sb.append(str);
                    CTPL.getInstance().setPrintDensity(Integer.parseInt(str));
                    insertItem(prepareContainer, sb.toString());
                    return true;
                });
                break;
            }
            case 8: {//设置打印方向和镜像
                showInputEditText(str -> {
                    Matcher isNum = Pattern.compile("\\d{1,4}").matcher(str);
                    if (!isNum.matches()) {
                        Toast.makeText(this, "纯数字1普通,2旋转,3镜像,4旋转+镜像", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    int input = Integer.parseInt(str);
                    Direction d;
                    Mirror m;


                    if(input == 1){
                        d = Direction.Degree0;
                        m = Mirror.Normal;
                    }else if(input == 2){
                        d = Direction.Degree180;
                        m = Mirror.Normal;
                    }else if(input == 3){
                        d = Direction.Degree0;
                        m = Mirror.Flip;
                    }else if(input == 4){
                        d = Direction.Degree180;
                        m = Mirror.Flip;
                    }else{
                        Toast.makeText(this, "纯数字1普通,2旋转,3镜像,4旋转+镜像", Toast.LENGTH_LONG).show();
                        return false;
                    }

                    sb.append(d + "," + m);

                    CTPL.getInstance().setOrientation(d, m);
                    insertItem(prepareContainer, sb.toString());
                    return true;
                });
                break;
            }
        }
    }

    private void onPrintClick(View v) {
        Object tag = v.getTag();
        if (!(tag instanceof Number)) {
            return;
        }
        int index = (int) tag;
        String[] strArray = getResources().getStringArray(R.array.printArray);
        if (index > strArray.length - 1)
            return;
        StringBuilder sb = new StringBuilder();
        sb.append("打印:").append(strArray[index]);
        Log.d(App.TAG, sb.toString());

        switch (index) {
            case 0: {//设置纸张的宽度及高度
                showInputEditText(str -> {
                    Matcher isNum = Pattern.compile("\\d{1,2},\\d{1,2}").matcher(str);
                    if (!isNum.matches()) {
                        Toast.makeText(this, "参数必须包含{宽,高}数字", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    sb.append(str);
                    String[] split = str.split(",");
                    CTPL.getInstance().setSize(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                    insertItem(prepareContainer, sb.toString());
                    return true;
                });
                break;
            }
            case 1: {//绘制线条
//                CTPL.getInstance().drawLine(new Point())
                showInputEditText(str -> {
                    Matcher isNum = Pattern.compile("\\d{1,3},\\d{1,3},\\d{1,3},\\d{1,3}").matcher(str);
                    if (!isNum.matches()) {
                        Toast.makeText(this, "纯数字{左,上,右,下}", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    String[] split = str.split(",");
                    CTPL.getInstance().drawRect(new Rect(
                            Integer.parseInt(split[0]),
                            Integer.parseInt(split[1]),
                            Integer.parseInt(split[2]),
                            Integer.parseInt(split[3])), true, 0);
                    sb.append(str);
                    insertItem(prepareContainer, sb.toString());
                    return true;
                });
                break;
            }
            case 2: {//绘制文字
                showInputEditText(str -> {
                    Matcher isNum = Pattern.compile("^\\d{1,3},\\d{1,3},\\d{1,2},\\d{1,2},(.*?)$").matcher(str);
                    if (!isNum.matches()) {
                        Toast.makeText(this, "{左,上,水平1~10,垂直1~10,文本}", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    String[] split = str.split(",");
                    CTPL.getInstance().drawText(
                            new Point(Integer.parseInt(split[0]), Integer.parseInt(split[1])),
                            Integer.parseInt(split[2]), Integer.parseInt(split[3]),
                            split[4]);
                    sb.append(split[0] + "," + split[1]).append(",");
                    sb.append("xScale:").append(Integer.parseInt(split[2])).append(",");
                    sb.append("yScale:").append(Integer.parseInt(split[3])).append(",");
                    sb.append(split[4]);

                    insertItem(prepareContainer, sb.toString());
                    return true;
                });

                break;
            }
            case 3: {//绘制条码
                showInputEditText(str -> {
                    Matcher isNum = Pattern.compile("^\\d{1,3},\\d{1,3},\\d{1,3},\\d{1,2},\\d,(.*?)$").matcher(str);
                    if (!isNum.matches()) {
                        Toast.makeText(this, "纯数字{左,上,高度10~255,条码类型1~10,排版1~3,文本}", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    String[] split = str.split(",");
                    Point p = new Point(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                    int heightDot = Integer.parseInt(split[2]);
                    int type = Integer.parseInt(split[3]);
                    BarCode b;
                    switch (type) {
                        case 1:
                            b = BarCode.CODE_BAR;
                            break;
                        case 2:
                            b = BarCode.CODE_39;
                            break;
                        case 3:
                            b = BarCode.CODE_93;
                            break;
                        case 5:
                            b = BarCode.UPC_A;
                            break;
                        case 6:
                            b = BarCode.UPC_E;
                            break;
                        case 7:
                            b = BarCode.EAN_8;
                            break;
                        case 8:
                            b = BarCode.EAN_13;
                            break;
                        case 9:
                            b = BarCode.EAN_128;
                            break;
                        case 10:
                            b = BarCode.ITF;
                            break;
                        case 4:
                        default:
                            b = BarCode.CODE_128;
                            break;
                    }
                    Paint.Align pa;
                    int align = Integer.parseInt(split[4]);
                    if (align == 1)
                        pa = Paint.Align.LEFT;
                    if (align == 2)
                        pa = Paint.Align.CENTER;
                    else if (align == 3)
                        pa = Paint.Align.RIGHT;
                    else
                        pa = null;

//                    int rotate = Integer.parseInt(split[5]);
//                    Rotate r;
//                    if (rotate == 2)
//                        r = Rotate.Degree90;
//                    else if (rotate == 3)
//                        r = Rotate.Degree180;
//                    else if (rotate == 4)
//                        r = Rotate.Degree270;
//                    else
//                        r = Rotate.Degree0;

                    String content = split[5];
                    CTPL.getInstance().drawBarCode(p, heightDot, b, pa, null, 2, 2, content);

                    sb.append(split[0] + "," + split[1]).append(",");
                    sb.append("高:").append(heightDot).append(",");
                    sb.append(b).append(",");
                    sb.append(pa).append(",");
                    sb.append(split[5]);
                    insertItem(prepareContainer, sb.toString());

                    return true;
                });
                break;
            }
            case 4: {//绘制QR
                showInputEditText(str -> {
                    Matcher isNum = Pattern.compile("^\\d{1,3},\\d{1,3},\\d,\\d{1,2},(.*?)$").matcher(str);
                    if (!isNum.matches()) {
                        Toast.makeText(this, "纯数字{左,上,二维码等级1~4,单元尺寸1~10,文本}", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    String[] split = str.split(",");
                    Point p = new Point(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                    int level = Integer.parseInt(split[2]);
                    int cellWidth = Integer.parseInt(split[3]);
                    QRLevel l;
                    if (level == 2)
                        l = QRLevel.ECC_M;
                    else if (level == 3)
                        l = QRLevel.ECC_Q;
                    else if (level == 4)
                        l = QRLevel.ECC_H;
                    else
                        l = QRLevel.ECC_L;
                    String content = split[4];
                    CTPL.getInstance().drawQRCode(p, l, cellWidth, QREncodeMode.AUTO, content);


                    sb.append(split[0] + "," + split[1]).append(",");
                    sb.append("单元大小:").append(cellWidth).append(",");
                    sb.append("纠错等级:").append(l).append(",");
                    sb.append(content);
                    insertItem(prepareContainer, sb.toString());
                    return true;
                });
                break;
            }
            case 5: {//绘制图片
                showInputEditText(str -> {
                    Matcher isNum = Pattern.compile("\\d{1,3},\\d{1,3},\\d{1,3},\\d").matcher(str);
                    if (!isNum.matches()) {
                        Toast.makeText(this, "纯数字{左,上,黑度0~255,示例图片1~4}", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    @DrawableRes
                    int[] bmpArray = new int[]{
                            R.mipmap.img
                    };
                    String[] split = str.split(",");
                    int bmpIndex = Math.max(0, Math.min(Integer.parseInt(split[3]) - 1, bmpArray.length - 1));
                    Bitmap b = BitmapFactory.decodeResource(getResources(), bmpArray[bmpIndex]);
                    sb.append(str);
                    int left = Integer.parseInt(split[0]);
                    int top = Integer.parseInt(split[1]);
                    int quality = Integer.parseInt(split[2]);
                    CTPL.getInstance().drawBitmap(new Rect(left,
                            top, left + b.getWidth(), top + b.getHeight()), b, false, quality);
                    insertItem(prepareContainer, sb.toString());
                    return true;
                });
                break;
            }
            case 6: {//绘制压缩图片
                showInputEditText(str -> {
                    Matcher isNum = Pattern.compile("\\d{1,3},\\d{1,3},\\d{1,3},\\d").matcher(str);
                    if (!isNum.matches()) {
                        Toast.makeText(this, "纯数字{左,上,清晰度0~255,示例图片1~4}", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    @DrawableRes
                    int[] bmpArray = new int[]{
                            R.drawable.test_big,
                            R.drawable.test_2,
                            R.drawable.test_3,
                            R.drawable.test_4,
                    };
                    String[] split = str.split(",");
                    int bmpIndex = Math.max(0, Math.min(Integer.parseInt(split[3]) - 1, bmpArray.length - 1));
                    Bitmap b = BitmapFactory.decodeResource(getResources(), bmpArray[bmpIndex]);
                    sb.append(str);
                    int left = Integer.parseInt(split[0]);
                    int top = Integer.parseInt(split[1]);
                    int quality = Integer.parseInt(split[2]);
                    CTPL.getInstance().drawBitmap(new Rect(left,
                            top, left + b.getWidth(), top + b.getHeight()), b, true, quality);
                    insertItem(prepareContainer, sb.toString());
                    return true;
                });
                break;
            }
            case 7: {//执行打印
                showInputEditText(str -> {
                    Matcher isNum = Pattern.compile("\\d{1,2}").matcher(str);
                    if (!isNum.matches()) {
                        Toast.makeText(this, "纯数字{份数1~99}", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    CTPL.getInstance().print(Integer.parseInt(str));
                    sb.append("份数:").append(Integer.parseInt(str));
                    insertItem(prepareContainer, sb.toString());
                    drawerLayout.closeDrawer(Gravity.LEFT, false);
                    return true;
                });
                break;
            }
        }
    }

    public static Bitmap fitXYCropBitmap(Bitmap cache, int newWidth, int newHeight, Rect cropTargetRect) {
        int transferHeight = cropTargetRect == null ? cache.getHeight() : cropTargetRect.height();

        Bitmap bmp = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        c.drawBitmap(cache,
                new Rect(0, 0, cache.getWidth(), cache.getHeight()),
                new Rect(0, 0, cache.getWidth(), transferHeight),
                null);
        return bmp;
    }

    private void showInputEditText(@NonNull App.InputListener listener) {
        input.setText("");
        mask.setVisibility(View.VISIBLE);
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        inputEnter.setOnClickListener(v -> {
            View focusView = getCurrentFocus();
            if (focusView == null || imm == null)
                return;

            if (!listener.onInputEnter(input.getText().toString()))
                return;

            imm.hideSoftInputFromWindow(focusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            mask.setVisibility(View.GONE);

        });
        input.requestFocus();
        imm.showSoftInput(getCurrentFocus(), 0);
    }

    private void insertItem(ViewGroup container, String text) {
        int dp48 = DensityUtil.dp2px(this, 48);

        TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_drawer_menu, null);
        tv.setText(text);
        tv.setTextColor(Color.BLACK);
        tv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setMinHeight(dp48);
        container.addView(tv);

        if (container.getParent() instanceof NestedScrollView) {
            NestedScrollView parent = (NestedScrollView) container.getParent();
            parent.post(() -> parent.fullScroll(View.FOCUS_DOWN));
        }
    }
}
