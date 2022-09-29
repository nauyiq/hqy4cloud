package com.hqy.util.file;

import com.hqy.base.common.base.lang.StringConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * FileValidateContext.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 12:28
 */
public class FileValidateContext {

    public static final List<String> NO_SUPPORT_FILE_TYPES
            = Arrays.asList(".jsp", ".html", ".htm", ".xhtml", ".js", ".php", ".sh", ".svg", ".jspx");

    public static final List<String> SUPPORT_IMAGE_FILE_TYPES
            = Arrays.asList(".jpg", ".jpeg", ".git", ".bmp", ".png", ".emoji");

    public static final List<String> SUPPORT_COMMON_FILE_TYPES
            = Arrays.asList(".zip", ".doc", ".docx", ".xls", "xlsx", ".pdf", ".mp3", ".mp4", ".pdf");


    /**
     * 校验文件类型是否是当前服务支持的.
     * @param fileType 文件类型
     * @return         是否支持?
     */
    public static boolean isSupportedFileType(String fileType) {
        if (StringUtils.isBlank(fileType)) {
            return false;
        }

        if (!fileType.startsWith(StringConstants.Symbol.POINT)) {
            fileType = StringConstants.Symbol.POINT + fileType;
        }

        return SUPPORT_IMAGE_FILE_TYPES.contains(fileType) || SUPPORT_COMMON_FILE_TYPES.contains(fileType);
    }

    /**
     * 校验图片类型是否是当前服务支持的。
     * @param fileType 文件类型。
     * @return          是否支持？
     */
    public static boolean isSupportedImgFileType(String fileType) {
        if (StringUtils.isBlank(fileType)) {
            return false;
        }

        if (!fileType.startsWith(StringConstants.Symbol.POINT)) {
            fileType = StringConstants.Symbol.POINT + fileType;
        }

        return SUPPORT_IMAGE_FILE_TYPES.contains(fileType);
    }


}
