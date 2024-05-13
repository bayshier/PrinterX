package com.sdk.cpcl;

public class checkClick {
    private static final long CLICK_DELAY = 1000;
    private static long mOldClickTime;

    public static boolean isClickEvent() {
        long time = System.currentTimeMillis();
        if (time - mOldClickTime < CLICK_DELAY)
            return false;

        mOldClickTime = time;
        return true;
    }
}
