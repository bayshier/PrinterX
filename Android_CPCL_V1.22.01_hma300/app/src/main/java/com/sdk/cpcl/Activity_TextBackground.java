package com.sdk.cpcl;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import cpcl.PrinterHelper;


public class Activity_TextBackground extends Activity {

    private EditText txtText;
    private EditText txtformat_x;
    private EditText txtformat_y;
    private EditText setmag_x;
    private EditText setmag_y;
    private Spinner spnformat_font;
    private CheckBox cb_textformat_bold;
    private Context mContext;
    private EditText ed_background;
    private ArrayAdapter arrformat_font;
    private int formatfont=16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__text_background);
        initView();
        initData();
    }

    private void initData() {
        String[] sList = "16,24,48".split(",");
        arrformat_font = new ArrayAdapter<String>(Activity_TextBackground.this,android.R.layout.simple_spinner_item, sList);
        arrformat_font.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnformat_font.setAdapter(arrformat_font);
        spnformat_font.setOnItemSelectedListener(new OnItemSelectedformatfont());
    }

    private void initView() {
        mContext = getApplicationContext();
        txtText = (EditText) findViewById(R.id.txtText);
        txtformat_x = (EditText) findViewById(R.id.txtformat_x);
        txtformat_y = (EditText) findViewById(R.id.txtformat_y);
        setmag_x = (EditText) findViewById(R.id.setmag_x);
        setmag_y = (EditText) findViewById(R.id.setmag_y);
        spnformat_font = (Spinner) findViewById(R.id.spnformat_font);
        cb_textformat_bold = (CheckBox) findViewById(R.id.cb_textformat_bold);
        ed_background = (EditText) findViewById(R.id.ed_background);
    }
    public void onClickPrint(View view){
        try{
            String data = txtText.getText().toString();
            String str_x = txtformat_x.getText().toString();
            String str_y = txtformat_y.getText().toString();
            String str_setmage_x = setmag_x.getText().toString();
            String str_setmage_y = setmag_y.getText().toString();
            String str_background = ed_background.getText().toString();
            if (TextUtils.isEmpty(data)||TextUtils.isEmpty(str_x)||TextUtils.isEmpty(str_y)||TextUtils.isEmpty(str_setmage_x)
                    ||TextUtils.isEmpty(str_setmage_y)||TextUtils.isEmpty(str_background)){
                Toast.makeText(mContext,getString(R.string.activity_1dbarcodes_no_data),Toast.LENGTH_SHORT).show();
                return;
            }
            int int_x = Integer.valueOf(str_x);
            int int_y = Integer.valueOf(str_y);
            int int_setmage_x = Integer.valueOf(str_setmage_x);
            int int_setmage_y = Integer.valueOf(str_setmage_y);
            int int_background = Integer.valueOf(str_background);
            PrinterHelper.printAreaSize("0","200","200","500","1");
            PrinterHelper.SetMag(""+int_setmage_x,""+int_setmage_y);
            PrinterHelper.PrintBackground(int_x,int_y,formatfont,int_background,data);
            PrinterHelper.SetMag("1","1");
            if ("1".equals(Activity_Main.paper)) {
                PrinterHelper.Form();
            }
            PrinterHelper.Print();

        }catch (Exception e){

        }
    }
    private class OnItemSelectedformatfont implements AdapterView.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3)
        {
            switch (arg2) {
                case 0:
                    formatfont=55;
                    break;
                case 1:
                    formatfont=24;
                    break;
                case 2:
                    //56
                    formatfont=4;
                    break;

                default:
                    break;
            }

        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0)
        {
            // TODO Auto-generated method stub
        }
    }
}
