package com.printer.cpcldemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import com.gprinter.bean.PrinterDevices;
import com.gprinter.utils.CallbackListener;
import com.gprinter.utils.Command;
import com.gprinter.utils.ConnMethod;
import com.gprinter.utils.LogUtils;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements CallbackListener {
    TextView tvState;
    CheckBox swState;
    Printer printer=null;
    Context context;
    String TAG=MainActivity.class.getSimpleName();
    PermissionUtils permissionUtils;
    Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
           switch (msg.what){
               case 0x00:
                   String tip=(String)msg.obj;
                    Toast.makeText(context,tip,Toast.LENGTH_SHORT).show();
                   break;
               case 0x01:
                   int status=msg.arg1;
                   if (status==-1){//获取状态失败 Failed to get status
                       AlertDialog alertDialog = new AlertDialog.Builder(context)
                               .setTitle(getString(R.string.tip))
                               .setMessage(getString(R.string.status_fail))
                               .setIcon(R.mipmap.ic_launcher)
                               .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {//添加"Yes"按钮
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i) {

                                   }
                               })
                               .create();
                       alertDialog.show();
                       return;
                   }else if (status==1){
                       Toast.makeText(context,getString(R.string.status_feed),Toast.LENGTH_SHORT).show();
                       return;
                   }else if (status==0){//状态正常 Normal state
                       Toast.makeText(context,getString(R.string.status_normal),Toast.LENGTH_SHORT).show();
                       return;
                   }else if (status==-2){//状态缺纸 Out of paper status
                       Toast.makeText(context,getString(R.string.status_out_of_paper),Toast.LENGTH_SHORT).show();
                       return;
                   }else if (status==-3){//状态开盖 Status open
                       Toast.makeText(context,getString(R.string.status_open),Toast.LENGTH_SHORT).show();
                       return;
                   }else if (status==-4){ //
                       Toast.makeText(context,getString(R.string.status_overheated),Toast.LENGTH_SHORT).show();
                       return;
                   }
                   Toast.makeText(context,"unkowns"+status,Toast.LENGTH_SHORT).show();
                   break;
               case 0x02://关闭连接
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           if (printer.getPortManager()!=null){
                               printer.close();
                           }
                       }
                   }).start();
                   tvState.setText(getString(R.string.not_connected));
                   break;
               case 0x03:
                   String message=(String)msg.obj;
                   AlertDialog alertDialog = new AlertDialog.Builder(context)
                           .setTitle(getString(R.string.tip))
                           .setMessage(message)
                           .setIcon(R.mipmap.ic_launcher)
                           .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {//添加"Yes"按钮
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {

                               }
                           })
                           .create();
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
     *Initialize permissions
     */
    private void initPermission() {
        permissionUtils.requestPermissions(getString(R.string.permission),
                new PermissionUtils.PermissionListener(){
                    @Override
                    public void doAfterGrand(String... permission) {

                    }
                    @Override
                    public void doAfterDenied(String... permission) {
                        for (String p:permission) {
                            switch (p){
                                case Manifest.permission.READ_EXTERNAL_STORAGE:
                                    Utils.shortToast(context,getString(R.string.no_read));
                                    break;
                                case Manifest.permission.ACCESS_FINE_LOCATION:
                                    Utils.shortToast(context,getString(R.string.no_permission));
                                    break;
                            }
                        }
                    }
                },  Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void initView() {
        context=MainActivity.this;
        permissionUtils=new PermissionUtils(context);
        setTitle(getString(R.string.app_name)+"V"+Utils.getVersionName(context));
        tvState=(TextView)findViewById(R.id.tvState);
        swState=(CheckBox)findViewById(R.id.swState);
        printer=Printer.getInstance();//获取管理对象
    }
    /**
     * 断开连接
     * Disconnect
     * @param view
     */
    public void disconnect(View view) {
        handler.obtainMessage(0x02).sendToTarget();
    }

    /**
     * 蓝牙设备
     *Bluetooth device
     * @param view
     */
    public void blueToothDevices(View view) {
        startActivityForResult(new Intent(context, BlueToothDeviceActivity.class),0x00);
    }
    /**
     * usb设备
     *usb device
     * @param view
     */
    public void usbDevices(View view) {
        startActivityForResult(new Intent(context, UsbDeviceActivity.class),0x01);
    }
    /**
     * wifi接口
     * wifi interface
     * @param view
     */
    public void wifiDevices(View view) {
        startActivityForResult(new Intent(context, WifiDeviceActivity.class),0x02);
    }
    /**
     * 串口接口
     * Serial port
     * @param view
     */
    public void serialPortDevices(View view) {
        startActivityForResult(new Intent(context, SerialPortDeviceActivity.class),0x03);
    }
    /**
     * 打印案例
     * Print case
     * @param view
     */
    public void print(View view) {
    ThreadPoolManager.getInstance().addTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (printer.getPortManager()==null){
                           tipsToast(getString(R.string.conn_first));
                            return;
                        }
                        //打印前后查询打印机状态，部分老款打印机不支持查询请去除下面查询代码
                        //******************     查询状态     ***************************
                       if (swState.isChecked()) {
                           Command command = printer.getPortManager().getCommand();
                           int status = printer.getPrinterState(command);
                           if (status != 0) {//打印机处于不正常状态,则不发送打印任务
                               Message msg = new Message();
                               msg.what = 0x01;
                               msg.arg1 = status;
                               handler.sendMessage(msg);
                               return;
                           }
                       }
                        //***************************************************************
                        boolean result=printer.getPortManager().writeDataImmediately(PrintContent.getExample(context));
                        if (result) {
                            tipsDialog(getString(R.string.send_success));
                        }else {
                            tipsDialog(getString(R.string.send_fail));
                        }
                        LogUtils.e("send result",result);
                    } catch (IOException e) {
                        tipsDialog(getString(R.string.print_fail)+e.getMessage());
                    }catch (Exception e){
                        tipsDialog(getString(R.string.print_fail)+e.getMessage());
                    }
                }
           });
    }

    /**
     * 检查打印机状态
     *Check printer status
     * @param view
     */
    public void checkState(View view) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
                @Override
                public void run() {
                    if (printer.getPortManager()==null){
                        tipsToast(getString(R.string.conn_first));
                        return;
                    }
                    try {
                        Command command=printer.getPortManager().getCommand();
                        int status = printer.getPrinterState(command);
                        Message msg=new Message();
                        msg.what=0x01;
                        msg.arg1=status;
                        handler.sendMessage(msg);
                    } catch (IOException e) {
                        tipsDialog(getString(R.string.status_error)+e.getMessage());
                    }catch (Exception e){
                        tipsDialog(getString(R.string.status_error)+e.getMessage());
                    }
                }
            });
   }
    /**
     * @param message
     */
    private void tipsToast(String message){
        Message msg =new Message();
        msg.what=0x00;
        msg.obj=message;
        handler.sendMessage(msg);
    }
    /**
     * @param message
     */
    private void tipsDialog(String message){
        Message msg =new Message();
        msg.what=0x03;
        msg.obj=message;
        handler.sendMessage(msg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       if (resultCode== Activity.RESULT_OK){
           switch (requestCode){
               case 0x00://蓝牙返回mac地址(Bluetooth return mac address)
                   String mac =data.getStringExtra(BlueToothDeviceActivity.EXTRA_DEVICE_ADDRESS);
                   PrinterDevices blueTooth=new PrinterDevices.Build()
                           .setContext(context)
                           .setConnMethod(ConnMethod.BLUETOOTH)
                           .setMacAddress(mac)
                           .setCommand(Command.CPCL)
                           .setCallbackListener(this)
                           .build();
                   printer.connect(blueTooth);
                   break;
               case 0x01://usb返回USB名称(usb returns the USB name)
                   String name =data.getStringExtra(UsbDeviceActivity.USB_NAME);
                   UsbDevice usbDevice = Utils.getUsbDeviceFromName(context, name);
                   PrinterDevices usb=new PrinterDevices.Build()
                           .setContext(context)
                           .setConnMethod(ConnMethod.USB)
                           .setUsbDevice(usbDevice)
                           .setCommand(Command.CPCL)
                           .setCallbackListener(this)
                           .build();
                   printer.connect(usb);
                   break;
               case 0x02://WIFI返回ip(WIFI return ip)
                   String ip =data.getStringExtra(WifiDeviceActivity.IP);
                   PrinterDevices wifi=new PrinterDevices.Build()
                           .setContext(context)
                           .setConnMethod(ConnMethod.WIFI)
                           .setIp(ip)
                           .setPort(9100)//打印唯一端口9100
                           .setCommand(Command.CPCL)
                           .setCallbackListener(this)
                           .build();
                   printer.connect(wifi);
                   break;
               case 0x03://串口返回路径、波特率（Serial port return path, baud rate）
                   int baudRate = data.getIntExtra(SerialPortDeviceActivity.SERIALPORT_BAUDRATE, 9600);
                   String path = data.getStringExtra(SerialPortDeviceActivity.SERIALPORT_PATH);
                   PrinterDevices serialPort=new PrinterDevices.Build()
                           .setContext(context)
                           .setConnMethod(ConnMethod.SERIALPORT)
                           .setSerialPort(path)
                           .setBaudrate(baudRate)
                           .setCommand(Command.CPCL)
                           .setCallbackListener(this)
                           .build();
                   printer.connect(serialPort);
                   break;
           }
       }
    }

    @Override
    public void onConnecting() {//连接打印机中（Connecting to the printer）
        tvState.setText(getString(R.string.conning));
    }

    @Override
    public void onCheckCommand() {//查询打印机指令（Query printer instructions）
        tvState.setText(getString(R.string.checking));
    }

    @Override
    public void onSuccess(PrinterDevices printerDevices) {//连接成功
        Toast.makeText(context,getString(R.string.conn_success),Toast.LENGTH_SHORT).show();
        tvState.setText(getString(R.string.conned)+"\n"+printerDevices.toString());
    }

    @Override
    public void onReceive(byte[] bytes) {//返回值

    }


    @Override
    public void onFailure() {//连接失败（Connection failed）
        Toast.makeText(context,getString(R.string.conn_fail),Toast.LENGTH_SHORT).show();
        handler.obtainMessage(0x02).sendToTarget();
    }

    @Override
    public void onDisconnect() {//断开连接（Disconnect）
        Toast.makeText(context,getString(R.string.disconnect),Toast.LENGTH_SHORT).show();
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
        if (printer.getPortManager()!=null){
            printer.close();
        }
        ThreadPoolManager.getInstance().stopThreadPool();
    }


}

