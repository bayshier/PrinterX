package com.printer.tscdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.gprinter.command.LabelCommand;
import com.gprinter.utils.GpUtils;
import com.gprinter.utils.PDFUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import java.util.Vector;

/**
 * Copyright (C), 2012-2020, 珠海佳博科技股份有限公司
 * FileName: PrintConntent
 * Author: Circle
 * Date: 2020/7/20 10:04
 * Description: 打印内容
 */
public class PrintContent {
    /**
     * 标签打印测试页
     *
     * @return
     */
    public static Vector<Byte> getLabel(Context context,int gap) {
        LabelCommand tsc = new LabelCommand();
        // 设置标签尺寸宽高，按照实际尺寸设置 单位mm
        tsc.addUserCommand("\r\n");
        tsc.addSize(76, 185);
        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0 单位mm
        tsc.addGap(gap);
        //设置纸张类型为黑标，发送BLINE 指令不能同时发送GAP指令
//        tsc.addBline(2);
        // 设置打印方向
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);
        // 设置原点坐标
        tsc.addReference(0, 0);
        //设置浓度
        tsc.addDensity(LabelCommand.DENSITY.DNESITY4);
        // 撕纸模式开启
        tsc.addTear(LabelCommand.RESPONSE_MODE.ON);
        // 清除打印缓冲区
        tsc.addCls();
//        // 绘制简体中文
        tsc.addText(10, 10, LabelCommand.FONTTYPE.SIMPLIFIED_24_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "ID: 订单编号：2548422450296825");
        tsc.addText(10, 50, LabelCommand.FONTTYPE.SIMPLIFIED_24_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "地址：，15625284256");
        tsc.addText(10, 90, LabelCommand.FONTTYPE.SIMPLIFIED_24_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "广东省深圳市南山区创智云城2期2b栋7楼 ");
        tsc.addText(10, 130, LabelCommand.FONTTYPE.SIMPLIFIED_24_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "物品：键盘一把无键帽  问题描述：失灵，输入异常");
//        //打印繁体
//        tsc.addUnicodeText(30,50, LabelCommand.FONTTYPE.TRADITIONAL_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"BIG5碼繁體中文","BIG5");
//        //打印韩文
//        tsc.addUnicodeText(30,80, LabelCommand.FONTTYPE.KOREAN, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"Korean 지아보 하성","EUC_KR");
//        //英数字
//        tsc.addText(240,20, LabelCommand.FONTTYPE.FONT_1, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"1");
//        tsc.addText(250,20, LabelCommand.FONTTYPE.FONT_2, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"2");
//        tsc.addText(270,20, LabelCommand.FONTTYPE.FONT_3, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"3");
//        tsc.addText(300,20, LabelCommand.FONTTYPE.FONT_4, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"4");
//        tsc.addText(330,20, LabelCommand.FONTTYPE.FONT_5, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"5");
//        tsc.addText(240,40, LabelCommand.FONTTYPE.FONT_6, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"6");
//        tsc.addText(250,40, LabelCommand.FONTTYPE.FONT_7, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"7");
//        tsc.addText(270,40, LabelCommand.FONTTYPE.FONT_8, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"8");
//        tsc.addText(300,60, LabelCommand.FONTTYPE.FONT_9, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"9");
//        tsc.addText(330,80, LabelCommand.FONTTYPE.FONT_10, LabelCommand.ROTATION.ROTATION_0,LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,"10");
//        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_priter);
        // 绘制图片
//        tsc.drawImage(30, 100, 300, b);

//        Bitmap b2= BitmapFactory.decodeResource(context.getResources(), R.drawable.flower);
//        tsc.drawJPGImage(200,250,200,b2);

