package com.demo.sdk.util;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class RequestUtils {
	
	/**
	 * 
	 * 获取请求内容
	 * 
	 * @author caiLinFeng
	 * @date 2018年2月6日
	 * @param
	 */
	public static String getRequestContent(HttpServletRequest request) {
		ServletInputStream is = null;
		String strcont = null;
		try {
			is = request.getInputStream();
			StringBuilder content = new StringBuilder();
			byte[] b = new byte[1024];
			int lens = -1;
			while ((lens = is.read(b)) > 0) {
				content.append(new String(b, 0, lens));
			}
			strcont = content.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return strcont;
	}

	/**
	 * 获取客户端真实ip
	 * 
	 * @author caiLinFeng
	 * @date 2018年2月6日
	 * @param
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ipAddress = request.getHeader("feign-request-ip");
		if(ipAddress == null  || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)){
			ipAddress = request.getHeader("x-forwarded-for");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ipAddress = inet.getHostAddress();
			}
		}
		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15) {
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}
		return ipAddress;
	}

}
