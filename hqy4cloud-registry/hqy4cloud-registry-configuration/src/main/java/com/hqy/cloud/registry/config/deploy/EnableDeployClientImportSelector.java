package com.hqy.cloud.registry.config.deploy;

import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.common.base.lang.DeployComponent;
import org.springframework.cloud.commons.util.SpringFactoryImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/8
 */
public class EnableDeployClientImportSelector extends SpringFactoryImportSelector<EnableDeployClient> {

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        String[] imports = super.selectImports(metadata);

        AnnotationAttributes attributes = AnnotationAttributes
                .fromMap(metadata.getAnnotationAttributes(getAnnotationClass().getName(), true));

        if (attributes == null) {
            throw new IllegalStateException("Read @EnableDeployClient error.");
        }

        boolean enableDeploy = attributes.getBoolean("enableDeploy");

        if (enableDeploy) {
            List<String> importsList = new ArrayList<>(Arrays.asList(imports));
            importsList.add("com.hqy.cloud.registry.config.autoconfigure.AutoApplicationDeployerConfiguration");
            imports = importsList.toArray(new String[0]);

            ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) getEnvironment();
            LinkedHashMap<String, Object> map = new LinkedHashMap<>(8);

            // get deploy actuator type and setting deploy components.
            ActuatorNode actuatorType = ActuatorNode.valueOf(attributes.getString("actuatorType"));
            String prefix = AutoApplicationDeployerProperties.PREFIX + ".components.";
            for (DeployComponent component : actuatorType.components) {
                map.put(prefix + component.name + ".enabled", true);
            }
            // get application revision
            String revision = attributes.getString("revision");
            map.put(AutoApplicationDeployerProperties.PREFIX + "revision", revision);
            // get application weight
            Integer weight = attributes.getNumber("weight");
            map.put(AutoApplicationDeployerProperties.PREFIX + "weight", weight);

            MapPropertySource propertySource = new MapPropertySource("hqy4cloudApplicationDeployerClient", map);
            configurableEnvironment.getPropertySources().addLast(propertySource);
        }

        return imports;
    }

    @Override
    protected boolean isEnabled() {
        return getEnvironment().getProperty("hqy4cloud.application.deploy.enabled", Boolean.class, Boolean.TRUE);
    }

    @Override
    protected boolean hasDefaultFactory() {
        return true;
    }
}
