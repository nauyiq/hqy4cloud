package com.hqy.cloud.auth.admin.component;

import com.hqy.cloud.auth.account.entity.AccountMenu;
import com.hqy.cloud.auth.admin.annotation.MenuAuthentication;
import com.hqy.cloud.auth.service.AuthOperationService;
import com.hqy.cloud.auth.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/27
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class MenuAuthenticationAspect {

    private final AuthOperationService authOperationService;

    @Pointcut("(execution(public * *(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping) && @annotation(com.hqy.cloud.auth.admin.annotation.MenuAuthentication)) || "
            + "(execution(public * *(..)) && @annotation(org.springframework.web.bind.annotation.GetMapping) && @annotation(com.hqy.cloud.auth.admin.annotation.MenuAuthentication)) || "
            + "(execution(public * *(..)) && @annotation(org.springframework.web.bind.annotation.PostMapping) && @annotation(com.hqy.cloud.auth.admin.annotation.MenuAuthentication)) || "
            + "(execution(public * *(..)) && @annotation(org.springframework.web.bind.annotation.PutMapping) && @annotation(com.hqy.cloud.auth.admin.annotation.MenuAuthentication)) || "
            + "(execution(public * *(..)) && @annotation(org.springframework.web.bind.annotation.DeleteMapping) && @annotation(com.hqy.cloud.auth.admin.annotation.MenuAuthentication)) ")
    private void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MenuAuthentication authentication = getAuthentication(pjp);
        if (Objects.isNull(authentication)) {
            return pjp.proceed();
        }
        // 判断是否有权限.
        Long currentUserId = AuthUtils.getCurrentUserId();
        List<AccountMenu> menus = authOperationService.getAccountMenus(currentUserId);
        return menus.parallelStream().anyMatch(menu -> menu.getMenuPermission().equals(authentication.value()));
    }


    private MenuAuthentication getAuthentication(JoinPoint point) {
        try {
            Method method = ((MethodSignature) point.getSignature()).getMethod();
            MenuAuthentication annotation = method.getAnnotation(MenuAuthentication.class);
            if (Objects.isNull(annotation)) {
                log.warn("Failed execute to do annotation for around aspect, not found annotation.");
            }
            return annotation;
        } catch (Throwable cause) {
            log.error("Failed execute to get Logging annotation, message: {}.", cause.getMessage(), cause);
            return null;
        }
    }

}
