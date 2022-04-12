package com.flink.streaming.web.common.util;

import com.flink.streaming.web.exceptions.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.*;
import java.util.Enumeration;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2018/7/19
 * @time 下午6:18
 */
@Slf4j
public class IpUtil {

    private static String ip;

    private static IpUtil ipUtil = new IpUtil();

    private IpUtil() {
        ip = getCurrentSystemIp();
    }

    public static IpUtil getInstance() {
        return ipUtil;
    }


    /**
     * 获取本机的ip地址
     */
    public String getLocalIP() {
        if (StringUtils.isEmpty(ip)) {
            return getCurrentSystemIp();
        }
        return ip;
    }

    /**
     * @description 获取系统环境ip
     */
    private String getCurrentSystemIp() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                Enumeration<InetAddress> niAs = ni.getInetAddresses();
                while (niAs.hasMoreElements()) {
                    InetAddress ia = niAs.nextElement();
                    if (!ia.isLinkLocalAddress() && !ia.isLoopbackAddress() && ia instanceof Inet4Address) {
                        return ia.toString().substring(1);
                    }
                }
            }
        } catch (SocketException e) {
            log.error("Failed to get current system Ip ", e);
        }
        return null;
    }

    public static String getHostName() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostName(); //获取本机计算机名称
        } catch (Exception e) {
            log.error("getHostName is error", e);
            throw new BizException(e.getMessage());
        }


    }

    public static void main(String[] args) {
        System.out.println(IpUtil.getInstance().getLocalIP());
        System.out.println(IpUtil.getInstance().getLocalIP());
        System.out.println(getHostName());
        System.out.println(System.getProperty("user.name"));
    }
}
