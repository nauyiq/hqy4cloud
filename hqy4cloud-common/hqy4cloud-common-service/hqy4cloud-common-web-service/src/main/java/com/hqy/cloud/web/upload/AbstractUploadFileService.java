package com.hqy.cloud.web.upload;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.lang.exception.UploadFileException;
import com.hqy.cloud.util.CommonDateUtil;
import com.hqy.cloud.util.ImageUtil;
import com.hqy.cloud.util.config.ConfigurationContext;
import com.hqy.cloud.util.file.FileUtil;
import com.hqy.cloud.util.file.FileValidateContext;
import com.hqy.cloud.util.thread.NamedThreadFactory;
import com.hqy.cloud.web.common.UploadResult;
import com.hqy.cloud.web.common.annotation.UploadMode;
import com.hqy.cloud.web.config.UploadFileProperties;
import com.hqy.cloud.web.upload.support.MultipartFileAdaptor;
import com.hqy.cloud.web.upload.support.UploadContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.hqy.cloud.common.base.lang.StringConstants.DO_PNG;
import static com.hqy.cloud.util.ImageUtil.DOWNLOAD_TMP_FOLDER;
import static com.hqy.cloud.web.upload.support.UploadContext.DEFAULT_STATE;

/**
 * AbstractUploadFileService.
 * @see UploadFileService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 11:13
 */
@Slf4j
public abstract class AbstractUploadFileService implements UploadFileService {
    private final UploadFileProperties properties;
    protected ExecutorService threadPool;

