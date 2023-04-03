package com.hqy.cloud.auth.base.dto;

import com.hqy.account.struct.ResourceStruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/19 9:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceConfigDTO {

    private Integer id;
    private String path;
    private String method;

    public ResourceConfigDTO(ResourceStruct e) {
        this.id = e.id;
        this.path = e.path;
        this.method = e.method;
    }
}
