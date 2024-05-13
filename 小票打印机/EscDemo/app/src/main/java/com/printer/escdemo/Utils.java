package com.printer.escdemo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 作者： Circle
 * 创造于 2018/5/24.
 */
public class Utils {
    /**
     * 通过USB名称获取设备
     * @param context
     * @param usbName
     * @return
     */
    public static UsbDevice getUsbDeviceFromName(Context context, String usbName) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> usbDeviceList = usbManager.getDeviceList();
        return usbDeviceList.get(usbName);
    }
    /**
     * 短时间吐司
     * @param context
     * @param msg
     */
    public static void shortToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
    /**
     * 合拼两个数组
     * @param first
     * @param second
     * @param <T>
     * @return
     */
    public static <T> byte[] concat(byte[] first, byte[] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
    /**
     * 检测WiFi的IP是否输入正确
     * @param addr
     * @return
     */
    public static boolean checkIP(String addr)

    {
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))
        {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        boolean ipAddress = mat.find();
        //============对之前的ip判断的bug在进行判断
        if (ipAddress==true){
            String ips[] = addr.split("\\.");
            if(ips.length==4){
                try{
                    for(String ip : ips){
                        if(Integer.parseInt(ip)<0||Integer.parseInt(ip)>255){
                            return false;
                        }
                    }
                }catch (Exception e){
                    return false;
                }
                return true;
            }else{
                return false;
            }
        }
        return ipAddress;
    }
    /**
     * 获取指定包名的版本号
     *
     * @param context
     *            本应用程序上下文
     * @return
     * @throws Exception
     */
    public static String getVersionName(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";

    }
    /**
     * 获取Assets文件
     * @param fileName
     * @return
     */
    public static byte [] getAssetsFile(Context context,String fileName) {
        InputStream in = null;
        ByteArrayOutputStream outStream = null;
        byte [] data=null;
        try {
            in = context.getResources().getAssets().open(fileName);
            outStream = new ByteArrayOutputStream();
            //创建byte数组
            byte[]  buffer = new byte[1024*1024];
            //将文件中的数据读到byte数组中
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            data = outStream.toByteArray();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (in!=null){
                    in.close();
                }
                if (outStream!=null){
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }
}
