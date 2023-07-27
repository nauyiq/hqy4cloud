package com.hqy.cloud.util.config;

import cn.hutool.system.SystemUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * 抽象的 加载配置文件的 策略
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/4 10:40
 */
public abstract class AbstractConfigStrategy implements AbstractStrategyProcessor {

    private static final Logger log = LoggerFactory.getLogger(AbstractConfigStrategy.class);

    /**
     * 配置文件名
     */
    private final String propertyName;

    /**
     * 外部配置文件
     */
    private String configPath;

    AbstractConfigStrategy(String propertyName) {
        this.propertyName = propertyName;
        setConfigPath();
        loadConfigFile();
    }

    private void setConfigPath() {
        if (SystemUtil.getOsInfo().isWindows()) {
            configPath = "D:/hongqy/project/conf";
        } else {
            configPath = "/hongqy/project/conf";
        }
    }

    /**
     * 加载配置文件数据到内存
     * @param inputStream 对应文件输入流
     */
    protected abstract void loadConfig(InputStream inputStream);

    @Override
    public void loadConfigFile() {
        InputStream inputStream = null;
        try {
            File externalFile = new File(configPath + StringConstants.Symbol.INCLINED_ROD + propertyName);
            if (externalFile.exists()) {
                log.info("@@@ 加载外部配置文件:{}", externalFile.getAbsolutePath());
                inputStream = new FileInputStream(externalFile);
            } else {
                log.info("@@@ 加载classpath下的配置文件: {}", propertyName);
                inputStream = this.getClass().getClassLoader().getResourceAsStream(propertyName);
                if (Objects.isNull(inputStream)) {
                    log.warn("@@@ 当前项目classpath下没有此[{}]配置文件, 忽略.", propertyName);
                    return;
                }
            }
            log.info("@@@ 开始加载配置文件数据到内存.");
            loadConfig(inputStream);
        } catch (Exception e) {
            log.error("@@@ 加载配置文件失败. filename：" + propertyName);
        } finally {
            if (Objects.nonNull(inputStream)) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public String getPropertyName() {
        return propertyName;
    }



}
