package com.sdk.cpcl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;


/**
 * Created by NO on 2017/5/9.
 */

public class AlertDialogUtil {
    Context mContext;
    private View view;
    private AlertDialog.Builder builder;
    private View popView;
    private PopupWindow popWindow;

    public AlertDialogUtil(Context context) {
        this.mContext = context;
    }

    public AlertDialog.Builder setAlertDialog() {
        // R.style.MyAlertDialog
        builder = new AlertDialog.Builder(mContext);
        return builder;
    }

    public View setViewID(int id) {
        this.view = LayoutInflater.from(mContext).inflate(id, null);
        builder.setView(view);
        return view;
    }

    public void setView(View view) {
        this.view = view;
        builder.setView(view);
    }

    public void settext(int id, String item) {
        TextView textview = (TextView) view.findViewById(id);
        textview.setText(item);
    }

    public String gettext(int id) {
        TextView textview = (TextView) view.findViewById(id);
        return textview.getText().toString();
    }

    public void setBitmap(int id, Bitmap bit) {
        ImageView imageview = (ImageView) view.findViewById(id);
        imageview.setImageBitmap(bit);
    }

    public View getView(int id) {
        return view.findViewById(id);
    }


    public interface DialogCallBack {
        void onCallBack(Object object);
    }

    //    //底部弹窗
//    public void setPopupWindow(int layoutID){
//        popView = View.inflate(mContext, layoutID, null);
////        int width = mContext.getResources().getDisplayMetrics().widthPixels;
////        int height = mContext.getResources().getDisplayMetrics().heightPixels;
//
//        popWindow = new PopupWindow(popView,WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
//        popWindow.setAnimationStyle(R.style.Popupwindow);
//        ColorDrawable dw = new ColorDrawable(0x30000000);
//        popWindow.setBackgroundDrawable(dw);
//        popWindow.setOutsideTouchable(true);// 设置同意在外点击消失
//        popWindow.setTouchable(true);
//        popWindow.setFocusable(true);
//    }
    public View PopupWindowfindView(int id) {
        return popView.findViewById(id);
    }

    public void showPopupWindow(final View view) {
        if (popWindow == null || popWindow.isShowing()) {
            return;
        }
        view.post(new Runnable() {
            @Override
            public void run() {
                popWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });

    }

    public void disPopupWindow() {
        if (popWindow == null || !popWindow.isShowing()) {
            return;
        }
        popWindow.dismiss();
    }

    //输入对话框
    public static void edTextDialog(Context context, String title, String message,String str, final setEdTextListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        View view = LayoutInflater.from(context).inflate(R.layout.item_edtext, null);
        final EditText edDialog = (EditText)view.findViewById(R.id.ed_dialog);
        edDialog.setText(str);
        builder.setView(view);
        builder.setPositiveButton(context.getString(R.string.picture_cancel),null);
        builder.setNegativeButton(context.getString(R.string.activity_1dbarcodes_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onResult(edDialog.getText().toString());
            }
        });
        builder.show();
    }
    public interface setEdTextListener {
        void onResult(String data);
    }

    //确认对话框
    public static void sureDialog(Context context, String title, String message, boolean isNo, final setSureListener setSureListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (setSureListener != null) {
                    setSureListener.onSure();
                }
            }
        });
        if (isNo)
            builder.setNegativeButton("取消", null);
        if (!((Activity) context).isFinishing())
            builder.show();
    }

    //自定义确认对话框
    public static void sureDialog(Context context, String title, String message, boolean isNo, String sureStr, final setSureListener setSureListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(sureStr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (setSureListener != null) {
                    setSureListener.onSure();
                }
            }
        });
        if (isNo)
            builder.setNegativeButton("取消", null);
        builder.show();
    }

    public interface setSureListener {
        void onSure();
    }

    //进度对话框
    public static ProgressDialog progressDialog(Context context, String title, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
        return progressDialog;
    }

    //底部列表弹窗
