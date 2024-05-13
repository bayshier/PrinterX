package com.printer.tscdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gprinter.bean.PrinterDevices;
import com.gprinter.command.LabelCommand;
import com.gprinter.utils.CallbackListener;
import com.gprinter.utils.Command;
import com.gprinter.utils.ConnMethod;
import com.gprinter.utils.LogUtils;
import com.gprinter.utils.PDFUtils;
import com.gprinter.utils.SDKUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements CallbackListener {
    TextView tvState;
    CheckBox swState;
    Printer printer = null;
    Context context;
    Spinner sp_gap;
    String TAG = MainActivity.class.getSimpleName();
    PermissionUtils permissionUtils;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0x00:
                    String tip = (String) msg.obj;
                    Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
                    break;
                case 0x01:
                    int status = msg.arg1;
                    if (status == -1) {//获取状态失败
                        AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(getString(R.string.tip)).setMessage(getString(R.string.status_fail)).setIcon(R.mipmap.ic_launcher).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {//添加"Yes"按钮
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create();
                        alertDialog.show();
                        return;
                    } else if (status == 1) {
                        Toast.makeText(context, getString(R.string.status_feed), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (status == 0) {//状态正常
                        Toast.makeText(context, getString(R.string.status_normal), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (status == -2) {//状态缺纸
                        Toast.makeText(context, getString(R.string.status_out_of_paper), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (status == -3) {//状态开盖
                        Toast.makeText(context, getString(R.string.status_open), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (status == -4) {
                        Toast.makeText(context, getString(R.string.status_overheated), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
                case 0x02://关闭连接
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (printer.getPortManager() != null) {
                                printer.close();
                            }
                        }
                    }).start();

                    tvState.setText(getString(R.string.not_connected));
                    break;
                case 0x03:
                    String message = (String) msg.obj;
                    AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(getString(R.string.tip)).setMessage(message).setIcon(R.mipmap.ic_launcher).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {//添加"Yes"按钮
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create();
                    alertDialog.show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initPermission();
    }

    /**
     * 初始化权限
     */
    private void initPermission() {
        permissionUtils.requestPermissions(getString(R.string.permission), new PermissionUtils.PermissionListener() {
            @Override
            public void doAfterGrand(String... permission) {

            }

            @Override
            public void doAfterDenied(String... permission) {
                for (String p : permission) {
                    switch (p) {
                        case Manifest.permission.READ_EXTERNAL_STORAGE:
                            Utils.shortToast(context, getString(R.string.no_read));
                            break;
                        case Manifest.permission.ACCESS_FINE_LOCATION:
                            Utils.shortToast(context, getString(R.string.no_permission));
                            break;
                    }
                }
            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void initView() {
        context = MainActivity.this;
        permissionUtils = new PermissionUtils(context);
        setTitle(getString(R.string.app_name) + "V" + Utils.getVersionName(context));
        tvState = (TextView) findViewById(R.id.tvState);
        swState = (CheckBox) findViewById(R.id.swState);
        sp_gap = (Spinner) findViewById(R.id.sp_gap);
        printer = Printer.getInstance();//获取管理对象
    }

    /**
     * 断开连接
     *
     * @param view
     */
    public void disconnect(View view) {
        handler.obtainMessage(0x02).sendToTarget();
    }

    /**
     * 蓝牙设备
     *
     * @param view
     */
    public void blueToothDevices(View view) {
        startActivityForResult(new Intent(context, BlueToothDeviceActivity.class), 0x00);
    }

    /**
     * usb设备
     *
     * @param view
     */
    public void usbDevices(View view) {
        startActivityForResult(new Intent(context, UsbDeviceActivity.class), 0x01);
    }

    /**
     * wifi接口
     *
     * @param view
     */
    public void wifiDevices(View view) {
        startActivityForResult(new Intent(context, WifiDeviceActivity.class), 0x02);
    }

    /**
     * 串口接口
     *
     * @param view
     */
    public void serialPortDevices(View view) {
        startActivityForResult(new Intent(context, SerialPortDeviceActivity.class), 0x03);
    }

    /**
     * 打印案例
     *
     * @param view
     */
    public void print(View view) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    if (printer.getPortManager() == null) {
                        tipsToast(getString(R.string.conn_first));
                        return;
                    }
                    //打印前后查询打印机状态，部分老款打印机不支持查询请去除下面查询代码
                    //******************     查询状态     ***************************
                    if (swState.isChecked()) {
                        Command command = printer.getPortManager().getCommand();
                        int status = printer.getPrinterState(command, 2000);
                        if (status != 0) {//打印机处于不正常状态,则不发送打印任务
                            Message msg = new Message();
                            msg.what = 0x01;
                            msg.arg1 = status;
                            handler.sendMessage(msg);
                            return;
                        }
                    }
                    //***************************************************************
                    boolean result = printer.getPortManager().writeDataImmediately(PrintContent.getLabel(context, sp_gap.getSelectedItemPosition()));
                    if (result) {
                        tipsDialog(getString(R.string.send_success));
                    } else {
                        tipsDialog(getString(R.string.send_fail));
                    }
                    LogUtils.e("send result", result);
                } catch (IOException e) {
                    tipsDialog(getString(R.string.print_fail) + e.getMessage());
                } catch (Exception e) {
                    tipsDialog(getString(R.string.print_fail) + e.getMessage());
                } finally {
                    if (printer.getPortManager() == null) {
                        printer.close();
                    }
                }
            }
        });

    }

    /**
     * 打印xml 布局
     *
     * @param view
     */
    public void xml(View view) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                if (printer.getPortManager() == null) {
                    tipsToast(getString(R.string.conn_first));
                    return;
                }
                try {

                    printer.getPortManager().writeDataImmediately(PrintContent.getXmlBitmap(context));
                } catch (IOException e) {
                    tipsDialog(getString(R.string.status_error) + e.getMessage());
                } catch (Exception e) {
                    tipsDialog(getString(R.string.status_error) + e.getMessage());
                }
            }
        });
    }

    /**
     * 打印PDF
     *
     * @param view
     */
    public void printPDF(View view) {
        if (!permissionUtils.hasPermissions(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Utils.shortToast(context, getString(R.string.no_read));
            return;
        }
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    if (printer.getPortManager() == null) {
                        tipsToast(getString(R.string.conn_first));
                        return;
                    }
                    //打印前后查询打印机状态，部分老款打印机不支持查询请去除下面查询代码
                    //******************     查询状态     ***************************
                    if (swState.isChecked()) {
                        Command command = printer.getPortManager().getCommand();
                        int status = printer.getPrinterState(command, 2000);
                        if (status != 0) {//打印机处于不正常状态、则不发送打印
                            Message msg = new Message();
                            msg.what = 0x01;
                            msg.arg1 = status;
                            handler.sendMessage(msg);
                            return;
                        }
                    }
                    //***************************************************************
                    File file = null;
                    try {
                        file = new File(context.getExternalCacheDir(), "WalmartFile.pdf");
                        if (!file.exists()) {
                            // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
                            // the cache directory.
                            InputStream asset = context.getAssets().open("WalmartFile.pdf");
                            FileOutputStream output = new FileOutputStream(file);
                            final byte[] buffer = new byte[1024];
                            int size;
                            while ((size = asset.read(buffer)) != -1) {
                                output.write(buffer, 0, size);
                            }
                            asset.close();
                            output.close();
                        }
                    } catch (IOException e) {
                        tipsToast(getString(R.string.pdf_error));
                        return;
                    }
                    boolean result = printer.getPortManager().writePDFToTsc(file, 576, 0, true, true, false, 160);
                    if (result) {
                        tipsDialog(getString(R.string.send_success));
                    } else {
                        tipsDialog(getString(R.string.send_fail));
                    }
                    LogUtils.e("send result", result);
                } catch (IOException e) {
                    tipsDialog(getString(R.string.disconnect) + "\n" + getString(R.string.print_fail) + e.getMessage());
                } catch (Exception e) {
                    tipsDialog(getString(R.string.print_fail) + e.getMessage());
                }
            }
        });
    }

    /**
     * 检查标签打印机状态
     *
     * @param view
     */
    public void checkState(View view) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                if (printer.getPortManager() == null) {
                    tipsToast(getString(R.string.conn_first));
                    return;
                }
                try {
                    Command command = printer.getPortManager().getCommand();
                    int status = printer.getPrinterState(command, 2000);
                    Message msg = new Message();
                    msg.what = 0x01;
                    msg.arg1 = status;
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    tipsDialog(getString(R.string.status_error) + e.getMessage());
                } catch (Exception e) {
                    tipsDialog(getString(R.string.status_error) + e.getMessage());
                }
            }
        });
    }

    /**
     * 提示弹框
     *
     * @param message
     */
    private void tipsToast(String message) {
        Message msg = new Message();
        msg.what = 0x00;
        msg.obj = message;
        handler.sendMessage(msg);
    }

    /**
     * 提示弹框
     *
     * @param message
     */
    private void tipsDialog(String message) {
        Message msg = new Message();
        msg.what = 0x03;
        msg.obj = message;
        handler.sendMessage(msg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0x00://蓝牙返回mac地址
                    String mac = data.getStringExtra(BlueToothDeviceActivity.EXTRA_DEVICE_ADDRESS);
                    Log.e(TAG, SDKUtils.bytesToHexString(mac.getBytes()));
                    PrinterDevices blueTooth = new PrinterDevices.Build().setContext(context).setConnMethod(ConnMethod.BLUETOOTH).setMacAddress(mac).setCommand(Command.TSC).setCallbackListener(this).build();
                    printer.connect(blueTooth);
                    break;
                case 0x01://usb返回USB名称
                    String name = data.getStringExtra(UsbDeviceActivity.USB_NAME);
                    UsbDevice usbDevice = Utils.getUsbDeviceFromName(context, name);
                    PrinterDevices usb = new PrinterDevices.Build().setContext(context).setConnMethod(ConnMethod.USB).setUsbDevice(usbDevice).setCommand(Command.TSC).setCallbackListener(this).build();
                    printer.connect(usb);
                    break;
                case 0x02://WIFI返回ip
                    String ip = data.getStringExtra(WifiDeviceActivity.IP);
                    PrinterDevices wifi = new PrinterDevices.Build().setContext(context).setConnMethod(ConnMethod.WIFI).setIp(ip).setPort(9100)//打印唯一端口9100
                            .setCommand(Command.TSC).setCallbackListener(this).build();
                    printer.connect(wifi);
                    break;
                case 0x03://串口返回路径、波特率
                    int baudRate = data.getIntExtra(SerialPortDeviceActivity.SERIALPORT_BAUDRATE, 9600);
                    String path = data.getStringExtra(SerialPortDeviceActivity.SERIALPORT_PATH);
                    PrinterDevices serialPort = new PrinterDevices.Build().setContext(context).setConnMethod(ConnMethod.SERIALPORT).setSerialPort(path).setBaudrate(baudRate).setCommand(Command.TSC).setCallbackListener(this).build();
                    printer.connect(serialPort);
                    break;
            }
        }
    }

    @Override
    public void onConnecting() {//连接打印机中
        tvState.setText(getString(R.string.conning));
    }

    @Override
    public void onCheckCommand() {//查询打印机指令
        tvState.setText(getString(R.string.checking));
    }

    @Override
    public void onSuccess(PrinterDevices devices) {//连接成功
        Toast.makeText(context, getString(R.string.conn_success), Toast.LENGTH_SHORT).show();
        tvState.setText(devices.toString());
    }

    @Override
    public void onReceive(byte[] bytes) {

    }

    @Override
    public void onFailure() {//连接失败
        Toast.makeText(context, getString(R.string.conn_fail), Toast.LENGTH_SHORT).show();
        handler.obtainMessage(0x02).sendToTarget();
    }

    @Override
    public void onDisconnect() {//断开连接
        Toast.makeText(context, getString(R.string.disconnect), Toast.LENGTH_SHORT).show();
        handler.obtainMessage(0x02).sendToTarget();
    }

    //申请权限返回
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionUtils.handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (printer.getPortManager() != null) {
            printer.close();
        }
    }


}
