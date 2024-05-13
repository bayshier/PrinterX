package com.sdk.cpcl;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cpcl.PrinterHelper;

import static cpcl.PrinterHelper.LanguageEncode;


public class AutoTextActivity extends Activity {

    @BindView(R.id.et_message)
    EditText etMessage;
    @BindView(R.id.spin_codepage)
    Spinner spinCodepage;
    @BindView(R.id.ed_x)
    EditText edX;
    @BindView(R.id.ed_y)
    EditText edY;
    @BindView(R.id.spin_font)
    Spinner spinFont;
    @BindView(R.id.cb_textformat_bold)
    CheckBox cbTextformatBold;
    @BindView(R.id.cb_setmag)
    CheckBox cbSetmag;
    @BindView(R.id.btn_print)
    Button btnPrint;
    @BindView(R.id.ed_page_height)
    EditText edPageHeight;
    @BindView(R.id.ed_page_width)
    EditText edPageWidth;
    private Context context;
    private ArrayAdapter<String> adapter_codepage;
    boolean isCodepage = false;
    String codepage;
    private ArrayAdapter<String> arrformat_font;
    private int formatfont = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_text);
        ButterKnife.bind(this);
        context = this;
        initView();
    }

    private void initView() {
        adapter_codepage = new ArrayAdapter<>(AutoTextActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.codepage));
        spinCodepage.setAdapter(adapter_codepage);
        spinCodepage.setOnItemSelectedListener(new OnItemSelectedformat_codepage());

    }

    @OnClick(R.id.btn_print)
    public void onViewClicked() {
        try {
            PublicAction publicFunction = new PublicAction();
            String sText = etMessage.getText().toString();
            sText = sText.replace("\n", "");
//            sText="កខគឃងចឆជឈញដឋឌណតថទធនបផពភមយរលវឝឞសហឡអ\n" +
//					"ហ្គាសហ្នាំង  ហ្ន័ងប៉ាក់ប៉័ងគ្រូហ្មហ្លួងហ្វង់កាហ្វេហ្វ៊ីលហ្សាសហ្ស៊ីបនោះ \n" +
//					"ឥឦឧឨឩឪឫឬឭឮឯឰឱ, ឲឳ០១២៣៤៥៦៧៨៩\n" +
//					"។ៗ៕៚៙៖\n";
            if (TextUtils.isEmpty(sText)) {
                Toast.makeText(context, getString(R.string.activity_1dbarcodes_no_data), Toast.LENGTH_SHORT).show();
                return;
            }
            String languageEncode = publicFunction.getLanguageEncode(codepage);
            PrinterHelper.printAreaSize("0", "200", "200", edPageHeight.getText().toString(), "1");
            if (isCodepage) {
                LanguageEncode = "gb2312";
            } else {
                LanguageEncode = languageEncode;
                PrinterHelper.Country(codepage);
            }
            PrinterHelper.AutLine2(edX.getText().toString(), edY.getText().toString(), edPageWidth.getText().toString(), formatfont, cbTextformatBold.isChecked(), cbSetmag.isChecked(), sText);
            if ("Khemr".equals(codepage)){
                PrinterHelper.setKhemrEnd();
                LanguageEncode="gb2312";
            }
            if ("1".equals(Activity_Main.paper)) {
                PrinterHelper.Form();
            }
            PrinterHelper.Print();
        } catch (Exception e) {

        }
    }

    private class OnItemSelectedformat_codepage implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            String[] sList = null;
            if (arg2 == 0) {
                isCodepage = true;
                sList = "16,24,32,繁体(12*24)".split(",");
            } else {
                isCodepage = false;
                if ("Khemr".equals(adapter_codepage.getItem(arg2).toString())){
                    sList = new String[]{"12*24"};
                }else{
                    sList = "12*24,9*17".split(",");
                }
            }
            codepage = adapter_codepage.getItem(arg2).toString();
            arrformat_font = new ArrayAdapter<String>(AutoTextActivity.this, android.R.layout.simple_spinner_item, sList);
            arrformat_font.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinFont.setAdapter(arrformat_font);
            spinFont.setOnItemSelectedListener(new OnItemSelectedformatfont());

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }

    private class OnItemSelectedformatfont implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (isCodepage) {
                switch (arg2) {
                    case 0:
                        formatfont = 55;
                        break;
                    case 1:
                        formatfont = 8;
                        break;
                    case 2:
                        formatfont = 4;
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
}
