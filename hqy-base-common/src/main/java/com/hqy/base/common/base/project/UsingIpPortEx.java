package com.hqy.base.common.base.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/2/24 15:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsingIpPortEx {

    /**
     * 节点ip地址信息
     */
    private UsingIpPort uip;

    /**
     * 接口名
     */
    String interfaceName;

}
