package com.hqy.common.logback;

import ch.qos.logback.core.util.EnvUtil;

/**
 * 根据是否windows环境决定要不要支持<appender-ref ref=\"stdout\"/>的输出<br>
 * 不建议使用原生的ch.qos.logback.core.ConsoleAppender，在超大日志量的场合，可能导致线程死锁阻塞等...
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-09 17:51
 */
public class ConsoleAppender<E> extends ch.qos.logback.core.ConsoleAppender<E> {

    @Override
    public void start() {
        if(EnvUtil.isWindows()) {
            //windows 环境下 支持<appender-ref ref=\"stdout\"/>的输出，方便IDE调试
            super.start();
        }else {
            //非windows环境下忽略<appender-ref ref=\"stdout\"/>的输出，当做没配置，防止线程锁....
            System.out.println("com.hqy.common.logback.ConsoleAppender  IGNORE <appender-ref ref=\"stdout\"/> \r\n");
        }
    }
}
