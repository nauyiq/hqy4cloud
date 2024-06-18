package com.hqy.cloud.registry.context;

import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.registry.common.context.BeanRepository;
import com.hqy.cloud.registry.api.Environment;
import com.hqy.cloud.registry.common.exeception.RegisterDiscoverException;
import com.hqy.cloud.registry.common.metadata.MetadataInfo;
import com.hqy.cloud.registry.common.model.ProjectInfoModel;
import com.hqy.cloud.registry.common.model.PubMode;
import com.hqy.cloud.registry.deploy.ApplicationLifecycleDeployer;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.common.base.project.ProjectContextInfo;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;

import java.util.concurrent.Future;


/**
 * ProjectContext.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/5
 */
public record ProjectContext(RegistryContext registryContext,
                             ApplicationLifecycleDeployer deployer) implements SmartInitializingSingleton {
    private static final Logger log = LoggerFactory.getLogger(ProjectContext.class);

    private final static ProjectContextInfo CONTEXT_INFO = new ProjectContextInfo();
    private final static Environment ENVIRONMENT = new Environment();

    @PostConstruct()
    public void init() {
        AssertUtil.notNull(deployer, "Application deployer should not be null.");
        this.deployer.initialize();
        BeanRepository.getInstance().register(ProjectContext.class, this);
        // init project info
        initProjectContext();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterSingletonsInstantiated() {
        AssertUtil.notNull(deployer, "Application deployer should not be null.");
        Future<Boolean> future = (Future<Boolean>) this.deployer.start();
        try {
            if (future.get()) {
                // print project info
                printProjectInfo();
            }
        } catch (Throwable cause) {
            throw new RegisterDiscoverException("Failed execute to start application deployed.", cause);
        }
    }

    private void printProjectInfo() {
        log.info("############################## ############### ############### ###############");
        log.info("##### Server Started OK. serviceName = {}", CONTEXT_INFO.getNameEn());
        log.info("##### Server Started OK : uip = {} ", JsonUtil.toJson(CONTEXT_INFO.getUip()));
        log.info("############################## ############### ############### ###############");
    }

    private void initProjectContext() {
        if (CommonSwitcher.ENABLE_SPRING_BOOT_RESTART_DEVTOOLS.isOn()) {
            System.setProperty("spring.devtools.restart.enabled", "false");
        }
        ProjectInfoModel model = registryContext.getModel();
        MetadataInfo metadataInfo = model.getMetadataInfo();
        // Get project metadata.
        String env = metadataInfo.getEnv();
        PubMode pubMode = metadataInfo.getPubMode();
        ActuatorNode actuatorNode = metadataInfo.getActuatorNode();
        String revision = metadataInfo.getRevision();

        // Set project info
        CONTEXT_INFO.setStartupTimeMillis(System.currentTimeMillis());
        CONTEXT_INFO.setNameEn(model.getApplicationName());
        CONTEXT_INFO.setEnv(env);
        CONTEXT_INFO.setPubValue(pubMode.value);
        CONTEXT_INFO.setNodeType(actuatorNode);
        CONTEXT_INFO.setRevision(revision);
        CONTEXT_INFO.getUip().setHostAddr(model.getIp());
        CONTEXT_INFO.getUip().setPort(model.getPort());
        CONTEXT_INFO.setMetadata(metadataInfo.getMetadataMap());
        // Set environment.
        ENVIRONMENT.setEnvironment(env);
    }

    public static ProjectContextInfo getContextInfo() {
        return CONTEXT_INFO;
    }

    public static Environment getEnvironment() {
        return ENVIRONMENT;
    }





}
