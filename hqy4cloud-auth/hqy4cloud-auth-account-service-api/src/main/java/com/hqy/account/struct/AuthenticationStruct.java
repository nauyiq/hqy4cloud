package com.hqy.account.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * AuthenticationStruct.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/24 14:32
 */
@Data
@ThriftStruct
@NoArgsConstructor
@AllArgsConstructor
public final class AuthenticationStruct implements Serializable {

    private static final long serialVersionUID = 4255955611344964526L;

    /**
     * 角色
     */
    @ThriftField(1)
    public String role;

    /**
     * 资源配置
     */
    @ThriftField(2)
    public List<ResourceStruct> resources;


    /**
     * 权限列表
     */
    @ThriftField(3)
    public List<String> permissions;



}
