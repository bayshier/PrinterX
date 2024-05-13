package com.sdk.cpcl;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Created by NO on 2017/9/14.
 */

public class Utility {
    public static void checkBlueboothPermission(Activity context, String permission, String[] requestPermissions, Callback callback) {
        if (Build.VERSION.SDK_INT >= 23) {
            //校验是否已具有模糊定位权限
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, requestPermissions, 100);
            } else {
                //具有权限
                callback.permit();
                return;
            }
        } else {
            //系统不高于6.0直接执行
            callback.permit();
        }
    }

    public interface Callback {
        /**
         * API>=23 允许权限
         */
        void permit();


    }

    public static Bitmap Tobitmap(Bitmap bitmap, int width, int height) {
        Bitmap target = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(target);
        canvas.drawBitmap(bitmap, null, new Rect(0, 0, target.getWidth(), target.getHeight()), null);
        return target;
    }

    //width：目标宽度，pageWidthPoint：初始宽度，pageHeightPoint：初始高度
    public static int getHeight(int width, int pageWidthPoint, int pageHeightPoint) {
        double bili = width / (double) pageWidthPoint;
        return (int) (pageHeightPoint * bili);
    }

    public static Bitmap Tobitmap90(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        // 设置旋转角度
        matrix.setRotate(90);
        // 重新绘制Bitmap
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }

    //判断是否是整数
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static void show(final Context context, final String message) {
        if (context == null || ((Activity) context).isFinishing()) {
            return;
        }
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static File uriToFile(Context context, Uri uri){
        String docId = "";
        try {
            try {
                docId = DocumentsContract.getDocumentId(uri);
            }catch (Exception e){
                try {
                    docId = DocumentsContract.getTreeDocumentId(uri);
                }catch (Exception e1){
                    docId = "";}
            }
            String[] split = docId.split(":");
            String type = split[0];
            if(type.equals("primary")){
                return new File(Environment.getExternalStorageDirectory()+"/"+split[1]);
            }else{
                return new File(type);
            }
        }catch (Exception e){

        }
        return null;
    }
}
