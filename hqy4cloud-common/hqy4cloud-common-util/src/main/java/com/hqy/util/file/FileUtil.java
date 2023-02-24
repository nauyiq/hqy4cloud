package com.hqy.util.file;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.UUID;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.lang.exception.UploadFileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * FileUtil.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 11:33
 */
@Slf4j
public class FileUtil {

    public static boolean validateFileType(String fileName) {
        return validateFileType(fileName, false);
    }

    public static boolean validateImgFileType(String fileName) {
        return validateFileType(fileName, true);
    }

    /**
     * valid file type is available
     * @param fileName file name.
     * @param checkImg is image type.
     * @return         is available.
     */
    public static boolean validateFileType(String fileName, boolean checkImg) {
        String extName = FileNameUtil.extName(fileName);
        if (StringUtils.isBlank(extName)) {
            // not file suffix
            log.info("Not found file suffix, filename: {}", fileName);
            //不能有百分号，防止黑客使用%转义, ' 或者 < 引入脚本
            return !fileName.contains("&") && !fileName.contains("<") && !fileName.contains("'");
        } else {
            String suffix = StringConstants.Symbol.POINT.concat(extName);
            if (FileValidateContext.NO_SUPPORT_FILE_TYPES.contains(suffix.toLowerCase())) {
                return false;
            }
            if (checkImg) {
                return FileValidateContext.isSupportedImgFileType(suffix);
            } else {
                return FileValidateContext.isSupportedFileType(suffix);
            }

        }
    }

    /**
     * 生成uuid类型的文件名
     * @param fileName  file name.
     * @return          uuid file name.
     */
    public static String generateUUIDFileName(final String fileName) {
        String extName = FileNameUtil.extName(fileName);
        String uuid = UUID.fastUUID().toString(true);
        return StringUtils.isBlank(extName) ? uuid : uuid + StringConstants.Symbol.POINT + extName;
    }


    public static void writeToFile(final MultipartFile file, final String fileName) throws UploadFileException {
        int index = fileName.lastIndexOf(StringConstants.Symbol.POINT);
        if (index != -1) {
            String prefixFileName = fileName.substring(index);
            if (prefixFileName.trim().equals(StringConstants.Symbol.POINT)) {
                throw new UploadFileException("File suffix should not be '.' .");
            }
        }
        try {
            cn.hutool.core.io.FileUtil.writeBytes(file.getBytes(), fileName);
        } catch (IOException e) {
            log.error("Write file error, cause: " + e.getMessage());
            throw new UploadFileException(e);
        }

    }






}
