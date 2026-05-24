package com.lckp.jproxy.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.lckp.jproxy.constant.SystemConfigKey;
import com.lckp.jproxy.exception.SystemConfigException;
import com.lckp.jproxy.service.ISystemConfigService;

import jakarta.servlet.http.HttpServletRequest;

final class ProwlarrUrlResolver {

	private static final String DEFAULT_SCHEME = "http";
	private static final int DEFAULT_PROWLARR_PORT = 9696;

	private ProwlarrUrlResolver() {
	}

	static String resolve(ISystemConfigService systemConfigService) {
		try {
			String prowlarrUrl = systemConfigService.queryValueByKey(SystemConfigKey.PROWLARR_URL);
			if (StringUtils.isNotBlank(prowlarrUrl)) {
				return prowlarrUrl;
			}
		} catch (SystemConfigException e) {
			// Prowlarr is commonly deployed on the same NAS as JProxy.
		}
		return resolveFromCurrentRequest();
	}

	private static String resolveFromCurrentRequest() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		if (attributes == null) {
			throw new SystemConfigException("system.config.invalid." + SystemConfigKey.PROWLARR_URL);
		}
		HttpServletRequest request = attributes.getRequest();
		return DEFAULT_SCHEME + "://" + request.getServerName() + ":" + DEFAULT_PROWLARR_PORT;
	}
}
