package com.hqy.cloud.util;

import com.hqy.cloud.util.concurrent.AbstractIExecutorService;
import com.hqy.cloud.util.concurrent.IExecutorService;
import com.hqy.cloud.util.concurrent.IExecutorsRepository;

/**
 * 通过的线程池工具类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 16:24
 */
public class ProjectExecutors extends AbstractIExecutorService {
    private static final String NAME = "hqy4cloud";

    private ProjectExecutors() {
        super(NAME);
    }

    public static IExecutorService getInstance() {
        IExecutorService executor = IExecutorsRepository.getExecutor(NAME);
        if (executor == null) {
            executor = new ProjectExecutors();
            IExecutorsRepository.setExecutor(NAME, executor);
        }
        return executor;
    }



}
