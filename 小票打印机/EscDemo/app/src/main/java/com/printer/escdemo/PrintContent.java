package com.printer.escdemo;

import static com.gprinter.bean.BarCodeType.BARCODE_CODE128;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gprinter.command.EscCommand;
import com.gprinter.command.EscForDotCommand;
import com.gprinter.command.LabelCommand;
import com.gprinter.utils.Menu58Utils;
import com.gprinter.utils.Menu80Utils;

import java.util.Vector;

/**
 * Copyright (C), 2012-2020, 打印机有限公司
 * FileName: PrintConntent
 * Author: Circle
 * Date: 2020/7/20 10:04
 * Description: 打印内容
 */
public class PrintContent {
    /**
     * 小票案例
     * @param context
     * @return
     */
    public static Vector<Byte> getReceiptChinese(Context context,int width) {
        EscCommand esc = new EscCommand();
        //初始化打印机
        esc.addInitializePrinter();
        // 设置打印居中
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        //字体放大两倍
        esc.addSetCharcterSize(EscCommand.WIDTH_ZOOM.MUL_2, EscCommand.HEIGHT_ZOOM.MUL_2);
        // 打印文字
        esc.addText("票据测试\n");

        //字体正常
        esc.addSetCharcterSize(EscCommand.WIDTH_ZOOM.MUL_1, EscCommand.HEIGHT_ZOOM.MUL_1);
        //打印并换行
        esc.addPrintAndLineFeed();
        // 设置打印左对齐
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        // 打印文字
        esc.addText("打印文字测试:\n");

//        esc.addSetKanjiUnderLine(EscCommand.UNDERLINE_MODE.UNDERLINE_2DOT);//汉字下划线
        esc.addTurnUnderlineModeOnOrOff(EscCommand.UNDERLINE_MODE.UNDERLINE_2DOT);//非汉字下划线
        esc.addText("125545AA测试下划线\n");
        esc.addTurnUnderlineModeOnOrOff(EscCommand.UNDERLINE_MODE.OFF);//非汉字取消下划线

        // 打印文字
        esc.addText("欢迎使用打印机!\n");
        esc.addPrintAndLineFeed();
        //对齐方式
        esc.addText("打印对齐方式测试:\n");
        //设置居左
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        esc.addText("居左");
        esc.addPrintAndLineFeed();
        //设置居中
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        esc.addText("居中");
        esc.addPrintAndLineFeed();
        //设置居右
        esc.addSelectJustification(EscCommand.JUSTIFICATION.RIGHT);
        esc.addText("居右");
        esc.addPrintAndLineFeed();
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        // 打印图片
        esc.addText("打印Bitmap图测试:\n");
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_priter);//二维码图片，图片类型bitmap
        // 打印图片,58打印机图片宽度最大为384dot  1mm=8dot 用尺子量取图片的宽度单位为Xmm  传入宽度值为 X*8
        //打印图片,80打印机图片宽度最大为576dot  1mm=8dot 用尺子量取图片的宽度单位为Xmm  传入宽度值为 X*8
        esc.drawImage(b, width);
        esc.addPrintAndLineFeed();
        //打印条码
        esc.addText("打印条码测试:\n");
        // 设置条码可识别字符位置在条码下方
        esc.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.BELOW);
        // 设置条码高度为60点
        esc.addSetBarcodeHeight((byte) 60);
        // 设置条码单元宽度为1
        esc.addSetBarcodeWidth((byte) 1);
        // 打印Code128码内容
        esc.addCODE128(esc.genCodeB("barcode128"));
        esc.addPrintAndLineFeed();
        // 打印二维码
        esc.addText("打印二维码测试:\n");
        // 设置纠错等级
        esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31);
        // 设置qrcode模块大小
        esc.addSelectSizeOfModuleForQRCode((byte) 4);
        // 设置qrcode内容
        esc.addStoreQRCodeData("www.baidu.com");
        // 打印QRCode
        esc.addPrintQRCode();
        esc.addPrintAndLineFeed();

        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        //打印文字
        esc.addSelectCharacterFont(EscCommand.FONT.FONTB);
        esc.addText("测试完成!\r\n");
        // 设置打印居左
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        esc.addPrintAndLineFeed();
        esc.addPrintAndFeedLines((byte) 4);
        //切纸（带切刀打印机才可用）
        esc.addCutPaper();
        // 开钱箱
        esc.addGeneratePlus(LabelCommand.FOOT.F2, (byte) 255, (byte) 255);
        esc.addInitializePrinter();
        //返回指令集
        return esc.getCommand();
    }


    public static Vector<Byte> getImgRes(Context context,int res) {
        EscCommand esc = new EscCommand();
        //初始化打印机
        esc.addInitializePrinter();
        // 设置打印居中
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), res);
        // 打印图片,图片宽度为384dot  1mm=8dot 用尺子量取图片的宽度单位为Xmm  传入宽度值为 X*8
        esc.drawJpgImage(b, 200);
        esc.addText("\n\n\n\n");
        esc.addPrintAndLineFeed();
        //切纸（带切刀打印机才可用）
        esc.addCutPaper();
        // 开钱箱
        esc.addGeneratePlus(LabelCommand.FOOT.F2, (byte) 255, (byte) 255);
        esc.addInitializePrinter();
        return esc.getCommand();
    }


    /**
     * 针式打印指令
     * @param qrCode
     * @param barCode
     * @return
     */
    public static Vector<Byte> getDotPrintCommand(String qrCode,String barCode) {
        EscForDotCommand esc = new EscForDotCommand();
        //初始化打印机
        esc.addInitializePrinter();
        // 设置打印居中
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        esc.addText("二维码打印");
        esc.addText("\n\n");
        esc.addQrcode(100,100,qrCode);
        // 打印图片,图片宽度为384dot  1mm=8dot 用尺子量取图片的宽度单位为Xmm  传入宽度值为 X*8
        esc.addText("\n\n");
        esc.addText("条形码打印");
        esc.addText("\n\n");
        esc.addBarcode(BARCODE_CODE128,200,50,true,12,barCode);
        esc.addText("\n\n\n\n");
        esc.addPrintAndLineFeed();
        //切纸（带切刀打印机才可用）
        esc.addCutPaper();
        // 开钱箱
        esc.addGeneratePlus(LabelCommand.FOOT.F2, (byte) 255, (byte) 255);
        esc.addInitializePrinter();
        return esc.getCommand();
    }


    /**
     * 菜单样例
     * @param context
     * @return
     */
    public static Vector<Byte> get58Menu(Context context) {
        EscCommand esc = new EscCommand();
        //初始化打印机
        esc.addInitializePrinter();
        // 设置打印居中
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.flower);//二维码图片，图片类型bitmap
        // 打印图片,图片宽度为384dot  1mm=8dot 用尺子量取图片的宽度单位为Xmm  传入宽度值为 X*8
        esc.drawJpgImage(b, 200);
        // 设置为倍高倍宽
        //字体放大两倍
        esc.addSetCharcterSize(EscCommand.WIDTH_ZOOM.MUL_2, EscCommand.HEIGHT_ZOOM.MUL_2);
        // 打印文字
        esc.addText("爱情餐厅\n\n");
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        esc.addText("520号桌\n\n");
        //字体正常
        esc.addSetCharcterSize(EscCommand.WIDTH_ZOOM.MUL_1, EscCommand.HEIGHT_ZOOM.MUL_1);
        esc.addText("点菜时间 2020-05-20 5:20\n");
        esc.addText("上菜时间 2020-05-20 13:14\n");
        esc.addText("人数：2人 点菜员：新疆包工头\n");
        esc.addText("--------------------------------\n");
        //开启加粗
        esc.addTurnEmphasizedModeOnOrOff(EscCommand.ENABLE.ON);
        esc.addText(Menu58Utils.printThreeData("菜名", "数量", "金额"));
        esc.addTurnEmphasizedModeOnOrOff(EscCommand.ENABLE.OFF);
        esc.addText(Menu58Utils.printThreeData("北京烤鸭", "1", "99.99"));
        esc.addText(Menu58Utils.printThreeData("四川麻婆豆腐", "1", "39.99"));
        esc.addText(Menu58Utils.printThreeData("西湖醋鱼", "1", "59.99"));
        esc.addText(Menu58Utils.printThreeData("飞龙汤", "1", "66.66"));
        esc.addText(Menu58Utils.printThreeData("无为熏鸭", "1", "88.88"));
        esc.addText(Menu58Utils.printThreeData("东坡肉", "1", "39.99"));
        esc.addText(Menu58Utils.printThreeData("辣子鸡", "1", "66.66"));
        esc.addText(Menu58Utils.printThreeData("腊味合蒸", "1", "108.00"));
        esc.addText(Menu58Utils.printThreeData("东安子鸡", "1", "119.00"));
        esc.addText(Menu58Utils.printThreeData("清蒸武昌鱼", "1", "88.88"));
        esc.addText(Menu58Utils.printThreeData("再来两瓶82年的快乐肥宅水(去冰)", "1", "9.99"));
        esc.addText(Menu58Utils.printThreeData("老干妈拌饭(加辣、加香菜)", "1", "6.66"));
        esc.addText("--------------------------------\n\n");
        esc.addTurnEmphasizedModeOnOrOff(EscCommand.ENABLE.ON);
        esc.addText(Menu58Utils.printTwoData("合计：", "1314.00"));
        esc.addText(Menu58Utils.printTwoData("抹零：", "14.00"));
        esc.addText(Menu58Utils.printTwoData("应收：", "1300.00"));
        esc.addTurnEmphasizedModeOnOrOff(EscCommand.ENABLE.OFF);
        esc.addText("--------------------------------\n");
        esc.addSelectJustification(EscCommand.JUSTIFICATION.RIGHT);
        esc.addText("收银员：广东包租公\n");
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        esc.addText("宣言：我点个鸡蛋都是爱你的形状哦");
        esc.addPrintAndLineFeed();
        esc.addPrintAndLineFeed();
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        // 设置纠错等级
        esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31);
        // 设置qrcode模块大小
        esc.addSelectSizeOfModuleForQRCode((byte) 4);
        // 设置qrcode内容
        esc.addStoreQRCodeData("https://www.baidu.com");
        // 打印QRCode
        esc.addPrintQRCode();
        esc.addText("\n(扫二维码送手机)\n");
        esc.addText("\n\n\n\n");
        esc.addPrintAndLineFeed();
        //切纸（带切刀打印机才可用）
        esc.addCutPaper();
        // 开钱箱
        esc.addGeneratePlus(LabelCommand.FOOT.F2, (byte) 255, (byte) 255);
        esc.addInitializePrinter();
        //返回指令集
        return esc.getCommand();
    }
    /**
     * 菜单样例
     * @param context
     * @return
     */
    public static Vector<Byte> get80Menu(Context context) {
        EscCommand esc = new EscCommand();
        //初始化打印机
        esc.addInitializePrinter();
        // 设置打印居中
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.flower);//二维码图片，图片类型bitmap
        // 打印图片,图片宽度为384dot  1mm=8dot 用尺子量取图片的宽度单位为Xmm  传入宽度值为 X*8
        esc.drawJpgImage(b, 200);
        // 设置为倍高倍宽
        //字体放大两倍
        esc.addSetCharcterSize(EscCommand.WIDTH_ZOOM.MUL_2, EscCommand.HEIGHT_ZOOM.MUL_2);
        // 打印文字
        esc.addText("爱情餐厅\n\n");
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        esc.addText("520号桌\n\n");
        //字体正常
        esc.addSetCharcterSize(EscCommand.WIDTH_ZOOM.MUL_1, EscCommand.HEIGHT_ZOOM.MUL_1);
        esc.addText("点菜时间 2020-05-20 5:20\n");
        esc.addText("上菜时间 2020-05-20 13:14\n");
        esc.addText("人数：2人 点菜员：新疆包工头\n");
        esc.addText("------------------三行菜单案例------------------\n");
        esc.addText(Menu80Utils.printThreeData("菜名", "数量", "金额"));
        esc.addTurnEmphasizedModeOnOrOff(EscCommand.ENABLE.OFF);
        esc.addText(Menu80Utils.printThreeData("北京烤鸭", "1", "99.99"));
        esc.addText(Menu80Utils.printThreeData("四川麻婆豆腐", "1", "39.99"));
        esc.addText(Menu80Utils.printThreeData("西湖醋鱼", "1", "59.99"));
        esc.addText(Menu80Utils.printThreeData("飞龙汤", "1", "66.66"));
        esc.addText(Menu80Utils.printThreeData("无为熏鸭", "1", "88.88"));
        esc.addText(Menu80Utils.printThreeData("东坡肉", "1", "39.99"));
        esc.addText("------------------四行菜单案例------------------\n");
        //开启加粗
        esc.addTurnEmphasizedModeOnOrOff(EscCommand.ENABLE.ON);
        esc.addText(Menu80Utils.printFourData("菜名", "单价","数量", "金额"));
        esc.addTurnEmphasizedModeOnOrOff(EscCommand.ENABLE.OFF);
        esc.addText(Menu80Utils.printFourData("北京烤鸭", "99.99","1", "99.99"));
        esc.addText(Menu80Utils.printFourData("四川麻婆豆腐", "39.99","1", "39.99"));
        esc.addText(Menu80Utils.printFourData("西湖醋鱼", "59.99","1", "59.99"));
        esc.addText(Menu80Utils.printFourData("飞龙汤", "66.66","1", "66.66"));
        esc.addText(Menu80Utils.printFourData("无为熏鸭", "88.88","1", "88.88"));
        esc.addText(Menu80Utils.printFourData("东坡肉", "39.99","1", "39.99"));
        esc.addText(Menu80Utils.printFourData("辣子鸡", "66.66","1", "66.66"));
        esc.addText(Menu80Utils.printFourData("腊味合蒸", "108.00","1", "108.00"));
        esc.addText(Menu80Utils.printFourData("东安子鸡", "119.00","1", "119.00"));
        esc.addText(Menu80Utils.printFourData("清蒸武昌鱼", "88.88","1", "88.88"));
        esc.addText(Menu80Utils.printFourData("再来两瓶82年的快乐肥宅水(去冰)", "9.00","11", "99.00"));
        esc.addText(Menu80Utils.printFourData("老干妈拌饭", "6.66","1", "6.66"));
        esc.addText("------------------------------------------------\n");
        esc.addText(Menu80Utils.printTwoData("合计：", "1314.00"));
        esc.addText(Menu80Utils.printTwoData("抹零：", "14.00"));
        esc.addText(Menu80Utils.printTwoData("应收：", "1300.00"));
        esc.addText("------------------------------------------------\n");
        esc.addSelectJustification(EscCommand.JUSTIFICATION.RIGHT);
        esc.addText("收银员：广东包租公\n");
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        esc.addText("宣言：我点个鸡蛋都是爱你的形状哦\n");

        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        // 设置纠错等级
        esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31);
        // 设置qrcode模块大小
        esc.addSelectSizeOfModuleForQRCode((byte) 4);
        // 设置qrcode内容
        esc.addStoreQRCodeData("www.baidu.com");
        // 打印QRCode
        esc.addPrintQRCode();
        esc.addText("\n(扫二维码送手机)\n");
        esc.addText("\n\n\n\n\n");
        esc.addPrintAndLineFeed();
        //切纸（带切刀打印机才可用）
        esc.addCutPaper();
        // 开钱箱
        esc.addGeneratePlus(LabelCommand.FOOT.F2, (byte) 255, (byte) 255);
        esc.addInitializePrinter();
        //返回指令集
        return esc.getCommand();
    }

    /**
     * 打印自检页
     * @return
     */
    public static Vector<Byte> getSelfTest() {
        EscCommand esc = new EscCommand();
        byte[] escSelfTestCommand = {0x1f, 0x1b, 0x1f, (byte) 0x93, 0x10, 0x11, 0x12, 0x15, 0x16, 0x17, 0x10, 0x00};
       esc.addUserCommand(escSelfTestCommand);
        return esc.getCommand();
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
        tableLayout.addView(ctv(context, "红茶", 3, 8));
        tableLayout.addView(ctv(context, "绿茶", 8, 10));
        tableLayout.addView(ctv(context, "咖啡", 4, 15));
        tableLayout.addView(ctv(context, "红茶", 3, 8));
        tableLayout.addView(ctv(context, "绿茶", 8, 10));
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
     * @param view
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
        tv1.setTextSize(30);
        tb.addView(tv1);
        TextView tv2=new TextView(context);
        tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT ,TableRow.LayoutParams.WRAP_CONTENT));
        tv2.setText(k+"");
        tv2.setTextColor(Color.BLACK);
        tv2.setTextSize(30);
        tb.addView(tv2);
        TextView tv3=new TextView(context);
        tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT ,TableRow.LayoutParams.WRAP_CONTENT));
        tv3.setText(n+"");
        tv3.setTextColor(Color.BLACK);
        tv3.setTextSize(30);
        tb.addView(tv3);
        return tb;
    }
}
