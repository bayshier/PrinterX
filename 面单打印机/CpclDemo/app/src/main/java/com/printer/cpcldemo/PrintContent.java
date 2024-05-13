package com.printer.cpcldemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.gprinter.command.CpclCommand;

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
     * 面单案例
     * @param context
     * @return
     */
    public  static Vector<Byte> getExample(Context context){
        CpclCommand cpcl=new CpclCommand();
        cpcl.addUserCommand("\r\n");
        cpcl.addInitializePrinter(900, 1);
        cpcl.addPagewidth(576);
        cpcl.addJustification(CpclCommand.ALIGNMENT.CENTER);
        cpcl.addSetmag(1, 1);
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 30, "CPCL测试");
        cpcl.addSetmag(0, 0);
        cpcl.addJustification(CpclCommand.ALIGNMENT.LEFT);
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 65, "打印文字测试：");
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 95, "欢迎使用打印机！");
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 155, "打印对齐方式测试：");
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 195, "居左");
        cpcl.addJustification(CpclCommand.ALIGNMENT.CENTER);
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 195, "居中");
        cpcl.addJustification(CpclCommand.ALIGNMENT.RIGHT);
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 195, "居右");
        cpcl.addJustification(CpclCommand.ALIGNMENT.LEFT);
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 260, "打印 Bitmap图测试：");
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_priter);
        cpcl.addEGraphics(0, 295, 385, bitmap);
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0,500, "打印条码测试:");
        cpcl.addBarcode(CpclCommand.COMMAND.BARCODE, CpclCommand.CPCLBARCODETYPE.CODE128, 100, 0, 540, "barcode128");
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 680, "打印二维码测试:");
        cpcl.addBQrcode(0, 720, "QRcode");
        cpcl.addJustification(CpclCommand.ALIGNMENT.CENTER);
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 860, "测试完成！");
        cpcl.addJustification(CpclCommand.ALIGNMENT.LEFT);
        cpcl.addPrint();
        Vector<Byte> datas = cpcl.getCommand();
        return datas;
    }



}
