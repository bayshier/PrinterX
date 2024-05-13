package com.sdk.cpcl;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import cpcl.PublicFunction;


public class Activity_Print_Image extends Activity {
    String TempFilePath = Environment.getExternalStorageDirectory().toString() + "/HPRTSDKSample/";
    String TempFileName = TempFilePath + "temp.jpg";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_print_image);

        if (PublicFunction.ExistSDCard()) {
            File TempFolder = new File(TempFilePath);
            if (!TempFolder.exists())
                TempFolder.mkdirs();
        } else {
            Log.d("SDKSample", (new StringBuilder("Activity_Print_Image --> onCreate ")).append("SD card is not avaiable/writeable right now.").toString());
            this.finish();
        }
    }

    public void onClickPhoto(View view) {
        if (!checkClick.isClickEvent()) return;

        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(TempFileName)));
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            Log.d("HPRTSDKSample", (new StringBuilder("Activity_Print_Image --> onClickPhoto ")).append(e.getMessage()).toString());
        }
    }

    public void onClickGallery(View view) {
        if (!checkClick.isClickEvent()) return;

        try {
            Intent myIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(myIntent, 20);
        } catch (Exception e) {
            Log.d("HPRTSDKSample", (new StringBuilder("Activity_Print_Image --> onClickPrint ")).append(e.getMessage()).toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            String strFilePath;
            Bitmap bmp = null;
            Cursor cursor;

            switch (resultCode) {
                case Activity.RESULT_OK:
                    new DateFormat();
                    String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
                    Toast.makeText(this, name, Toast.LENGTH_LONG).show();

                    String path = TempFilePath + name;
                    File file = new File(TempFileName);
                    boolean blnOK = file.renameTo(new File(path));

                    bmp = BitmapFactory.decodeFile(path);
                    //HPRTPrinterHelper.PrintBitmap(bmp,0,0);
                case 20:
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    strFilePath = cursor.getString(columnIndex);
                    cursor.close();
                    bmp = BitmapFactory.decodeFile(strFilePath);
                    //HPRTPrinterHelper.PrintBitmap(bmp,0,0);

                default:
                    return;
            }
        } catch (Exception e) {
            Log.d("SDKSample", (new StringBuilder("Activity_Main --> onActivityResult ")).append(e.getMessage()).toString());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
