package com.hqy.util;

import com.hqy.base.common.swticher.CommonSwitcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.net.ServerSocket;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/8 9:41
 */
public class NetUtils {

    private static final Logger log = LoggerFactory.getLogger(NetUtils.class);

    /**
     * 获取进程号
     * @return pid
     */
    public static int getProgramId() {
        return Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }

    /**
     * 判断端口是否被占用
     * @param serverPort 端口是否可用
     * @return boolean result.
     */
    public static boolean isPortUsing(int serverPort) {
        boolean result = true;
        try {
            ServerSocket socket = new ServerSocket(serverPort);
            result = false;
            socket.close();
        } catch (Exception e) {
            //绑定端口失败...
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.warn("@@@ Current port: {} bind failure, retry again.", serverPort);
            }
        }
        return result;
    }
}
