package com.sdk.cpcl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BTActivity extends Activity {

    @BindView(R.id.recy_history)
    RecyclerView recyHistory;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.activity_bt)
    RelativeLayout activityBt;
    private Context mContext;
    private ListView list_bt;
    public BluetoothAdapter myBluetoothAdapter;
    private Intent intent;
    private BaseQuickAdapter<BluetoothDevice, BaseViewHolder> baseQuickAdapter;
    private List<BluetoothDevice> list;
    private int tag;
    private Bluetooth bluetooth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        initData();
    }

    private void initData() {
        intent = getIntent();
        tag = intent.getIntExtra("TAG", RESULT_CANCELED);
        ListBluetoothDevice();
    }
    @SuppressLint("MissingPermission")
    public void ListBluetoothDevice() {
        if ((myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()) == null) {
            Toast.makeText(this, "没有找到蓝牙适配器", Toast.LENGTH_LONG).show();
            return;
        }

        if (!myBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 2);
        }
        list = new ArrayList<BluetoothDevice>();
        baseQuickAdapter = new BaseQuickAdapter<BluetoothDevice, BaseViewHolder>(android.R.layout.simple_list_item_2, list) {

            @Override
            protected void convert(BaseViewHolder helper, BluetoothDevice item) {
                if (item!=null){
                    if (item.getName()!=null)
                        helper.setText(android.R.id.text1, item.getName().isEmpty()?"Null":item.getName());
                    helper.setText(android.R.id.text2, item.getAddress());
                }
            }
        };
        recyHistory.setLayoutManager(new LinearLayoutManager(mContext));
        recyHistory.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyHistory.setAdapter(baseQuickAdapter);
        bluetooth = Bluetooth.getBluetooth(this);
        initBT();
        baseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bluetooth.setOnBondState(list.get(position), new Bluetooth.OnBondState() {
                    @Override
                    public void bondSuccess() {
                        if (progressDialog!=null&&progressDialog.isShowing())
                            progressDialog.dismiss();
                        Intent intent = new Intent();
                        intent.putExtra("SelectedBDAddress", list.get(position).getAddress());
                        setResult(tag, intent);
                        finish();
                    }
                });
                if (list.get(position).getBondState()==BluetoothDevice.BOND_BONDED){
                    Intent intent = new Intent();
                    intent.putExtra("SelectedBDAddress", list.get(position).getAddress());
                    setResult(tag, intent);
                    finish();
                }else{
//                    Method method = null;
//                    try {
//                        method = BluetoothDevice.class.getMethod("createBond");
//                        Log.d("Print", "开始配对");
//                        method.invoke(list.get(position));
//                    } catch (Exception e) {
//                    }
                    progressDialog = new ProgressDialog(BTActivity.this);
                    progressDialog.setMessage(getString(R.string.activity_devicelist_connect));
                    progressDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            list.get(position).createBond();
                        }
                    }).start();
                }
            }
        });
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initBT();
                if (swipeRefresh.isRefreshing())
                    swipeRefresh.setRefreshing(false);
            }
        });
    }
    private void initBT() {
        Log.d("TAG","initBT:");
        list.clear();
        baseQuickAdapter.notifyDataSetChanged();
        bluetooth.doDiscovery();
        bluetooth.getData(new Bluetooth.toData() {
            @Override
            public void succeed(BluetoothDevice bluetoothDevice) {
                for (BluetoothDevice printBT : list) {
                    if (bluetoothDevice.getAddress().equals(printBT.getAddress())) {
                        return;
                    }
                }
                //XiangYinBao_X3,ATOL1
                list.add(bluetoothDevice);
                baseQuickAdapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetooth!=null)
            bluetooth.disReceiver();
    }
}
