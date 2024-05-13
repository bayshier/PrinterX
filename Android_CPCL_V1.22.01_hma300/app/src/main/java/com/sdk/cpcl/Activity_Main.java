package com.sdk.cpcl;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static cpcl.PrinterHelper.Print;
import static cpcl.PrinterHelper.getElectricity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cpcl.BTOperator;
import cpcl.Config;
import cpcl.IPort;
import cpcl.PrinterHelper;
import cpcl.PublicFunction;
import cpcl.listener.DisConnectBTListener;
import rx.functions.Action1;


public class Activity_Main extends Activity {
    private Context thisCon = null;
    private BluetoothAdapter mBluetoothAdapter;
    private PublicFunction PFun = null;
    private PublicAction PAct = null;

    private Button btnWIFI = null;
    private Button btnBT = null;
    private Button btnUSB = null;

    private Spinner spnPrinterList = null;
    private TextView txtTips = null;
    private Button btnOpenCashDrawer = null;
    private Button btnSampleReceipt = null;
    private Button btn1DBarcodes = null;
    private Button btnQRCode = null;
    private Button btnPDF417 = null;
    private Button btnCut = null;
//	private Button btnPageMode=null;

    private EditText edtTimes = null;

    private ArrayAdapter arrPrinterList;
    private String ConnectType = "";
    private String PrinterName = "";
    private String PortParam = "";

    private UsbManager mUsbManager = null;
    private UsbDevice device = null;
    private static final String ACTION_USB_PERMISSION = "com.HPRTSDKSample";
    private PendingIntent mPermissionIntent = null;
    private static IPort Printer = null;
    private Handler handler;
    private ProgressDialog dialog;
    public static String paper = "0";
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static String[] wifi_PERMISSIONS = {
            "android.permission.CHANGE_WIFI_STATE",
            "android.permission.ACCESS_WIFI_STATE"
    };
    private ExecutorService executorService;
    public static final String PAPERTYPE = "papertype";
    public static final String LABEL = "1";
    private static final int threeInch = 0;
    private static final int fourInch = 1;
    private static final int fourInch_Receipt = 0;
    private static final int fourInch_Label = 1;
    private static final int fourInch_Two_BM = 2;
    private static final int fourInch_Three_BM = 3;
    private static final int fourInch_Four_BM = 4;
    private static final int requestCode = 1;

    private static final int REQUEST_ENABLE_BT = 2;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(BuildConfig.VERSION_NAME);
        try {
            thisCon = this;
            btnWIFI = (Button) findViewById(R.id.btnWIFI);
            btnUSB = (Button) findViewById(R.id.btnUSB);
            btnBT = (Button) findViewById(R.id.btnBT);

            spnPrinterList = (Spinner) findViewById(R.id.spn_printer_list);
            txtTips = (TextView) findViewById(R.id.txtTips);
            btnSampleReceipt = (Button) findViewById(R.id.btnSampleReceipt);
            btnOpenCashDrawer = (Button) findViewById(R.id.btnOpenCashDrawer);
            btn1DBarcodes = (Button) findViewById(R.id.btn1DBarcodes);
            btnQRCode = (Button) findViewById(R.id.btnQRCode);
            btnPDF417 = (Button) findViewById(R.id.btnPDF417);
            btnCut = (Button) findViewById(R.id.btnCut);
            btnGetStatus = (Button) findViewById(R.id.btnGetStatus);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                mPermissionIntent = PendingIntent.getBroadcast(thisCon, 0, new Intent(ACTION_USB_PERMISSION), FLAG_IMMUTABLE);
            } else {
                mPermissionIntent = PendingIntent.getBroadcast(thisCon, 0, new Intent(ACTION_USB_PERMISSION), 0);
            }
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            thisCon.registerReceiver(mUsbReceiver, filter);