    public AbstractUploadFileService(UploadFileProperties properties) {
        this.properties = properties;
        this.threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() / 2, properties.getMaxThreadCore(),
                5 * 1000 * 60, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new NamedThreadFactory("Upload"));
    }

    @Override
    public UploadResponse uploadAvatar(MultipartFile file) throws UploadFileException {
        return uploadImgFile(AVATAR_FOLDER, file);
    }

    @Override
    public UploadResponse uploadImgFile(String folderPath, MultipartFile file) throws UploadFileException {
        return doUpload(folderPath, file, true);
    }

    @Override
    public UploadResponse uploadFile(String folderPath, MultipartFile file) throws UploadFileException {
        return doUpload(folderPath, file, false);
    }

    @SneakyThrows
    public static void main(String[] args) {
        List<String> files = Arrays.asList("https://file-1314878458.cos.ap-guangzhou.myqcloud.com/files/avatar/3610a21b38ce460abbe614832a9d915e.jpg",
                StringConstants.Host.HTTPS_FILE_ACCESS + "/files/avatar/20221205/58ebe0d182f54b15956750f8b20044cc.jpg",
                StringConstants.Host.HTTPS_FILE_ACCESS + "/files/avatar/20221205/58ebe0d182f54b15956750f8b20044cc.jpg");
        String configPath = ConfigurationContext.getConfigPath();
        String generatorFolder = configPath.concat("/files/avatar/group");
        String fileName = UUID.fastUUID().toString(true) + ".png";
//        String outputPath = generatorFolder + StrUtil.SLASH + fileName;
        ImageUtil.createImage(files, ConfigurationContext.getConfigPath() + DOWNLOAD_TMP_FOLDER, fileName);
    }

    @Override
    public UploadResponse generateFile(List<String> files, String folder) throws UploadFileException {
        String configPath = ConfigurationContext.getConfigPath();
        //获取系统文件生成的路径.
        String generatorFolder = configPath.concat(DOWNLOAD_TMP_FOLDER);
        String fileName = UUID.fastUUID().toString(true) + DO_PNG;
        String outputPath = generatorFolder + StrUtil.SLASH + fileName;
        try {
            ImageUtil.createImage(files, generatorFolder, fileName);
        } catch (Exception e) {
            log.warn("Failed execute to generate file: {}.", outputPath, e);
            throw new UploadFileException("Failed execute generator files: " + files + " cause:" + e.getMessage());
        }
        File file = new File(outputPath);
        if (!file.exists()) {
            log.warn("Not found file: {}.", outputPath);
            throw new UploadFileException("Failed execute generator file: " + outputPath);
        }
        try {
            MultipartFile multipartFile = new MultipartFileAdaptor(fileName, new FileInputStream(file));
            return writeFile(fileName, folder, DEFAULT_STATE , multipartFile);
        } catch (Exception e) {
            throw new UploadFileException("Failed execute generator file: " + outputPath);
        } finally {
            if (file.exists()) {
                log.info("Remove generate temp file = {}, result = {}.", outputPath, file.delete());
            }
        }
    }

    @Override
    public void addUploadSupportImgFileType(String fileType) {
        if (StringUtils.isBlank(fileType)) {
            return;
        }
        if (!fileType.startsWith(StringConstants.Symbol.POINT)) {
            fileType = StringConstants.Symbol.POINT + fileType.trim();
        }
        FileValidateContext.SUPPORT_IMAGE_FILE_TYPES.add(fileType);
        log.info("Service add new support image file type, invalid after Service restart, image fileType: {}.", fileType);
    }

    @Override
    public void addUploadSupportFileType(String fileType) {
        if (StringUtils.isBlank(fileType)) {
            return;
        }
        if (!fileType.startsWith(StringConstants.Symbol.POINT)) {
            fileType = StringConstants.Symbol.POINT + fileType.trim();
        }
        FileValidateContext.SUPPORT_COMMON_FILE_TYPES.add(fileType);
        log.info("Service add new support file type, invalid after Service restart, fileType: {}.", fileType);
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    private UploadResponse doUpload(String folderPath, MultipartFile file, boolean isImageUpload) {
        UploadContext.UploadState uploadState = getUploadState();
        UploadMode.Mode uploadMode = uploadState.getMode();
        UploadResult result;
        if (StringUtils.isBlank(folderPath)) {
            result = UploadResult.failed("Upload image file folderPath should not be null.");
            return buildResponse(result, uploadMode);
        }
        if (!folderPath.startsWith(StringConstants.Symbol.INCLINED_ROD)) {
            folderPath = StringConstants.Symbol.INCLINED_ROD + folderPath;
        }
        String originalFilename = file.getOriginalFilename();
        //checking file type.
        boolean validate = validateFileType(originalFilename, isImageUpload);
        if (!validate) {
            result = UploadResult.failed("No support file type.");
            return buildResponse(result, uploadMode);
        }
        // check file.
        result = validateFile(file);
        if (!result.isResult()) {
            return buildResponse(result, uploadMode);
        }
        return writeFile(originalFilename, folderPath, uploadState, file);
    }

    /**
     * 写文件.
     * @param originalFilename     起始文件名
     * @param folderPath           文件目录
     * @param state                文件上传方式，同步或异步
     * @param file                 文件
     * @return                     {@link UploadResponse}
     * @throws UploadFileException e.
     */
    protected abstract UploadResponse writeFile(String originalFilename, String folderPath, UploadContext.UploadState state, MultipartFile file) throws UploadFileException;


    private UploadContext.UploadState getUploadState() {
        UploadContext.UploadState state = UploadContext.getState();
        if (state == null) {
            return DEFAULT_STATE;
        }
        return state;
    }

    protected boolean validateFileType(String filename, boolean imageType) {
        if (imageType) {
            return FileUtil.validateImgFileType(filename);
        } else {
            return FileUtil.validateFileType(filename);
        }
    }

    protected String generateRelativeFilePath(String folder, String baseFileName) {
        return folder +
                StringConstants.Symbol.INCLINED_ROD +
                CommonDateUtil.today() +
                StringConstants.Symbol.INCLINED_ROD +
                baseFileName;
    }

    protected UploadResult validateFile(MultipartFile file) {
        long maxSize = properties.getSize().toMillis();
        if (maxSize > 0 && file.getSize() > maxSize) {
            return UploadResult.failed("The file size larger than " + maxSize);
        }
        return UploadResult.success();
    }

    /**
     * generate file name.
     * @param originalFilename originalFilename.
     * @return new file name.
     */
    public String generateFileName(String originalFilename) {
        return FileUtil.generateUUIDFileName(originalFilename);
    }

    public UploadFileProperties getProperties() {
        return properties;
    }


    protected UploadResponse buildResponse(UploadResult result, UploadMode.Mode mode) {
        return new UploadResponse() {
            @Override
            public UploadMode.Mode uploadMode() {
                return mode == null ? UploadMode.Mode.SYNC : mode;
            }
            @Override
            public UploadResult getResult(boolean syncWait) {
                return result;
            }
        };
    }




}
