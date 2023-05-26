package com.hqy.foundation.common.bind;

import cn.hutool.core.map.MapUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/25 10:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudSecret {
    private String appId;
    private String secretId;
    private String secretKey;
    private Map<String, String> properties = MapUtil.newHashMap(4);
}
