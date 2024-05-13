package com.sdk.cpcl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Activity_Image_File extends Activity {
    private ListView listV = null;
    private List<File> list = null;
//    private int a[] = {R.drawable.icon_folder, R.drawable.icon_prn, R.drawable.icon_file_image};
    private int a[] = {R.mipmap.img};
    private ArrayList<HashMap<String, Object>> recordItem;
    private static final String PREFS_NAME = "MyPrefsFile";
    private String FileFilterList = "prn,";
    private File FolderPath;
    //private String PrePath;
    private Context thisCon;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_prn_file);

        listV = (ListView) findViewById(R.id.list);

        Intent iFilePath = getIntent();
        FolderPath = new File(iFilePath.getStringExtra("Folder"));
        FileFilterList = iFilePath.getStringExtra("FileFilter");
        list_files();
    }

    private void list_files() {
        File[] file = FolderPath.listFiles();
        fill(file);
    }

    private void fill(File[] file) {
        SimpleAdapter adapter = null;
        recordItem = new ArrayList<HashMap<String, Object>>();
        list = new ArrayList<File>();

        List<String> fDir = new ArrayList<String>();
        List<String> fDirPath = new ArrayList<String>();
        List<String> fFile = new ArrayList<String>();
        List<String> fFilePath = new ArrayList<String>();
        List<String> fAllFile = new ArrayList<String>();
        List<String> fAllFilePath = new ArrayList<String>();
        for (File f : file) {
            if (f.isDirectory()) {
                if (!f.getName().substring(0, 1).equals(".")) {
                    fDir.add(f.getName());
                    fDirPath.add(f.getAbsolutePath());
                }
            } else {
                if (!f.getName().substring(0, 1).equals(".")) {
                    fFile.add(f.getName());
                    fFilePath.add(f.getAbsolutePath());
                }
            }
        }
        Collections.sort(fDir, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(fDirPath, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(fFile, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(fFilePath, String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < fDir.size(); i++) {
            fAllFile.add(fDir.get(i));
            fAllFilePath.add(fDirPath.get(i));
        }
        for (int i = 0; i < fFile.size(); i++) {
            fAllFile.add(fFile.get(i));
            fAllFilePath.add(fFilePath.get(i));
        }

        File fileTemp = null;
        int intFileType = 0;
        String strFilePrefix = "";
        String strFileName = "";
        for (int i = 0; i < fAllFile.size(); i++) {
            fileTemp = new File(fAllFilePath.get(i));
            strFileName = fileTemp.getName();
            strFilePrefix = strFileName.substring(strFileName.lastIndexOf(".") + 1);
            if (!fileTemp.isDirectory() && (!FileFilterList.equals("") && !FileFilterList.contains(strFilePrefix + ",")))
                continue;

            intFileType = Invalid(fileTemp);
            if (intFileType == -1) {
                list.add(fileTemp);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("picture", a[0]);
                map.put("name", strFileName);
                recordItem.add(map);
            }
//            if (intFileType == 0) {
//                list.add(fileTemp);
//                HashMap<String, Object> map = new HashMap<String, Object>();
//                map.put("picture", a[1]);
//                map.put("name", strFileName);
//                recordItem.add(map);
//            }
//            if (intFileType == 1) {
//                list.add(fileTemp);
//                HashMap<String, Object> map = new HashMap<String, Object>();
//                map.put("picture", a[2]);
//                map.put("name", strFileName);
//                recordItem.add(map);
//            }
        }

        if (list.size() == 0) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("picture", a[0]);
            map.put("name", "...");
            recordItem.add(map);
        }

        adapter = new SimpleAdapter(this, recordItem, R.layout.file_item_bar, new String[]{"picture", "name"}, new int[]{R.id.picture, R.id.text});
        listV.setAdapter(adapter);
        listV.setOnItemClickListener(new Clicker());
    }

    //�ļ���չ��
    private int Invalid(File f) {
        String strFilePrefix = "";
        String strFileName = "";
        strFileName = f.getName();
        strFilePrefix = strFileName.substring(strFileName.lastIndexOf(".") + 1);

        String strType0 = "prn,";
        String strType1 = "gif,png,bmp,jpg,";

        if (strType0.contains(strFilePrefix.toLowerCase() + ",")) {
            return 0;
        } else if (strType1.contains(strFilePrefix.toLowerCase() + ",")) {
            return 1;
        } else {
            return -1;
        }
    }

    private class Clicker implements OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (list.size() == 0)
                finish();
            else {
                File file = list.get(arg2);
                if (file.isFile()) {

                } else {
                    Intent myIntent = new Intent();
                    myIntent.setClass(Activity_Image_File.this, Activity_Image_File.class);
                    myIntent.putExtra("Folder", file.getAbsolutePath());
                    myIntent.putExtra("FileFilter", FileFilterList);
                    startActivityFromChild(Activity_Image_File.this, myIntent, 10);
                }
            }
        }
    }
}

