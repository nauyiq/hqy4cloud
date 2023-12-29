package com.hqy.cloud.registry.common.model;

import com.hqy.cloud.common.base.Parameters;
import com.hqy.cloud.common.base.lang.ActuatorNode;
import lombok.extern.slf4j.Slf4j;

import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Metadata of the service instance registered to the registry.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29 10:24
 */
@Slf4j
public class MetadataInfo extends Parameters {

    private String application;
    private String env;
    private ActuatorNode actuatorNode;
    private String revision;

    private ConcurrentHashMap<String, SortedSet<RegistryInfo>> subscribeServiceInfos;



}
