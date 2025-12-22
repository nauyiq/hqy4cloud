package com.hqy.cloud.web.support;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.auth.annotation.AuthenticationResource;
import com.hqy.cloud.auth.common.AuthorizationResourceDTO;
import com.hqy.cloud.auth.core.AuthorizationResourceRepository;
import com.hqy.cloud.web.annotation.BsWebAdvice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;

/**
 * HTTP权限资源注解扫描器
 * 在Spring容器启动时扫描所有带有@AuthenticationResource注解的方法
 * 并将资源权限信息注册到AuthorizationResourceRepository中
 * 
 * @author hongqy
 * @date 2025/12/15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpAuthenticationResourceScanner implements BeanPostProcessor {

    private final AuthorizationResourceRepository repository;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);

        // 只处理Controller相关的Bean
        if (!isController(targetClass)) {
            return bean;
        }

        // 获取类级别的请求路径
        String classLevelPath = getClassLevelPath(targetClass);

        // 扫描方法级别的注解
        Method[] methods = targetClass.getDeclaredMethods();
        for (Method method : methods) {
            AuthenticationResource annotation = AnnotationUtils.findAnnotation(method, AuthenticationResource.class);
            if (annotation != null) {
                // 注册权限资源
                processAuthenticationResource(targetClass, method, classLevelPath, annotation);
            }

            BsWebAdvice bsWebAdvice = AnnotationUtils.findAnnotation(method, BsWebAdvice.class);
            if (bsWebAdvice != null && bsWebAdvice.requiredToken()) {
                processBsWebAdvice(targetClass, method, classLevelPath, bsWebAdvice);
            }
        }

        return bean;
    }

    private void processBsWebAdvice(Class<?> targetClass, Method method, String classLevelPath, BsWebAdvice bsWebAdvice) {
        try {
            // 方法级别的路径可能为空
            String methodPath = getMethodPath(method);

            // 拼接类级别路径和方法级别路径
            String fullPath = combinePaths(classLevelPath, methodPath);
            String normalizedPath = normalizePath(fullPath);

            if (!bsWebAdvice.requiredToken()) {
                repository.registerIgnoredAccessTokenUri(normalizedPath);
            }

            if (bsWebAdvice.requiredIdentifier()) {
                repository.registerIdentifierTokenUri(normalizedPath);
            }

        } catch (Exception e) {
            log.error("Failed to process BsWebAdvice annotation for method: {}.{}",
                    targetClass.getName(), method.getName(), e);
        }
    }

    /**
     * 处理权限资源注解
     */
    private void processAuthenticationResource(Class<?> targetClass, Method method,
                                               String classLevelPath,
                                               AuthenticationResource annotation) {
        try {
            // 获取资源ID
            String resourceId = getResourceId(method, classLevelPath, annotation);
            if (StringUtils.isBlank(resourceId)) {
                log.warn("Cannot determine resource ID for method: {}.{}", targetClass.getName(), method.getName());
                return;
            }

            // 获取权限列表
            String[] authorities = annotation.authorities();
            if (authorities == null || authorities.length == 0) {
                log.warn("No authorities defined for resource: {}", resourceId);
                return;
            }

            // 构建AuthorizationResourceDTO对象
            AuthorizationResourceDTO dto = new AuthorizationResourceDTO();
            dto.setId(resourceId);
            dto.setAuthorities(new HashSet<>(Arrays.asList(authorities)));

            // 注册到仓库
            repository.registerAuthorizationResource(dto);

            log.info("Registered authentication resource: {} -> authorities={}", resourceId, authorities);

        } catch (Exception e) {
            log.error("Failed to process authentication resource annotation for method: {}.{}",
                    targetClass.getName(), method.getName(), e);
        }
    }

    /**
     * 判断是否是Controller
     */
    private boolean isController(Class<?> clazz) {
        return AnnotationUtils.findAnnotation(clazz, Controller.class) != null ||
                AnnotationUtils.findAnnotation(clazz, RestController.class) != null;
    }

    /**
     * 获取类级别的路径
     */
    private String getClassLevelPath(Class<?> clazz) {
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(clazz, RequestMapping.class);
        if (requestMapping != null && requestMapping.value().length > 0) {
            return requestMapping.value()[0];
        }
        if (requestMapping != null && requestMapping.path().length > 0) {
            return requestMapping.path()[0];
        }
        return "";
    }

    /**
     * 获取资源ID
     * 规则: 如果注解中指定了id，则直接使用；否则使用 HTTP方法 + "_" + URI
     */
    private String getResourceId(Method method, String classLevelPath, AuthenticationResource annotation) {
        // 如果注解中指定了资源ID，则直接使用
        if (StringUtils.isNotBlank(annotation.id())) {
            return annotation.id();
        }

        // 否则自动生成: HTTP方法 + "_" + URI
        String httpMethod = getHttpMethod(method);
        if (StringUtils.isBlank(httpMethod)) {
            log.warn("Cannot determine HTTP method for method: {}", method.getName());
            return null;
        }

        // 方法级别的路径可能为空
        String methodPath = getMethodPath(method);

        // 拼接类级别路径和方法级别路径
        String fullPath = combinePaths(classLevelPath, methodPath);
        String normalizedPath = normalizePath(fullPath);

        return httpMethod + StrUtil.UNDERLINE + normalizedPath;
    }

    /**
     * 获取HTTP方法类型
     */
    private String getHttpMethod(Method method) {
        if (AnnotationUtils.findAnnotation(method, GetMapping.class) != null) {
            return "GET";
        }
        if (AnnotationUtils.findAnnotation(method, PostMapping.class) != null) {
            return "POST";
        }
        if (AnnotationUtils.findAnnotation(method, PutMapping.class) != null) {
            return "PUT";
        }
        if (AnnotationUtils.findAnnotation(method, DeleteMapping.class) != null) {
            return "DELETE";
        }
        if (AnnotationUtils.findAnnotation(method, PatchMapping.class) != null) {
            return "PATCH";
        }

        RequestMapping requestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
        if (requestMapping != null && requestMapping.method().length > 0) {
            return requestMapping.method()[0].name();
        }

        return null;
    }

    /**
     * 获取方法级别的路径
     */
    private String getMethodPath(Method method) {
        // 尝试从各种RequestMapping注解中获取路径
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
        if (requestMapping != null) {
            return getPathFromAnnotation(requestMapping.value(), requestMapping.path());
        }

        GetMapping getMapping = AnnotationUtils.findAnnotation(method, GetMapping.class);
        if (getMapping != null) {
            return getPathFromAnnotation(getMapping.value(), getMapping.path());
        }

        PostMapping postMapping = AnnotationUtils.findAnnotation(method, PostMapping.class);
        if (postMapping != null) {
            return getPathFromAnnotation(postMapping.value(), postMapping.path());
        }

        PutMapping putMapping = AnnotationUtils.findAnnotation(method, PutMapping.class);
        if (putMapping != null) {
            return getPathFromAnnotation(putMapping.value(), putMapping.path());
        }

        DeleteMapping deleteMapping = AnnotationUtils.findAnnotation(method, DeleteMapping.class);
        if (deleteMapping != null) {
            return getPathFromAnnotation(deleteMapping.value(), deleteMapping.path());
        }

        PatchMapping patchMapping = AnnotationUtils.findAnnotation(method, PatchMapping.class);
        if (patchMapping != null) {
            return getPathFromAnnotation(patchMapping.value(), patchMapping.path());
        }

        return null;
    }

    /**
     * 从注解中获取路径
     */
    private String getPathFromAnnotation(String[] values, String[] paths) {
        if (values.length > 0) {
            return values[0];
        }
        if (paths.length > 0) {
            return paths[0];
        }
        return null;
    }

    /**
     * 合并路径
     */
    private String combinePaths(String path1, String path2) {
        if (StringUtils.isBlank(path1)) {
            return path2;
        }
        if (StringUtils.isBlank(path2)) {
            return path1;
        }

        String p1 = path1.endsWith("/") ? path1.substring(0, path1.length() - 1) : path1;
        String p2 = path2.startsWith("/") ? path2 : "/" + path2;
        return p1 + p2;
    }

    /**
     * 规范化路径
     */
    private String normalizePath(String path) {
        if (StringUtils.isBlank(path)) {
            return path;
        }
        // 确保路径以/开头
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }
}
