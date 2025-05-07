package com.hqy.cloud.file.common.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author hongqy
 * @date 2025/4/3
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FileQueryRequest {

    /**
     * 访问路径
     */
    @NotNull(message = "访问路径不能为空")
    private String accessUri;

}
