package com.hqy.cloud.file.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件使用的场景
 * @author hongqy
 * @date 2025/4/3
 */
@Getter
@AllArgsConstructor
public enum FileScene {

    AVATAR("/avatar", FileAccessControl.PUBLIC_READ),

    IMAGE("/image", FileAccessControl.PUBLIC_READ)


    ;

    private final String folder;
    private final FileAccessControl accessControl;




}