//    public void BottemPopupWindow(String title, List<String> content,View view, final SetOnLister setOnLister){
//        setPopupWindow(R.layout.item_recyview);
//        TextView tvTitle = (TextView) PopupWindowfindView(R.id.tv_title);
//        tvTitle.setText(title);
//        RecyclerView recyContent = (RecyclerView) PopupWindowfindView(R.id.recy_content);
//        BaseQuickAdapter<String, BaseViewHolder> baseQuickAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_one_text, content) {
//
//            @Override
//            protected void convert(BaseViewHolder helper, String item) {
//                helper.setText(R.id.tv_content, item);
//            }
//        };
//        recyContent.setLayoutManager(new LinearLayoutManager(mContext));
//        recyContent.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
//        recyContent.setAdapter(baseQuickAdapter);
//        baseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                setOnLister.Lister(position);
//                disPopupWindow();
//            }
//        });
//        PopupWindowfindView(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                disPopupWindow();
//            }
//        });
//        showPopupWindow(view);
//    }
    public interface SetOnLister {
        void Lister(int position);
    }

    //    //单个滑轮底部框
//    public  void oneWheelPopupWindow(String title, String[] entries, View view, final setSureLister setsurelister){
//        final AlertDialogUtil alertDialogUtil = new AlertDialogUtil(mContext);
//        alertDialogUtil.setPopupWindow(R.layout.item_one_wheelview);
//        final Wheel3DView wheel = (Wheel3DView) alertDialogUtil.PopupWindowfindView(R.id.wheel);
//        ((Button) alertDialogUtil.PopupWindowfindView(R.id.btn_cancel)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialogUtil.disPopupWindow();
//            }
//        });
//        ((Button) alertDialogUtil.PopupWindowfindView(R.id.btn_define)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String data=wheel.getCurrentItem().toString();
//                setsurelister.Lister(data,wheel.getCurrentIndex());
//                alertDialogUtil.disPopupWindow();
//            }
//        });
//        ((TextView) alertDialogUtil.PopupWindowfindView(R.id.tv_title)).setText(title);
//        wheel.setEntries(entries);
//        wheel.setSelectedColor(mContext.getResources().getColor(R.color.black));
//        wheel.setUnselectedColor(mContext.getResources().getColor(R.color.color_gray));
//        alertDialogUtil.showPopupWindow(view);
//    }
    public interface setSureLister {
        void Lister(String data, int position);
    }
//    //参数设置成功
//    public  void setParameterSettingSucceededDialog(){
//        Print.disConnection();
//        AlertDialogUtil.sureDialog(mContext, mContext.getString(R.string.activity_image_dialog_title), mContext.getString(R.string.setting_dialog_message), true, new AlertDialogUtil.setSureListener() {
//            @Override
//            public void onSure() {
//                ActivityUtilitys.startActivityPrintStatus(mContext);
//                ((Activity)mContext).finish();
//            }
//        });
//    }
    //控制是否消失的对话框
//    public static void edTextAppearDialog(Context context, String title, String message,String str, final setEdTextAppearListener listener){
//        View view = LayoutInflater.from(context).inflate(R.layout.item_edtext, null);
//        final EditText edDialog = (EditText)view.findViewById(R.id.ed_dialog);
//        edDialog.setText(str);
//        edDialog.setSelection(str.length());
//        final AlertDialog builder = new AlertDialog.Builder(context)
//                .setView(view)
//                .setTitle(title)
//                .setMessage(message)
//                .setNegativeButton(R.string.item_popupwindow_cancel,null)
//                .setPositiveButton(R.string.activity_barcode_sure,null)
//                .create();
//        builder.show();
//        builder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.onResult(builder,edDialog.getText().toString());
//            }
//        });
//    }
//    public interface setEdTextAppearListener{
//        void onResult(AlertDialog dialog, String data);
//    }
}
