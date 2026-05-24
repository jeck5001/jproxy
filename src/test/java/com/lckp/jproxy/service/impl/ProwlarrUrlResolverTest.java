package com.lckp.jproxy.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.lckp.jproxy.constant.SystemConfigKey;
import com.lckp.jproxy.exception.SystemConfigException;
import com.lckp.jproxy.service.ISystemConfigService;

class ProwlarrUrlResolverTest {

	@AfterEach
	void resetRequestContext() {
		RequestContextHolder.resetRequestAttributes();
	}

	@Test
	void resolveUsesConfiguredProwlarrUrlWhenValid() {
		ISystemConfigService systemConfigService = mock(ISystemConfigService.class);
		when(systemConfigService.queryValueByKey(SystemConfigKey.PROWLARR_URL))
				.thenReturn("http://192.168.5.35:9696");

		String url = ProwlarrUrlResolver.resolve(systemConfigService);

		assertThat(url).isEqualTo("http://192.168.5.35:9696");
	}

	@Test
	void resolveFallsBackToCurrentRequestHostWhenProwlarrUrlIsInvalid() {
		ISystemConfigService systemConfigService = mock(ISystemConfigService.class);
		when(systemConfigService.queryValueByKey(SystemConfigKey.PROWLARR_URL))
				.thenThrow(new SystemConfigException("system.config.invalid.prowlarrUrl"));
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServerName("192.168.5.35");
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		String url = ProwlarrUrlResolver.resolve(systemConfigService);

		assertThat(url).isEqualTo("http://192.168.5.35:9696");
	}
}
