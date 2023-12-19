package com.hqy.cloud.notice.email;

import com.hqy.foundation.notice.AbstractNotifierFactory;
import com.hqy.foundation.notice.Notifier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.env.Environment;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/19 17:30
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailNotifierFactory extends AbstractNotifierFactory {
    private static final EmailNotifierFactory INSTANCE = new EmailNotifierFactory();
    public static EmailNotifierFactory getInstance() {
        return INSTANCE;
    }

    @Override
    protected Notifier doCreate(Environment environment) {
        return new EmailNotifier(environment);
    }
}
