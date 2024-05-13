package com.sdk.cpcl;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import cpcl.PrinterHelper;


public class Activity_Status extends Activity {
    private Context thisCon;
    private TextView txtStatus = null;
    private int iStatusMode = 1;
    private String sStatus = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_status);
        thisCon = this.getApplicationContext();

        txtStatus = (TextView) this.findViewById(R.id.txtStatus);
        Refresh();
    }

    public void onClickRefresh(View view) {
        if (!checkClick.isClickEvent()) return;
        try {
            Refresh();
        } catch (Exception e) {
        }
    }

    private void Refresh() {
        txtStatus.setText("");
        try {
            int status = PrinterHelper.getPrinterStatus();
            if (status == 0){
                txtStatus.setText(getString(R.string.activity_Statues_ready));
            }
            if((status & 2) == 2){
                txtStatus.setText(getString(R.string.activity_Statues_nopage));
            }
            if ((status & 4) == 4){
                txtStatus.setText(getString(R.string.activity_Statues_open));
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("TAG", "Activity_Status->REfresh" + e.getMessage().toString());
        }
    }
}