        Bitmap b2= BitmapFactory.decodeResource(context.getResources(), R.mipmap.img_1);
        tsc.drawJPGImage(10,160,600,b2);

//        //绘制二维码
//        tsc.addQRCode(30,250, LabelCommand.EEC.LEVEL_L, 5, LabelCommand.ROTATION.ROTATION_0, " www.smarnet.cc");
//        // 绘制一维条码
//        tsc.add1DBarcode(30, 380, LabelCommand.BARCODETYPE.CODE128, 80, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, "12345678");
        // 打印标签
        tsc.addPrint(1, 1);
        // 打印标签后 蜂鸣器响
        tsc.addSound(2, 100);
        //开启钱箱
        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);
        Vector<Byte> datas = tsc.getCommand();
        // 发送数据
        return datas;
    }


    /**
     * 获取图片
     * @param context
     * @return
     */
    public static Bitmap getBitmap(Context context) {
        View v = View.inflate(context, R.layout.page, null);
        TableLayout tableLayout = (TableLayout) v.findViewById(R.id.line);
        TextView total = (TextView) v.findViewById(R.id.total);
        TextView cashier = (TextView) v.findViewById(R.id.cashier);
        tableLayout.addView(ctv(context, "红茶\n加热\n加糖", 3, 8));
        tableLayout.addView(ctv(context, "绿茶", 899, 109));
        tableLayout.addView(ctv(context, "咖啡", 4, 15));
        tableLayout.addView(ctv(context, "红茶", 3, 8));
        tableLayout.addView(ctv(context, "绿茶", 8, 10));
        tableLayout.addView(ctv(context, "咖啡", 4, 15));
        total.setText("998");
        cashier.setText("张三");
        final Bitmap bitmap = convertViewToBitmap(v);
        return bitmap;
    }
    /**
     * mxl转bitmap图片
     * @return
     */
    public static Bitmap convertViewToBitmap(View view){
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    public static TableRow ctv(Context context, String name, int k, int n){
        TableRow tb=new TableRow(context);
        tb.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT ,TableLayout.LayoutParams.WRAP_CONTENT));
        TextView tv1=new TextView(context);
        tv1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT ,TableRow.LayoutParams.WRAP_CONTENT));
        tv1.setText(name);
        tv1.setTextColor(Color.BLACK);
        tb.addView(tv1);
        TextView tv2=new TextView(context);
        tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT ,TableRow.LayoutParams.WRAP_CONTENT));
        tv2.setText(k+"");
        tv2.setTextColor(Color.BLACK);
        tb.addView(tv2);
        TextView tv3=new TextView(context);
        tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT ,TableRow.LayoutParams.WRAP_CONTENT));
        tv3.setText(n+"");
        tv3.setTextColor(Color.BLACK);
        tb.addView(tv3);
        return tb;
    }
    /**
     * 获取Assets文件
     * @param fileName
     * @return
     */
    public static String getFromAssets(Context context,String fileName) {
        String result = "";
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null)
                result += line+"\r\n";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public static Vector<Byte> getXmlBitmap(Context context){
        LabelCommand tsc = new LabelCommand();
        // 设置标签尺寸宽高，按照实际尺寸设置 单位mm
        tsc.addUserCommand("\r\n");
        tsc.addSize(58, 100);
        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0 单位mm
        tsc.addGap(0);
        // 设置打印方向
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);
        // 设置原点坐标
        tsc.addReference(0, 0);
        //设置浓度
        tsc.addDensity(LabelCommand.DENSITY.DNESITY4);
        // 撕纸模式开启
        tsc.addTear(LabelCommand.RESPONSE_MODE.ON);
        // 清除打印缓冲区
        tsc.addCls();
        Bitmap bitmap=getBitmap(context);
        // 绘制图片
        /**
         * x:打印起始横坐标
         * y:打印起始纵坐标
         * mWidth：打印宽度以dot为单位
         * nbitmap：源图
         */
        tsc.drawXmlImage(10,10,bitmap.getWidth(),bitmap);
        // 打印标签
        tsc.addPrint(1, 1);
        return tsc.getCommand();
    }
}
