package com.printer.tscdemo;

import com.gprinter.bean.PrinterDevices;
import com.gprinter.io.BluetoothPort;
import com.gprinter.io.EthernetPort;
import com.gprinter.io.PortManager;
import com.gprinter.io.SerialPort;
import com.gprinter.io.UsbPort;
import com.gprinter.utils.Command;

import java.io.IOException;
import java.util.Vector;

/**
 * Copyright (C), 2012-2019, 打印机有限公司
 * FileName: Printer
 * Author: Circle
 * Date: 2019/12/25 19:46
 * Description: 打印机使用单例
 */
public class Printer {
    public static Printer printer=null;
    public static PortManager portManager=null;
    public final PrinterDevices devices=null;
    public Printer(){
    }
    /**
     * 单例
     * @return
     */
    public static Printer getInstance(){
       if (printer==null){
           printer=new Printer();
       }
       return printer;
    }

    /**
     * 获取打印机管理类
     * @return
     */
    public static PortManager getPortManager(){
        return portManager;
    }

    /**
     * 获取连接状态
     * @return
     */
    public static boolean getConnectState(){
        return portManager.getConnectStatus();
    }

    /**
     * 连接
     * @param devices
     */
    public static void connect(final PrinterDevices devices){
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                         if (portManager!=null) {//先close上次连接
                             portManager.closePort();
                             try {
                                  Thread.sleep(2000);
                             } catch (InterruptedException e) {
                             }
                         }
                         if (devices!=null) {
                           switch (devices.getConnMethod()) {
                               case BLUETOOTH://蓝牙
                                   portManager = new BluetoothPort(devices);
                                   portManager.openPort();
                                   break;
                               case USB://USB
                                   portManager = new UsbPort(devices);
                                   portManager.openPort();
                                   break;
                               case WIFI://WIFI
                                   portManager = new EthernetPort(devices);
                                   portManager.openPort();
                                   break;
                               case SERIALPORT://串口
                                   portManager=new SerialPort(devices);
                                   portManager.openPort();
                                   break;
                               default:
                                   break;
                           }
                       }

                }
            });
    }
    /**
     * 发送数据到打印机 字节数据
     * @param vector
     * @return true发送成功 false 发送失败
     * 打印机连接异常或断开发送时会抛异常，可以捕获异常进行处理
     */
    public static boolean sendDataToPrinter(byte [] vector) throws IOException {
        if (portManager==null){
            return false;
        }
        return portManager.writeDataImmediately(vector);
    }

    /**
     * 获取打印机状态
     * @param printerCommand 打印机命令 ESC为小票，TSC为标签 ，CPCL为面单
     * @return 返回值常见文档说明
     * @throws IOException
     */
    public static int getPrinterState(Command printerCommand, long delayMillis)throws IOException {
        return portManager.getPrinterStatus(printerCommand);
    }

    /**
     * 获取打印机电量
     * @return
     * @throws IOException
     */
    public static int getPower() throws IOException {
        return portManager.getPower();
    }
    /**
     * 获取打印机指令
     * @return
     */
    public static Command getPrinterCommand(){
        return portManager.getCommand();
    }

    /**
     * 设置使用指令
     * @param printerCommand
     */
    public static void setPrinterCommand(Command printerCommand){
        if (portManager==null){
            return;
        }
        portManager.setCommand(printerCommand);
    }
    /**
     * 发送数据到打印机 指令集合内容
     * @param vector
     * @return true发送成功 false 发送失败
     * 打印机连接异常或断开发送时会抛异常，可以捕获异常进行处理
     */
    public static boolean sendDataToPrinter(Vector<Byte> vector) throws IOException {
        if (portManager==null){
            return false;
        }
        return portManager.writeDataImmediately(vector);
    }
    /**
     * 关闭连接
     * @return
     */
    public static void close(){
        if (portManager!=null){
             portManager.closePort();
             portManager=null;
        }
    }
}
