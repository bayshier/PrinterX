package com.ctaiot.ctprinter.ctpl_demo;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;


public class DensityUtil {

    private static DisplayMetrics displayMetrics;

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dp2px(Context context, float dipValue) {
        if (context == null){
            return 10;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * sp转pt
     * @param
     * @return
     * @modi by twm 20191022
     */
    public static float sp2pt(Context context, float spValue, float scale) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = spValue * metrics.scaledDensity;
        float mm = px / scale;
        float pt = (float) (mm /0.3528) ; ///72/25.4
        return pt;
    }

    /**
     * pt转sp
     *
     * @param pt
     * @return
     * @modi
     * 3.2.0读取方式
     */
    public static int pt2sp(Context context, float pt, float scale) {
        // px = dp * (dpi / 160)
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float dpi = metrics.scaledDensity;
        float mm = (float) (pt * 0.3528);  //25.4/72
        float px = mm * scale ;
        float sp = px / dpi;
        return (int) (sp + 0.5f);
    }

    public static float px2pt(Context c,float px){
        return px / (1F / 72) / c.getResources().getDisplayMetrics().xdpi;
    }

    /**
     * pt转sp
     * @param pt
     * @return
     * @modi   3.1.0读取方式
     */
    public static int pt2sp2(Context context, float pt, float scale) {
        // px = dp * (dpi / 160)
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float dpi = metrics.scaledDensity;
        float mm = (float) (pt * 0.376);
        float px = mm * scale * 72 / 96;
        float sp = px / dpi;
        return (int) (sp + 0.5f);
    }

    private static DisplayMetrics getDisplayMetrics(Context c) {
        if(displayMetrics == null) {
            displayMetrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics;
    }

    public static int getScreenWidth(Context c) {
        return getDisplayMetrics(c).widthPixels;
    }


    public static int getScreenHeight(Context c) {
        return getDisplayMetrics(c).heightPixels;
    }
}
