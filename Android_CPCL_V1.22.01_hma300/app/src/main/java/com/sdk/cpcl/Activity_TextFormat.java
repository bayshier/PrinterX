package com.sdk.cpcl;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import cpcl.PrinterHelper;
import cpcl.PublicFunction;

import static cpcl.PrinterHelper.LanguageEncode;
import static cpcl.PrinterHelper.Print;


public class Activity_TextFormat extends Activity {
    private Context thisCon = null;
    private PublicFunction PFun = null;
    private EditText txtText = null;
    private EditText txtformat_x = null;
    private EditText txtformat_y = null;
    private Spinner spnformat_font = null;
    private Spinner spnformat_rotation = null;
    private ArrayAdapter arrformat_font;
    private ArrayAdapter arrformatrotation;
    private Spinner spnformat_x_multiplication = null;
    private ArrayAdapter arrformat_x_multiplication;
    private Spinner spnformat_y_multiplication = null;
    private ArrayAdapter arrformat_y_multiplication;
    private int formatfont = 16;
    private String x_multiplication = "0";
    private String y_multiplication = "0";
    private String qrcoderotation = "";
    private CheckBox cb_textformat_bold;
    private CheckBox cb_textformat_inverse;
    private CheckBox cb_textformat_doublewidth;
    private CheckBox cb_textformat_doubleheight;
    private int textType = 1;
    private EditText ed_textformat_papeheight;
    private Spinner spnformat_codepage;
    private ArrayAdapter<String> adapter_codepage;
    boolean isCodepage = false;
    String codepage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_text_format);
        thisCon = this.getApplicationContext();

        txtText = (EditText) findViewById(R.id.txtText);
        txtformat_x = (EditText) findViewById(R.id.txtformat_x);
        txtformat_y = (EditText) findViewById(R.id.txtformat_y);

        spnformat_codepage = (Spinner) findViewById(R.id.spnformat_codepage);
        adapter_codepage = new ArrayAdapter<>(Activity_TextFormat.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.codepage));
        spnformat_codepage.setAdapter(adapter_codepage);
        spnformat_codepage.setOnItemSelectedListener(new OnItemSelectedformat_codepage());

        spnformat_font = (Spinner) findViewById(R.id.spnformat_font);
