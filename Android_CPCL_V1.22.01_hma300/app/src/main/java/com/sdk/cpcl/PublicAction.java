package com.sdk.cpcl;

import android.content.Context;

import java.util.HashMap;

public class PublicAction {
    private Context context = null;

    public PublicAction() {

    }

    public PublicAction(Context con) {
        context = con;
    }

    public static final int PAGE_STYPE_RECEIPT = 0x30;
    public static final int PAGE_STYPE_LABEL = 0x31;
    public static final int PAGE_STYPE_LEFT_TOP_BM = 0x32;
    public static final int PAGE_STYPE_LEFT_BEL_BM = 0x33;
    public static final int PAGE_STYPE_RIGHT_TOP_BM = 0x34;
    public static final int PAGE_STYPE_RIGHT_BEL_BM = 0x35;
    public static final int PAGE_STYPE_CENTRAL_TOP_BM = 0x36;
    public static final int PAGE_STYPE_CENTRAL_BEL_BM = 0x37;

    public String getLanguageEncode(String codePage) {
        HashMap<String, String> codeMap = new HashMap<String, String>();
        codeMap.put("Chinese Simplified", "gb2312");
        codeMap.put("ISO8859-1", "iso8859-1");
        codeMap.put("ISO8859-2", "ISO8859-2");
        codeMap.put("ISO8859-3", "iso8859-3");
        codeMap.put("ISO8859-4", "iso8859-4");
        codeMap.put("ISO8859-5", "iso8859-5");
        codeMap.put("ISO8859-6", "iso8859-6");
        codeMap.put("ISO8859-8", "iso8859-8");
        codeMap.put("ISO8859-9", "iso8859-9");
        codeMap.put("ISO8859-15", "iso8859-15");
        codeMap.put("WPC1253", "iso8859-11");
        codeMap.put("KU42", "iso8859-7");
        codeMap.put("TIS18", "windows-874");
        codeMap.put("Khemr", "UnicodeBigUnmarked");
        return codeMap.get(codePage);
    }
}