package com.hqy.cloud.file.common;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.util.AssertUtil;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * MultipartFile适配类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/26 10:15
 */
public class MultipartFileAdaptor implements MultipartFile {
    private final String name;
    private final String originalFilename;
    private final String contentType;
    private final byte[] content;

    public MultipartFileAdaptor(String name, byte[] content) {
        this(name, StrUtil.EMPTY, null, content);
    }

    public MultipartFileAdaptor(String name, InputStream contentStream) throws IOException {
        this(name, StrUtil.EMPTY, null, FileCopyUtils.copyToByteArray(contentStream));
    }

    public MultipartFileAdaptor(String name, String originalFilename, String contentType,
                                byte[] content) {
        AssertUtil.notEmpty(name, "Name should not be empty.");
        this.name = name;
        this.originalFilename = (StrUtil.isNotBlank(originalFilename) ? originalFilename : name);
        this.contentType = contentType;
        this.content = (content != null ? content : new byte[0]);
    }

    public MultipartFileAdaptor(
            String name, String originalFilename,
            String contentType, InputStream contentStream)
            throws IOException {

        this(name, originalFilename, contentType, FileCopyUtils.copyToByteArray(contentStream));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getOriginalFilename() {
        return this.originalFilename;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty() {
        return (this.content.length == 0);
    }

    @Override
    public long getSize() {
        return this.content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return this.content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    @Override
    public void transferTo(File file) throws IOException, IllegalStateException {
        FileCopyUtils.copy(this.content, file);
    }
}
