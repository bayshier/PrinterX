package com.sdk.cpcl;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import cpcl.PublicFunction;


public class Activity_Setting extends Activity {
    private Spinner spnCodepage = null;
    private Spinner spnCut = null;
    private Spinner spnCashdrawer = null;
    private Spinner spnBuzzer = null;
    private Spinner spnFeeds = null;

    private ArrayAdapter arrCodepage;
    private ArrayAdapter arrCut;
    private ArrayAdapter arrCashdrawer;
    private ArrayAdapter arrBuzzer;
    private ArrayAdapter arrFeeds;

    private PublicFunction PFun = null;
    private Context thisCon = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_setting);

        spnCodepage = (Spinner) findViewById(R.id.spnCodepage);
        arrCodepage = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        arrCodepage = ArrayAdapter.createFromResource(this, R.array.codepage, android.R.layout.simple_spinner_item);
        arrCodepage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCodepage.setAdapter(arrCodepage);
        spnCodepage.setOnItemSelectedListener(new OnItemSelectedCodepage());

        spnCut = (Spinner) findViewById(R.id.spnCut);
        arrCut = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        arrCut = ArrayAdapter.createFromResource(this, R.array.cut_paper_list, android.R.layout.simple_spinner_item);
        arrCut.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCut.setAdapter(arrCut);
        spnCut.setOnItemSelectedListener(new OnItemSelectedCut());

        spnCashdrawer = (Spinner) findViewById(R.id.spnCashdrawer);
        arrCashdrawer = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        arrCashdrawer = ArrayAdapter.createFromResource(this, R.array.open_cashdrawer_list, android.R.layout.simple_spinner_item);
        arrCashdrawer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCashdrawer.setAdapter(arrCashdrawer);
        spnCashdrawer.setOnItemSelectedListener(new OnItemSelectedCashdrawer());

        spnBuzzer = (Spinner) findViewById(R.id.spnBuzzer);
        arrBuzzer = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        arrBuzzer = ArrayAdapter.createFromResource(this, R.array.buzzer_list, android.R.layout.simple_spinner_item);
        arrBuzzer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBuzzer.setAdapter(arrBuzzer);
        spnBuzzer.setOnItemSelectedListener(new OnItemSelectedBuzzer());

        spnFeeds = (Spinner) findViewById(R.id.spnFeeds);
        arrFeeds = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        arrFeeds = ArrayAdapter.createFromResource(this, R.array.feeds_list, android.R.layout.simple_spinner_item);
        arrFeeds.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFeeds.setAdapter(arrFeeds);
        spnFeeds.setOnItemSelectedListener(new OnItemSelectedFeeds());

        thisCon = this.getApplicationContext();
        PFun = new PublicFunction(thisCon);
        InitSetting();
    }

    private void InitSetting() {
        String SettingValue = "";
        SettingValue = PFun.ReadSharedPreferencesData("Codepage");
        if (SettingValue.equals("")) {
            PFun.WriteSharedPreferencesData("Codepage", "0,PC437(USA:Standard Europe)");
            SettingValue = "0";
        }
        spnCodepage.setSelection(Integer.parseInt(SettingValue.split(",")[0]));

        SettingValue = PFun.ReadSharedPreferencesData("Cut");
        if (SettingValue.equals("")) {
            PFun.WriteSharedPreferencesData("Cut", "0");
            SettingValue = "0";
        }
        spnCut.setSelection(Integer.parseInt(SettingValue));

        SettingValue = PFun.ReadSharedPreferencesData("Cashdrawer");
        if (SettingValue.equals("")) {
            PFun.WriteSharedPreferencesData("Cashdrawer", "0");
            SettingValue = "0";
        }
        spnCashdrawer.setSelection(Integer.parseInt(SettingValue));

        SettingValue = PFun.ReadSharedPreferencesData("Buzzer");
        if (SettingValue.equals("")) {
            PFun.WriteSharedPreferencesData("Buzzer", "0");
            SettingValue = "0";
        }
        spnBuzzer.setSelection(Integer.parseInt(SettingValue));

        SettingValue = PFun.ReadSharedPreferencesData("Feeds");
        if (SettingValue.equals("")) {
            PFun.WriteSharedPreferencesData("Feeds", "0");
            SettingValue = "0";
        }
        spnFeeds.setSelection(Integer.parseInt(SettingValue));
    }

    private class OnItemSelectedCodepage implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            String sCodepage = arrCodepage.getItem(arg2).toString();
            PFun.WriteSharedPreferencesData("Codepage", String.valueOf(arg2) + "," + sCodepage);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }

    private class OnItemSelectedCut implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            PFun.WriteSharedPreferencesData("Cut", String.valueOf(arg2));
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }

    private class OnItemSelectedCashdrawer implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            PFun.WriteSharedPreferencesData("Cashdrawer", String.valueOf(arg2));
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }

    private class OnItemSelectedBuzzer implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            PFun.WriteSharedPreferencesData("Buzzer", String.valueOf(arg2));
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }

    private class OnItemSelectedFeeds implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            PFun.WriteSharedPreferencesData("Feeds", String.valueOf(arg2));
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }
}