            IntentFilter intent = new IntentFilter();
            intent.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, intent);

            PAct = new PublicAction(thisCon);
            InitSetting();
            this.spnPrinterList.setOnItemSelectedListener(new OnItemSelectedPrinter());
            //Enable Bluetooth
            EnableBluetooth();
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // TODO Auto-generated method stub
                    super.handleMessage(msg);
                    if (msg.what == 1) {
                        Toast.makeText(thisCon, "succeed", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    } else {
                        Toast.makeText(thisCon, "failure", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                }
            };
            executorService = Executors.newSingleThreadExecutor();
            PrinterHelper.setDisConnectBTListener(disConnectBTListener);

        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> onCreate ")).append(e.getMessage()).toString());
        }
    }

    private void InitSetting() {
        PFun = new PublicFunction(thisCon);
        String paper = PFun.ReadSharedPreferencesData(PAPERTYPE);
        if (!"".equals(paper)) {
            Activity_Main.paper = paper;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        String paper = PFun.ReadSharedPreferencesData(PAPERTYPE);
        if (!TextUtils.isEmpty(paper)) {
            Activity_Main.paper = paper;
        }
        String[] arrpaper = getResources().getStringArray(R.array.activity_main_papertype);
        if (LABEL.equals(Activity_Main.paper)) {
            btnOpenCashDrawer.setText(getResources().getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + arrpaper[1]);
        } else {
            btnOpenCashDrawer.setText(getResources().getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + arrpaper[0]);
        }
    }


    private class OnItemSelectedPrinter implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            PrinterName = arrPrinterList.getItem(arg2).toString();
            CapturePrinterFunction();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private DisConnectBTListener disConnectBTListener = () -> runOnUiThread(() -> {
        txtTips.setText("BT Disconnect");
        Toast.makeText(thisCon, "BT Disconnect", Toast.LENGTH_SHORT).show();
    });

    //EnableBluetooth
    private boolean EnableBluetooth() {
        boolean bRet = false;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled())
                return true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                RxPermissions rxPermissions = new RxPermissions(this);
                rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT).subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            if (ActivityCompat.checkSelfPermission(Activity_Main.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                                return;
                            }
                        }
                    }
                });
            } else {
                mBluetoothAdapter.enable();
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!mBluetoothAdapter.isEnabled()) {
                bRet = true;
                Log.d("PRTLIB", "BTO_EnableBluetooth --> Open OK");
            }
        } else {
            Log.d("HPRTSDKSample", (new StringBuilder("Activity_Main --> EnableBluetooth ").append("Bluetooth Adapter is null.")).toString());
        }
        return bRet;
    }

    //call back by scan bluetooth printer
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        try {
            String strIsConnected;
            if (data == null)
                return;
            switch (resultCode) {
                case RESULT_CANCELED:
                    connectBT(data.getStringExtra("SelectedBDAddress"));
                    break;
                case PrinterHelper.ACTIVITY_CONNECT_WIFI:
                    String resultWIFI = data.getExtras().getString("is_connected");
                    if (resultWIFI.equals("OK"))
                        txtTips.setText(thisCon.getString(R.string.activity_main_connected));
                    return;
                case RESULT_OK:
                    if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                        setPrintDialog(data);
                        return;
                    }
                    if (requestCode == requestCode) {
                        File file = Utility.uriToFile(thisCon, data.getData());
                        if (file != null) {
                            if (file.getPath().contains(Environment.getExternalStorageDirectory().getPath())) {
                                printerUpgrade(file);
                            } else {
                                ToastUtils.showShort("not support");
                            }
                        }
                        return;
                    }
                case PrinterHelper.ACTIVITY_PRNFILE:
                    String strPRNFile = data.getExtras().getString("FilePath");
                    PrinterHelper.PrintBinaryFile(strPRNFile);
                    return;
            }
        } catch (Exception e) {
            Log.e("SDKSample", (new StringBuilder("Activity_Main --> onActivityResult ")).append(e.getMessage()).toString());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void printerUpgrade(File file) {
        ProgressDialog dialog = new ProgressDialog(Activity_Main.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setMax(100);
        dialog.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    FileInputStream fileInputStream = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        boolean manager = Environment.isExternalStorageManager();
                        Log.d("Print", "isExternalStorageManager: " + manager);
                        if (!manager) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, 1024);
                            dialog.dismiss();
                            return;
                        }
                        File destFile = new File(thisCon.getExternalCacheDir(), file.getName());
                        if (!destFile.exists()) {
                            destFile.mkdir();
                        }
                        com.blankj.utilcode.util.FileUtils.copyFile(file, destFile);
                        if (destFile == null) {
                            dialog.dismiss();
                            return;
                        }
                        fileInputStream = new FileInputStream(destFile);
                    } else {
                        fileInputStream = new FileInputStream(file);
                    }
                    PrinterHelper.sendUpdateToPrint(1, fileInputStream, new PrinterHelper.setOnProgress() {
                        @Override
                        public void onProgress(int progress) {
                            dialog.setProgress(progress);
                            if (progress == 100) {
                                dialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(thisCon, "Upgrade succeed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void failure() {
                            dialog.dismiss();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(thisCon, "Upgrade fail", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    dialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(thisCon, "Upgrade fail", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.d("Print", "Exception: " + e.getMessage());
                }
            }
        }.start();
    }

    private void connectBT(final String selectedBDAddress) {
        if (TextUtils.isEmpty(selectedBDAddress))
            return;
        final ProgressDialog progressDialog = new ProgressDialog(Activity_Main.this);
        progressDialog.setMessage(getString(R.string.activity_devicelist_connect));
        progressDialog.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    final int result = PrinterHelper.portOpenBT(getApplicationContext(), selectedBDAddress);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result == 0)
                                txtTips.setText(thisCon.getString(R.string.activity_main_connected));
                            else
                                txtTips.setText(thisCon.getString(R.string.activity_main_connecterr));
                        }
                    });
                    progressDialog.dismiss();
                } catch (Exception e) {
                    progressDialog.dismiss();
                }
            }
        }.start();
    }

    private void setPrintDialog(Intent data) {
        List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
        String path = selectList.get(0).getPath();
        Log.d("Print", "image path: " + path);
        if (path.contains("content://")) {
            Uri uri = Uri.parse(path);
            path = FileUtils.getFilePathByUri_BELOWAPI11(uri, this);
            Log.d("Print", "image path: " + path);
        }
        final Bitmap bmp = BitmapFactory.decodeFile(path);
        if (bmp == null) {
            Toast.makeText(thisCon, "Image error", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialogUtil alertDialogUtil = new AlertDialogUtil(Activity_Main.this);
        Builder builder = alertDialogUtil.setAlertDialog();
        View view = alertDialogUtil.setViewID(R.layout.item_print);
        final RadioButton rbInch3 = view.findViewById(R.id.rb_inch3);
        final RadioButton rbInch2 = view.findViewById(R.id.rb_inch2);
        final RadioButton rbInch4 = view.findViewById(R.id.rb_inch4);
        final RadioButton rbZero = view.findViewById(R.id.rb_zero);
        final RadioButton rbShake = view.findViewById(R.id.rb_shake);
        final RadioButton rbBlackW = view.findViewById(R.id.rb_bw);
        final EditText edLight = view.findViewById(R.id.ed_light);
        final RadioButton rbNo = view.findViewById(R.id.rb_lzo_no);
        final RadioButton rbIntegrated = view.findViewById(R.id.rb_integrated);
        builder.setNegativeButton(R.string.activity_wifi_btncancel, null);
        builder.setPositiveButton(R.string.activity_global_print, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String strLight = edLight.getText().toString();
                if (TextUtils.isEmpty(strLight) || strLight.length() > 4 || !Utility.isInteger(strLight) || Integer.valueOf(strLight) < -100 || Integer.valueOf(strLight) > 100) {
                    Toast.makeText(thisCon, R.string.activity_main_light_message, Toast.LENGTH_SHORT).show();
                    return;
                }
                setPrintImage(bmp, Integer.valueOf(strLight), rbInch3.isChecked() ? 576 : rbInch2.isChecked() ? 384 : rbInch4.isChecked() ? 832 : 0, !rbZero.isChecked(), rbShake.isChecked() ? 1 : (rbBlackW.isChecked() ? 0 : 2), rbNo.isChecked() ? 0 : (rbIntegrated.isChecked() ? 1 : 2));
            }
        });
        builder.show();
    }

    private void setPrintImage(final Bitmap bitmap, final int light, final int size, final boolean isRotate, final int sype, final int compressType) {
        dialog = new ProgressDialog(Activity_Main.this);
        dialog.setMessage("Printing.....");
        dialog.setProgress(100);
        dialog.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Bitmap bitmapPrint = bitmap;
                    if (isRotate)
                        bitmapPrint = Utility.Tobitmap90(bitmapPrint);
                    if (size != 0)
                        bitmapPrint = Utility.Tobitmap(bitmapPrint, size, Utility.getHeight(size, bitmapPrint.getWidth(), bitmapPrint.getHeight()));
                    int printImage = PrinterHelper.printBitmap(0, 0, sype, bitmapPrint, compressType, "1".equals(Activity_Main.paper), light);
                    Log.d("Print", "printImage: " + printImage);
                    if (printImage > 0) {
                        handler.sendEmptyMessage(1);
                    } else {
                        handler.sendEmptyMessage(0);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(0);
                }
            }
        }.start();
    }

    @SuppressLint("NewApi")
    public void onClickConnect(View view) {
//    	if (!checkClick.isClickEvent()) return;

        try {
            PrinterHelper.portClose();
            if (view.getId() == R.id.btnBT) {
                //获取蓝牙动态权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    RxPermissions rxPermissions = new RxPermissions(this);
                    rxPermissions.request(Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.ACCESS_FINE_LOCATION).subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if (aBoolean) {
                                ConnectType = "Bluetooth";
                                Intent intent = new Intent(thisCon, BTActivity.class);
                                intent.putExtra("TAG", 0);
                                startActivityForResult(intent, 0);
                            }
                        }
                    });
                } else {
                    RxPermissions rxPermissions = new RxPermissions(this);
                    rxPermissions.request(Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.ACCESS_FINE_LOCATION).subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if (aBoolean) {
                                ConnectType = "Bluetooth";
                                Intent intent = new Intent(thisCon, BTActivity.class);
                                intent.putExtra("TAG", 0);
                                startActivityForResult(intent, 0);
                            }
                        }
                    });
                }
            } else if (view.getId() == R.id.btnWIFI) {
                RxPermissions rxPermissions = new RxPermissions(Activity_Main.this);
                rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION).subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            ConnectType = "WiFi";
                            Intent serverIntent = new Intent(thisCon, Activity_Wifi.class);
                            serverIntent.putExtra("PN", PrinterName);
                            startActivityForResult(serverIntent, PrinterHelper.ACTIVITY_CONNECT_WIFI);
                            return;
                        }
                    }
                });
            } else if (view.getId() == R.id.btnUSB) {
                ConnectType = "USB";
                //USB not need call "iniPort"
                mUsbManager = (UsbManager) thisCon.getSystemService(Context.USB_SERVICE);
                HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
                Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

                boolean HavePrinter = false;
                while (deviceIterator.hasNext()) {
                    device = deviceIterator.next();
                    int count = device.getInterfaceCount();
                    for (int i = 0; i < count; i++) {
                        UsbInterface intf = device.getInterface(i);
                        if (intf.getInterfaceClass() == 7) {
                            HavePrinter = true;
                            mUsbManager.requestPermission(device, mPermissionIntent);
                        }
                    }
                }
                if (!HavePrinter)
                    txtTips.setText(thisCon.getString(R.string.activity_main_connect_usb_printer));
            }
        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> onClickConnect " + ConnectType)).append(e.getMessage()).toString());
        }
    }

    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if (ACTION_USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            Log.d("Print", "PID:" + device.getProductId() + " SN:" + device.getSerialNumber() + "VID:" + device.getVendorId() + "Name:" + device.getProductName());
                            int i = PrinterHelper.portOpenUSB(thisCon, device);
                            if (i != 0) {
                                txtTips.setText(thisCon.getString(R.string.activity_main_connecterr) + i);
                                return;
                            } else
                                txtTips.setText(thisCon.getString(R.string.activity_main_connected));
                            txtTips.setText("Name:" + device.getProductName());
                        } else {
                            return;
                        }
                    }
                }
                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        int count = device.getInterfaceCount();
                        for (int i = 0; i < count; i++) {
                            UsbInterface intf = device.getInterface(i);
                            //Class ID 7代表打印机
                            if (intf.getInterfaceClass() == 7) {
                                PrinterHelper.portClose();
                                txtTips.setText(R.string.activity_main_tips);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("SDKSample", (new StringBuilder("Activity_Main --> mUsbReceiver ")).append(e.getMessage()).toString());
            }
        }
    };
    private Button btnGetStatus;

    public void onClickClose(View view) {
        if (!checkClick.isClickEvent()) return;

        try {
            PrinterHelper.portClose();
            this.txtTips.setText(R.string.activity_main_tips);
            return;
        } catch (Exception e) {
            Log.e("SDKSample", (new StringBuilder("Activity_Main --> onClickClose ")).append(e.getMessage()).toString());
        }
    }

    public void onClickbtnSetting(View view) {
        if (!checkClick.isClickEvent()) return;

        try {
            Intent myIntent = new Intent(this, Activity_Setting.class);
            startActivityForResult(myIntent, PrinterHelper.ACTIVITY_IMAGE_FILE);
            startActivityFromChild(this, myIntent, 0);
        } catch (Exception e) {
            Log.e("SDKSample", (new StringBuilder("Activity_Main --> onClickClose ")).append(e.getMessage()).toString());
        }
    }

    public void onClickDo(View view) {
        if (!checkClick.isClickEvent()) return;

        if (!PrinterHelper.IsOpened()) {
            Toast.makeText(thisCon, thisCon.getText(R.string.activity_main_tips), Toast.LENGTH_SHORT).show();
            return;
        }
        String paper = PFun.ReadSharedPreferencesData("papertype");
        if (!"".equals(paper)) {
            Activity_Main.paper = paper;
        }
        if (view.getId() == R.id.btnOpenCashDrawer) {
            selectPaperSize();
        }
        if (view.getId() == R.id.btnGetStatus) {
            Intent myIntent = new Intent(this, Activity_Status.class);
            startActivityFromChild(this, myIntent, 0);
        } else if (view.getId() == R.id.btnSampleReceipt) {
            PrintSampleReceipt();
        } else if (view.getId() == R.id.btn1DBarcodes) {
            Intent myIntent = new Intent(this, Activity_1DBarcodes.class);
            startActivityFromChild(this, myIntent, 0);
        } else if (view.getId() == R.id.btnTextFormat) {
            Intent myIntent = new Intent(this, Activity_TextFormat.class);
            startActivityFromChild(this, myIntent, 0);
        } else if (view.getId() == R.id.btnPrintImageFile) {
            setPrintImage();
        } else if (view.getId() == R.id.btnUpgrade) {
            selectFirmware();
        } else if (view.getId() == R.id.btnQRCode) {
            Intent myIntent = new Intent(this, Activity_QRCode.class);
            startActivityFromChild(this, myIntent, 0);
        } else if (view.getId() == R.id.btnPrintTestPage) {
            setTestPage();
        } else if (view.getId() == R.id.btnExpress) {
            setExpress();
        } else if (view.getId() == R.id.btnReverseFeed) {
            ReverseFeed();
        } else if (view.getId() == R.id.btn_background) {
            startActivity(new Intent(Activity_Main.this, Activity_TextBackground.class));
        } else if (view.getId() == R.id.btn_printSN) {
            PrintSN();
        } else if (view.getId() == R.id.btn_font_text) {
            fontPrint();
        } else if (view.getId() == R.id.btn_get_electricity) {
            Toast.makeText(thisCon, "" + getElectricity(), Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.btn_set_bluetooth_name) {
            setBluetoothName();
        } else if (view.getId() == R.id.btnPrintSelf) {
            PrinterHelper.printSelf();
        } else if (view.getId() == R.id.btn_double_color_print) {
            doublePrint();
        } else if (view.getId() == R.id.btn_voltage) {
            Toast.makeText(thisCon, "voltage:" + PrinterHelper.getVoltage(), Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.btn_printer_version) {
            Toast.makeText(thisCon, "version:" + PrinterHelper.getPrinterVersion(), Toast.LENGTH_SHORT).show();
        }
    }

    private void doublePrint() {
        try {
            PrinterHelper.printAreaSize("0", "200", "200", "500", "1");
            PrinterHelper.setLayer(0);
            PrinterHelper.Align(PrinterHelper.LEFT);
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "66", "CODE128");
            PrinterHelper.Barcode(PrinterHelper.BARCODE, "128", "2", "1", "50", "0", "100", true, "7", "0", "5", "123456789");
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "180", "UPCA");
            PrinterHelper.Barcode(PrinterHelper.BARCODE, PrinterHelper.UPCA, "2", "1", "50", "0", "210", true, "7", "0", "5", "123456789012");
            PrinterHelper.setLayer(1);
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "310", "UPCE");
            PrinterHelper.Barcode(PrinterHelper.BARCODE, PrinterHelper.code128, "2", "1", "50", "0", "340", true, "7", "0", "5", "0234565687");
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "440", "EAN8");
            PrinterHelper.Barcode(PrinterHelper.BARCODE, PrinterHelper.EAN8, "2", "1", "50", "0", "470", true, "7", "0", "5", "12345678");
            if ("1".equals(Activity_Main.paper)) {
                PrinterHelper.Form();
            }
            PrinterHelper.Print();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> onClickWIFI ")).append(e.getMessage()).toString());
        }
    }

    private void setBluetoothName() {
        AlertDialogUtil.edTextDialog(thisCon, getString(R.string.activity_bluetooth_name), getString(R.string.activity_input_name), "", new AlertDialogUtil.setEdTextListener() {
            @Override
            public void onResult(String data) {
                int result = PrinterHelper.setBluetoothName(data);
                if (result > 0) {
                    PrinterHelper.saveParameter();
                    Toast.makeText(thisCon, getString(R.string.activity_global_cmd_send), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(thisCon, getString(R.string.send_fail) + result, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectFirmware() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("application/*");
                    startActivityForResult(intent, requestCode);
                }
            }
        });

    }

    /**
     * 选择字库打印文本
     */
    private void fontPrint() {
        try {
            PrinterHelper.printAreaSize("0", "200", "200", "500", "1");
            PrinterHelper.printTextPro(PrinterHelper.TEXT, "SIMSUN.TTF", 24, 24, 0, 0, "SIMSUN.TTF 24 Test");
            PrinterHelper.printTextPro(PrinterHelper.TEXT, "TT0003M_.TTF", 48, 48, 0, 50, "TT0003M_.TTF 48 Test");
            if ("1".equals(Activity_Main.paper)) {
                PrinterHelper.Form();
            }
            PrinterHelper.Print();
        } catch (Exception e) {
        }
    }

    private void selectPaperSize() {
        final String[] papertype = getResources().getStringArray(R.array.activity_main_paper_size);
        Builder builder = new Builder(Activity_Main.this);
        builder.setTitle(getResources().getString(R.string.activity_esc_function_print_type))
                .setItems(papertype, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                paperAlertDialog(threeInch);
                                break;
                            case 1:
                                paperAlertDialog(fourInch);
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void setExpress() {
        Builder builder = new Builder(Activity_Main.this);
        builder.setIcon(R.drawable.logo2);
        final String[] cities = getResources().getStringArray(R.array.activity_main_express);
        builder.setItems(cities, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    //申通
                    case 0:
                        STexpress();
                        break;
                    //中通
                    case 1:
                        ZTexpress();
                        break;
                    //天天
                    case 2:
                        TTexpress();
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }

    private void setTestPage() {
        try {
            PrinterHelper.printAreaSize("0", "200", "200", "1400", "1");
            PrinterHelper.Box("50", "5", "450", "400", "1");
            PrinterHelper.Align(PrinterHelper.CENTER);
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "50", "5", getResources().getString(R.string.activity_test_page));
            PrinterHelper.Align(PrinterHelper.LEFT);
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "66", "CODE128");
            PrinterHelper.Barcode(PrinterHelper.BARCODE, "128", "2", "1", "50", "0", "100", true, "7", "0", "5", "123456789");
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "180", "UPCA");
            PrinterHelper.Barcode(PrinterHelper.BARCODE, PrinterHelper.UPCA, "2", "1", "50", "0", "210", true, "7", "0", "5", "123456789012");
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "310", "UPCE");
            PrinterHelper.Barcode(PrinterHelper.BARCODE, PrinterHelper.code128, "2", "1", "50", "0", "340", true, "7", "0", "5", "0234565687");
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "440", "EAN8");
            PrinterHelper.Barcode(PrinterHelper.BARCODE, PrinterHelper.EAN8, "2", "1", "50", "0", "470", true, "7", "0", "5", "12345678");
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "570", "CODE93");
            PrinterHelper.Barcode(PrinterHelper.BARCODE, PrinterHelper.code93, "2", "1", "50", "0", "600", true, "7", "0", "5", "123456789");
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "700", "CODE39");
            PrinterHelper.Barcode(PrinterHelper.BARCODE, PrinterHelper.code39, "2", "1", "50", "0", "730", true, "7", "0", "5", "123456789");
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "830", getResources().getString(R.string.activity_esc_function_btnqrcode));
            PrinterHelper.PrintQR(PrinterHelper.BARCODE, "0", "870", "4", "6", "ABC123");
            PrinterHelper.PrintQR(PrinterHelper.BARCODE, "150", "870", "4", "6", "ABC123");
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "1000", getResources().getString(R.string.activity_test_line));
            PrinterHelper.Line("0", "1030", "400", "1030", "1");
            PrinterHelper.Text(PrinterHelper.TEXT, "8", "0", "0", "1050", getResources().getString(R.string.activity_test_box));
            PrinterHelper.Box("10", "1080", "400", "1300", "1");
            if ("1".equals(Activity_Main.paper)) {
                PrinterHelper.Form();
            }
            PrinterHelper.Print();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> onClickWIFI ")).append(e.getMessage()).toString());
        }
    }

    private void setPrintImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            //校验是否已具有模糊定位权限
            if (ContextCompat.checkSelfPermission(Activity_Main.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Activity_Main.this,
                        PERMISSIONS_STORAGE,
                        100);
                return;
            }
        }
        setStartImageActivity();
    }

    private void setStartImageActivity() {

        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine()) // Please refer to the Demo GlideEngine.java
                .isWeChatStyle(true)// 是否开启微信图片选择风格
                .selectionMode(PictureConfig.SINGLE)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    private void PrintSN() {
        try {
            String printSN = PrinterHelper.getPrintSN();
            if (TextUtils.isEmpty(printSN)) {
                Toast.makeText(thisCon, getString(R.string.activity_main_data_error), Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(thisCon, printSN, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }

    private void ReverseFeed() {
        try {
            PrinterHelper.ReverseFeed(50);
        } catch (Exception e) {

        }
    }

    private void TTexpress() {
        try {
            HashMap<String, String> pum = new HashMap<String, String>();
            pum.put("[Referred]", "蒙 锡林郭勒盟");
            pum.put("[City]", "锡林郭勒盟 包");
            pum.put("[Number]", "108");
            pum.put("[Receiver]", "渝州");
            pum.put("[Receiver_Phone]", "15182429075");
            pum.put("[Receiver_address1]", "内蒙古自治区 锡林郭勒盟 正黄旗 解放东路与");//收件人地址第一行
            pum.put("[Receiver_address2]", "外滩路交叉口62号静安中学静安小区10栋2单元");//收件人第二行（若是没有，赋值""）
            pum.put("[Receiver_address3]", "1706室");//收件人第三行（若是没有，赋值""）
            pum.put("[Sender]", "洲瑜");
            pum.put("[Sender_Phone]", "13682429075");
            pum.put("[Sender_address1]", "浙江省 杭州市 滨江区 滨盛路1505号1706室信息部,滨盛路1505号滨盛");//寄件人地址第一行
            pum.put("[Sender_address2]", "滨盛路1505号1706室信息部");//寄件人第二行（若是没有，赋值""）
            pum.put("[Barcode]", "998016450402");
            pum.put("[Waybill]", "运单号：998016450402");
            pum.put("[Product_types]", "数码产品");
            pum.put("[Quantity]", "数量：22");
            pum.put("[Weight]", "重量：22.66KG");
            Set<String> keySet = pum.keySet();
            Iterator<String> iterator = keySet.iterator();
            InputStream afis = this.getResources().getAssets().open("TTKD.txt");//打印模版放在assets文件夹里
            String path = new String(InputStreamToByte(afis), "utf-8");//打印模版以utf-8无bom格式保存
            while (iterator.hasNext()) {
                String string = (String) iterator.next();
                path = path.replace(string, pum.get(string));
            }
            PrinterHelper.printText(path);
            if ("1".equals(Activity_Main.paper)) {
                PrinterHelper.Form();
            }
            PrinterHelper.Print();
        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
        }
    }

    private void ZTexpress() {
        try {
            HashMap<String, String> pum = new HashMap<String, String>();
            pum.put("[payment]", "18");
            pum.put("[remark]", "上海");
            pum.put("[Barcode]", "376714121");
            pum.put("[orderCodeNumber]", "100");
            pum.put("[date]", "200");
            pum.put("[siteName]", "上海 上海市 长宁区");
            pum.put("[Receiver]", "申大通");
            pum.put("[Receiver_Phone]", "13826514987");
            pum.put("[Receiver_address]", "上海市宝山区共和新路47");
            pum.put("[Sender]", "快小宝");
            pum.put("[Sender_Phone]", "13826514987");
            pum.put("[Sender_address]", "上海市长宁区北曜路1178号（鑫达商务楼）");
            pum.put("[goodName1]", "鞋子");
            pum.put("[goodName2]", "衬衫");
            pum.put("[wight]", "10kg");
            pum.put("[price]", "200");
            pum.put("[payment]", "18");
            pum.put("[orderCode]", "12345");
            pum.put("[goodName]", "帽子");
            pum.put("[nowDate]", "2017.3.13");
            Set<String> keySet = pum.keySet();
            Iterator<String> iterator = keySet.iterator();
            InputStream afis = this.getResources().getAssets().open("ZhongTong.txt");//打印模版放在assets文件夹里
            String path = new String(InputStreamToByte(afis), "utf-8");//打印模版以utf-8无bom格式保存
            while (iterator.hasNext()) {
                String string = (String) iterator.next();
                path = path.replace(string, pum.get(string));
            }
            PrinterHelper.printText(path);
            if ("1".equals(Activity_Main.paper)) {
                PrinterHelper.Form();
            }
            PrinterHelper.Print();
        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
        }
    }

    private void STexpress() {
        try {
//			PrinterHelper.openEndStatic(true);
            HashMap<String, String> pum = new HashMap<String, String>();
            pum.put("[barcode]", "363604310467");
            pum.put("[distributing]", "上海 上海市 长宁区");
            pum.put("[receiver_name]", "申大通");
            pum.put("[receiver_phone]", "13826514987");
            pum.put("[receiver_address1]", "上海市宝山区共和新路4719弄共");
            pum.put("[receiver_address2]", "和小区12号306室");//收件人地址第一行
            pum.put("[sender_name]", "快小宝");//收件人第二行（若是没有，赋值""）
            pum.put("[sender_phone]", "13826514987");//收件人第三行（若是没有，赋值""）
            pum.put("[sender_address1]", "上海市长宁区北曜路1178号（鑫达商务楼）");
            pum.put("[sender_address2]", "1号楼305室");
            Set<String> keySet = pum.keySet();
            Iterator<String> iterator = keySet.iterator();
            InputStream afis = this.getResources().getAssets().open("STO_CPCL.txt");//打印模版放在assets文件夹里
            String path = new String(InputStreamToByte(afis), "utf-8");//打印模版以utf-8无bom格式保存
            while (iterator.hasNext()) {
                String string = (String) iterator.next();
                path = path.replace(string, pum.get(string));
            }
            PrinterHelper.printText(path);
            InputStream inbmp = this.getResources().getAssets().open("logo_sto_print1.png");
            Bitmap bitmap = BitmapFactory.decodeStream(inbmp);
            InputStream inbmp2 = this.getResources().getAssets().open("logo_sto_print2.png");
            Bitmap bitmap2 = BitmapFactory.decodeStream(inbmp2);
            PrinterHelper.Expanded("10", "20", bitmap, 0, 0);//向打印机发送LOGO
            PrinterHelper.Expanded("10", "712", bitmap2, 0, 0);//向打印机发送LOGO
            PrinterHelper.Expanded("10", "1016", bitmap2, 0, 0);//向打印机发送LOGO
            if ("1".equals(Activity_Main.paper)) {
                PrinterHelper.Form();
            }
            PrinterHelper.Print();
//			PrinterHelper.getEndStatus(16);
        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
        }
    }

    private byte[] InputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }

    private void paperAlertDialog(final int paperSize) {
        final String[] papertype = getResources().getStringArray(paperSize == threeInch ? R.array.activity_main_papertype : R.array.activity_main_papertype_4inch);
        Builder builder = new Builder(Activity_Main.this);
        builder.setTitle(getResources().getString(R.string.activity_esc_function_btnopencashdrawer))
                .setItems(papertype, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (paperSize == threeInch)
                            selectThreePaper(which, papertype);
                        else
                            selectFourPaper(which, papertype);
                    }
                });
        builder.create().show();
    }

    private void selectFourPaper(int which, String[] papertype) {
        try {
            switch (which) {
                case fourInch_Receipt:
                    PrinterHelper.setPaperFourInch(PrinterHelper.Paper_FourInch_Receipt);
                    break;
                case fourInch_Label:
                    PrinterHelper.setPaperFourInch(PrinterHelper.Paper_FourInch_Label);
                    break;
                case fourInch_Two_BM:
                    PrinterHelper.setPaperFourInch(PrinterHelper.Paper_FourInch_TWO_BM);
                    break;
                case fourInch_Three_BM:
                    PrinterHelper.setPaperFourInch(PrinterHelper.Paper_FourInch_THREE_BM);
                    break;
                case fourInch_Four_BM:
                    PrinterHelper.setPaperFourInch(PrinterHelper.Paper_FourInch_FOUR_BM);
                    break;
                default:
                    break;
            }
            PFun.WriteSharedPreferencesData("papertype", "" + which);
            btnOpenCashDrawer.setText(getResources().getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which]);
        } catch (Exception e) {
        }
    }

    private void selectThreePaper(int which, String[] papertype) {
        switch (which) {
            case 1:
                try {
                    PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_LABEL);
                    PFun.WriteSharedPreferencesData("papertype", "1");
                    btnOpenCashDrawer.setText(getResources().getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 0:
                try {
                    PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_RECEIPT);
                    PFun.WriteSharedPreferencesData("papertype", "0");
                    btnOpenCashDrawer.setText(getResources().getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which]);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_LEFT_TOP_BM);
                    PFun.WriteSharedPreferencesData("papertype", "2");
                    btnOpenCashDrawer.setText(getResources().getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                try {
                    PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_LEFT_BEL_BM);
                    PFun.WriteSharedPreferencesData("papertype", "3");
                    btnOpenCashDrawer.setText(getResources().getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which]);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case 4:
                try {
                    PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_RIGHT_TOP_BM);
                    PFun.WriteSharedPreferencesData("papertype", "4");
                    btnOpenCashDrawer.setText(getResources().getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which]);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case 5:
                try {
                    PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_RIGHT_BEL_BM);
                    PFun.WriteSharedPreferencesData("papertype", "5");
                    btnOpenCashDrawer.setText(getResources().getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which]);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case 6:
                try {
                    PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_CENTRAL_TOP_BM);
                    PFun.WriteSharedPreferencesData("papertype", "6");
                    btnOpenCashDrawer.setText(getResources().getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which]);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case 7:
                try {
                    PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_CENTRAL_BEL_BM);
                    PFun.WriteSharedPreferencesData("papertype", "7");
                    btnOpenCashDrawer.setText(getResources().getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which]);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case 8:
                try {
                    PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_2INCH_LEFT_TOP_BM);
                    PFun.WriteSharedPreferencesData("papertype", "8");
                    btnOpenCashDrawer.setText(getResources().getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which]);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case 9:
                try {
                    PrinterHelper.papertype_CPCL_TWO(PrinterHelper.PAGE_STYPE_2INCH_LEFT_BEL_BM);
                    PFun.WriteSharedPreferencesData("papertype", "9");
                    btnOpenCashDrawer.setText(getResources().getString(R.string.activity_esc_function_btnopencashdrawer) + ":" + papertype[which]);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void CapturePrinterFunction() {
        try {
            int[] propType = new int[1];
            byte[] Value = new byte[500];
            int[] DataLen = new int[1];
            String strValue = "";
            boolean isCheck = false;
//			if (PrinterName.equals("HM-T300")|PrinterName.equals("HM-A300")|PrinterName.equals("108B")|PrinterName.equals("R42")|PrinterName.equals("106B")) {
            btnCut.setVisibility(View.GONE);
            btnOpenCashDrawer.setVisibility(View.VISIBLE);
            btn1DBarcodes.setVisibility(View.VISIBLE);
            btnQRCode.setVisibility(View.VISIBLE);
//				btnPageMode.setVisibility(View.GONE);
            btnPDF417.setVisibility(View.GONE);
            btnWIFI.setVisibility(View.GONE);
//				btnUSB.setVisibility(View.VISIBLE);		
            btnBT.setVisibility(View.VISIBLE);
            btnSampleReceipt.setVisibility(View.GONE);
            btnGetStatus.setVisibility(View.VISIBLE);
//			}
        } catch (Exception e) {
            Log.e("SDKSample", (new StringBuilder("Activity_Main --> CapturePrinterFunction ")).append(e.getMessage()).toString());
        }
    }

    private void Test() {
        try {
//			int i = PrinterHelper.portOpenBT("34:81:F4:2C:EB:54");
//			if(i==0){
//				PrinterHelper.printAreaSize("0","200","200","200","1");
//				Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo2);
//				PrinterHelper.Expanded("0","0",bitmap,0,0);
//				PrinterHelper.Form();
//				PrinterHelper.Print();
////				PrinterHelper.PortClose();
//			}
        } catch (Exception e) {
            Log.d("Print", e.getMessage().toString());
        }
    }

    private void PrintSampleReceipt() {
        try {
            String[] ReceiptLines = getResources().getStringArray(R.array.activity_main_sample_2inch_receipt);
            PrinterHelper.LanguageEncode = "GBK";
            PrinterHelper.RowSetX("200");//设置X坐标
            PrinterHelper.Setlp("5", "2", "32");//5:字体这个是默认值。2：字体大小。32：设置的整行的行高。
            PrinterHelper.RowSetBold("2");//字体加粗2倍
            PrinterHelper.PrintData(ReceiptLines[0] + "\r\n");//小票内容
            PrinterHelper.RowSetBold("1");//关闭加粗
            PrinterHelper.RowSetX("100");
            PrinterHelper.Setlp("5", "2", "32");
            PrinterHelper.RowSetBold("2");
            PrinterHelper.PrintData(ReceiptLines[1] + "\r\n");
            PrinterHelper.RowSetBold("1");//关闭加粗
            PrinterHelper.RowSetX("100");
            for (int i = 2; i < ReceiptLines.length; i++) {
                PrinterHelper.Setlp("5", "0", "32");
                PrinterHelper.PrintData(ReceiptLines[i] + "\r\n");
            }
            PrinterHelper.RowSetX("0");
        } catch (Exception e) {
            Log.e("SDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            PrinterHelper.setIsListenerBT(false);
            PrinterHelper.portClose();
            if (mUsbReceiver != null) {
                unregisterReceiver(mUsbReceiver);
            }
            if (mReceiver != null) {
                unregisterReceiver(mReceiver);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                try {
                    PrinterHelper.portClose();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                txtTips.setText(R.string.activity_main_tips);
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d("Print", "STATE_OFF 手机蓝牙关闭");
                        if (PrinterHelper.IsOpened()) {
                            Log.d("Print", "BluetoothBroadcastReceiver:Bluetooth close");
                            try {
                                PrinterHelper.portClose();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            txtTips.setText(R.string.activity_main_tips);
                            Utility.show(thisCon, getString(R.string.activity_main_close));
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d("Print", "STATE_TURNING_OFF 手机蓝牙正在关闭");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d("Print", "STATE_ON 手机蓝牙开启");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d("Print", "STATE_TURNING_ON 手机蓝牙正在开启");
                        break;
                }
            }
        }
    };


    public static byte[] inputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096 * 2];
        int n = 0;
        while (-1 != (n = is.read(buffer))) {
            output.write(buffer, 0, n);
        }
        output.close();
        is.close();
        return output.toByteArray();
    }

    private void upDataPrint() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    PrinterHelper.sendUpdateToPrint(1, getResources().getAssets().open("hma300s(V1.0.1)_font.bin"), new PrinterHelper.setOnProgress() {
                        @Override
                        public void onProgress(int progress) {
                            Log.d("Print", "onProgress: " + progress);
                        }

                        @Override
                        public void failure() {
                            Log.d("Print", "failure");
                        }
                    });
                } catch (Exception e) {
                }
            }
        });
    }

}