//		String[] sList = "16,24,32,繁体(12*24)".split(",");


        spnformat_rotation = (Spinner) findViewById(R.id.spnformat_rotation);
        arrformatrotation = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        arrformatrotation = ArrayAdapter.createFromResource(this, R.array.activity_1dbarcodes_hri_rotation, android.R.layout.simple_spinner_item);
        arrformatrotation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnformat_rotation.setAdapter(arrformatrotation);
        spnformat_rotation.setOnItemSelectedListener(new OnItemSelectedformatrotation());
        cb_textformat_bold = (CheckBox) findViewById(R.id.cb_textformat_bold);
        cb_textformat_inverse = (CheckBox) findViewById(R.id.cb_textformat_inverse);
        cb_textformat_doublewidth = (CheckBox) findViewById(R.id.cb_textformat_doublewidth);
        cb_textformat_doubleheight = (CheckBox) findViewById(R.id.cb_textformat_doubleheight);
    }

    private class OnItemSelectedformatrotation implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            switch (arg2) {
                case 0:
                    qrcoderotation = PrinterHelper.TEXT;
                    txtformat_x.setText("0");
                    break;
                case 1:
                    qrcoderotation = PrinterHelper.TEXT270;
                    txtformat_x.setText("100");
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

    private class OnItemSelectedformatfont implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (isCodepage) {
                switch (arg2) {
                    case 0:
                        formatfont = 16;
                        break;
                    case 1:
                        formatfont = 24;
                        break;
                    case 2:
                        formatfont = 48;
                        break;
                    case 3:
                        formatfont = 1;
                        break;

                    default:
                        break;
                }
            } else {
                formatfont = arg2;
            }


        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }

    private class OnItemSelectedformat_codepage implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            String[] sList = null;
            if (arg2 == 0) {
                isCodepage = true;
                sList = "16,24".split(",");
            } else {
                isCodepage = false;
                if ("Khemr".equals(adapter_codepage.getItem(arg2).toString())) {
                    sList = new String[]{"12*24"};
                } else {
                    sList = "12*24,9*17".split(",");
                }
            }
            codepage = adapter_codepage.getItem(arg2).toString();
            arrformat_font = new ArrayAdapter<String>(Activity_TextFormat.this, android.R.layout.simple_spinner_item, sList);
            arrformat_font.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnformat_font.setAdapter(arrformat_font);
            spnformat_font.setOnItemSelectedListener(new OnItemSelectedformatfont());

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }

    private class OnItemSelectedformat_y_multiplication implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            y_multiplication = spnformat_y_multiplication.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }

    public void onClickPrint(View view) {
        if (!checkClick.isClickEvent()) return;

        try {
            PublicAction publicFunction = new PublicAction();
            String sText = txtText.getText().toString();
            sText = sText.replace("\n", "");
//			sText="កខគឃងចឆជឈញដឋឌណតថទធនបផពភមយរលវឝឞសហឡអ" +
//					"ហ្គាសហ្នាំង  ហ្ន័ងប៉ាក់ប៉័ងគ្រូហ្មហ្លួងហ្វង់កាហ្វេហ្វ៊ីលហ្សាសហ្ស៊ីបនោះ " +
//					"ឥឦឧឨឩឪឫឬឭឮឯឰឱ, ឲឳ០១២៣៤៥៦៧៨៩" +
//					"។ៗ៕៚៙៖";
            if (TextUtils.isEmpty(sText)) {
                Toast.makeText(thisCon, getString(R.string.activity_1dbarcodes_no_data), Toast.LENGTH_SHORT).show();
                return;
            }
            String languageEncode = publicFunction.getLanguageEncode(codepage);
            textType = 0;
            if (cb_textformat_bold.isChecked()) {
                textType = textType | 1;
            }
            if (cb_textformat_inverse.isChecked()) {
                textType = textType | 2;
            }
            if (cb_textformat_doublewidth.isChecked()) {
                textType = textType | 4;
            }
            if (cb_textformat_doubleheight.isChecked()) {
                textType = textType | 8;
            }
            PrinterHelper.printAreaSize("0", "200", "200", "" + 200, "1");
            if (isCodepage) {
//				LanguageEncode = "big5";
                LanguageEncode = "gb2312";
                PrinterHelper.Country("CHINA");
            } else {
                LanguageEncode = languageEncode;
                PrinterHelper.Country(codepage);
            }
//			LanguageEncode = "big5";
//			PrinterHelper.printText("國\r\n");
            if (qrcoderotation.equals(PrinterHelper.TEXT)) {
                if (!isCodepage) {
                    PrinterHelper.PrintCodepageTextCPCL(qrcoderotation, formatfont, txtformat_x.getText().toString(), "" + (Integer.valueOf(txtformat_y.getText().toString())), sText, textType);
                } else {
                    PrinterHelper.PrintTextCPCL(qrcoderotation, formatfont, txtformat_x.getText().toString(), "" + (Integer.valueOf(txtformat_y.getText().toString())), sText, textType, false, 100);
                }
            } else {
                if (!isCodepage) {
                    PrinterHelper.PrintCodepageTextCPCL(qrcoderotation, formatfont, "" + (Integer.valueOf(txtformat_x.getText().toString())), txtformat_y.getText().toString(), sText, textType);
                } else {
                    PrinterHelper.PrintTextCPCL(qrcoderotation, formatfont, "" + (Integer.valueOf(txtformat_x.getText().toString())), txtformat_y.getText().toString(), sText, textType, false, 100);
                }
            }
            if ("Khemr".equals(codepage)) {
                //关闭高棉语
                PrinterHelper.setKhemrEnd();
                //codepage切回中文
                LanguageEncode = "gb2312";
            }
            if ("1".equals(Activity_Main.paper)) {
                PrinterHelper.Form();
            }

            PrinterHelper.Print();
        } catch (Exception e) {
            Log.d("Print", (new StringBuilder("Activity_TextFormat --> onClickPrint ")).append(e.getMessage()).toString());
        }
    }
}
