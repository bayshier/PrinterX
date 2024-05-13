package com.sdk.cpcl;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import cpcl.PrinterHelper;


public class Activity_QRCode extends Activity {
    private Context thisCon = null;
    private Spinner spnQRCodeSize = null;
    private ArrayAdapter arrQRCodeSize;
    private Spinner spnQRCodeModel = null;
    private ArrayAdapter arrQRCodeModel;
    private Spinner spnQRCodeLevel = null;
    private Spinner spnqrcode_rotation = null;
    private ArrayAdapter arrQRCoderotation;
    private ArrayAdapter arrQRCodeLevel;
    private EditText txtQRCodeData = null;
    private EditText txtqrcode_x = null;
    private EditText txtqrcode_y = null;
    private String qrcoderotation = "";
    private String QRCodeSize = "6";
    private String QRCodeModel = "A";
    private String QRCodeLevel = "L";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_qrcode);
        thisCon = this.getApplicationContext();

        txtQRCodeData = (EditText) this.findViewById(R.id.txtQRCodeData);
        txtqrcode_x = (EditText) this.findViewById(R.id.txtqrcode_x);
        txtqrcode_y = (EditText) this.findViewById(R.id.txtqrcode_y);


        String[] sList;
        spnQRCodeSize = (Spinner) findViewById(R.id.spnQRCodeSize);
        sList = "1,2,3,4,5,6,7,8,9,10,12,13,14,15,16,18,20,25,32".split(",");
        arrQRCodeSize = new ArrayAdapter<String>(Activity_QRCode.this, android.R.layout.simple_spinner_item, sList);
        arrQRCodeSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnQRCodeSize.setAdapter(arrQRCodeSize);
        spnQRCodeSize.setOnItemSelectedListener(new OnItemSelectedQRCodeSize());

        spnQRCodeModel = (Spinner) findViewById(R.id.spnQRCodeModel);
        sList = "1,2".split(",");
        arrQRCodeModel = new ArrayAdapter<String>(Activity_QRCode.this, android.R.layout.simple_spinner_item, sList);
        arrQRCodeModel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnQRCodeModel.setAdapter(arrQRCodeModel);
        spnQRCodeModel.setOnItemSelectedListener(new OnItemSelectedQRCodeModel());

//		spnQRCodeLevel = (Spinner) findViewById(R.id.spnQRCodeLevel);	
//		sList="L,M,Q,H".split(",");		
//		arrQRCodeLevel = new ArrayAdapter<String>(Activity_QRCode.this,android.R.layout.simple_spinner_item, sList);
//		arrQRCodeLevel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
//		spnQRCodeLevel.setAdapter(arrQRCodeLevel);
//		spnQRCodeLevel.setOnItemSelectedListener(new OnItemSelectedQRCodeLevel());

        spnqrcode_rotation = (Spinner) findViewById(R.id.spnqrcode_rotation);
        arrQRCoderotation = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        arrQRCoderotation = ArrayAdapter.createFromResource(this, R.array.activity_1dbarcodes_rotation, android.R.layout.simple_spinner_item);
        arrQRCoderotation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnqrcode_rotation.setAdapter(arrQRCoderotation);
        spnqrcode_rotation.setOnItemSelectedListener(new OnItemSelectedqrcoderotation());
    }

    private class OnItemSelectedqrcoderotation implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            switch (arg2) {
                case 0:
                    qrcoderotation = PrinterHelper.BARCODE;
                    txtqrcode_y.setText("0");
                    break;
                case 1:
                    qrcoderotation = PrinterHelper.VBARCODE;
                    txtqrcode_y.setText("500");
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }

    private class OnItemSelectedQRCodeSize implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            QRCodeSize = spnQRCodeSize.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }

    private class OnItemSelectedQRCodeModel implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            QRCodeModel = spnQRCodeModel.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }

    private class OnItemSelectedQRCodeLevel implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            QRCodeLevel = spnQRCodeLevel.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }

    public void onClickPrint(View view) {
        if (!checkClick.isClickEvent()) return;

        try {
            if (txtQRCodeData.getText().toString().trim().length() == 0) {
                Toast.makeText(thisCon, getString(R.string.activity_qrcode_no_data), Toast.LENGTH_SHORT).show();
                return;
            }
            PrinterHelper.printAreaSize("0", "200", "200", "500", "1");
            PrinterHelper.PrintQR(qrcoderotation, txtqrcode_x.getText().toString(), txtqrcode_y.getText().toString(), QRCodeModel, QRCodeSize, txtQRCodeData.getText().toString());
            if ("1".equals(Activity_Main.paper)) {
                PrinterHelper.Form();
            }
            PrinterHelper.Print();

        } catch (Exception e) {
            Log.d("SDKSample", (new StringBuilder("Activity_QRCode --> onClickPrint ")).append(e.getMessage()).toString());
        }
    }
}
