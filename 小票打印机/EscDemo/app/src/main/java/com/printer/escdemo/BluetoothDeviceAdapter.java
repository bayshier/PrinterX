package com.printer.escdemo;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.printer.escdemo.bean.BluetoothParameter;

import java.util.List;


/**
 * 作者： Circle
 * 创造于 2018/5/24.
 */
public class BluetoothDeviceAdapter extends BaseAdapter {

    private List<BluetoothParameter> pairedDevices;
    private List<BluetoothParameter> newDevices;
    private Context mContext;
    private static final int TITLE = 0;
    private static final int CONTENT = 1;
    public BluetoothDeviceAdapter(List<BluetoothParameter> pairedDevices, List<BluetoothParameter> newDevices, Context context) {
        this.pairedDevices = pairedDevices;
        this.newDevices = newDevices;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return pairedDevices.size() + newDevices.size() + 2;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case TITLE:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.text_item, parent, false);
                TextView tv_title = (TextView) convertView.findViewById(R.id.text);
                tv_title.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                tv_title.setGravity(Gravity.LEFT);
                if (position == 0) {
                    tv_title.setText(mContext.getResources().getString(R.string.paired));
                } else {
                    tv_title.setText(mContext.getResources().getString(R.string.unpaired));
                }
                break;
            case CONTENT:
                BluetoothParameter bluetoothParameter = null;
                if (position < pairedDevices.size() + 1) {
                    bluetoothParameter = pairedDevices.get(position - 1);
                }
                if (position > pairedDevices.size()+1 && newDevices.size() > 0) {
                    bluetoothParameter = newDevices.get(position - pairedDevices.size() - 2);
                }

                if (bluetoothParameter!=null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.bluetooth_list_item, parent, false);
                    TextView tvName = (TextView) convertView.findViewById(R.id.b_name);
                    TextView tvMac = (TextView) convertView.findViewById(R.id.b_mac);
                    TextView tvStrength = (TextView) convertView.findViewById(R.id.b_info);
                    tvName.setText(bluetoothParameter.getBluetoothName());
                    tvMac.setText(bluetoothParameter.getBluetoothMac());
                    tvStrength.setText(bluetoothParameter.getBluetoothStrength());
                }
                break;
        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if ((position == pairedDevices.size()+1) || (position == 0)) {
            return TITLE;
        } else {
            return CONTENT;
        }
    }
}
