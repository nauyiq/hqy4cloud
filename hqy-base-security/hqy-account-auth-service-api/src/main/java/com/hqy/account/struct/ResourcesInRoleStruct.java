package com.hqy.account.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * ResourceInRoleStruct.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/24 14:32
 */
@Data
@ThriftStruct
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public final class ResourcesInRoleStruct implements Serializable {

    /**
     * 角色
     */
    @ThriftField(1)
    public String role;

    /**
     * 资源
     */
    @ThriftField(2)
    public List<ResourceStruct> resources;




}
