package com.hqy.cloud.util.sensitive;

import com.github.houbb.chars.scan.bs.CharsScanBs;
import com.github.houbb.chars.scan.util.InnerCharsScanPropertyBuilder;

/**
 * @author hongqy
 * @date 2026/1/28
 */
public class LogDesensitizedUtil {
    private static final CharsScanBs CHARS_SCAN_BS = buildCharsScanBs();

    public static String desensitized(String desensitizeMsg) {
        return CHARS_SCAN_BS.scanAndReplace(desensitizeMsg);
    }

    private static CharsScanBs buildCharsScanBs() {
        String prefix = "：‘“，| ,:\\\"'=";
        String scanList = "1,2,3,4,9";
        String replaceList =  "1,2,3,4,9";
        String defaultReplace = "12";
        String replaceHash = "none";
        String whiteList = "";
        return InnerCharsScanPropertyBuilder.buildCharsScanBs(prefix, scanList, replaceList, defaultReplace, replaceHash, whiteList);
    }

}
