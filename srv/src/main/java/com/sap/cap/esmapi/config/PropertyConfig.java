package com.sap.cap.esmapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.sap.cap.esmapi.utilities.pojos.TY_RLConfig;
import com.sap.cap.esmapi.utilities.pojos.TY_SrvCloudUrls;

@Configuration
@PropertySources(
{ @PropertySource("classpath:messages.properties"), @PropertySource("classpath:srvcloudurls.properties"),
		@PropertySource("classpath:appconfig.properties") })

public class PropertyConfig
{

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties()
	{
		PropertySourcesPlaceholderConfigurer pSConf = new PropertySourcesPlaceholderConfigurer();
		return pSConf;
	}

	@Bean
	public ResourceBundleMessageSource messageSource()
	{

		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.addBasenames("messages");
		source.setUseCodeAsDefaultMessage(true);

		return source;
	}

	@Bean
	@Autowired // For PropertySourcesPlaceholderConfigurer
	public TY_SrvCloudUrls SrvCloudUrls(@Value("${username}") final String userName,
			@Value("${password}") final String password, @Value("${casesurl}") final String casesUrl,
			@Value("${cpurl}") final String cpUrl, @Value("${accountsurl}") final String acUrl,
			@Value("${notesurl}") final String notesUrl, @Value("${topN}") final String topN,
			@Value("${caseTemplateUrl}") final String caseTemplateUrl,
			@Value("${catgTreeUrl}") final String catgTreeUrl, @Value("${docSrvUrl}") final String docSrvUrl,
			@Value("${emplSrvUrl}") final String emplSrvUrl, @Value("${vhlpUrl}") final String vhlpUrl,
			@Value("${caseDetailsUrl}") final String caseDetailsUrl

	)

	{
		TY_SrvCloudUrls srvClUrls = new TY_SrvCloudUrls(userName, password, casesUrl, cpUrl, acUrl, notesUrl, topN,
				caseTemplateUrl, catgTreeUrl, docSrvUrl, emplSrvUrl, vhlpUrl, caseDetailsUrl);

		return srvClUrls;
	}

	@Bean
	@Autowired // For PropertySourcesPlaceholderConfigurer
	public TY_RLConfig RatelimitConfigLoad(@Value("${numFormSubms}") final int numFormSubms,
			@Value("${intvSecs}") final long intvSecs, @Value("${allowedAttachments}") final String allowedAttachments)
	{
		TY_RLConfig rlConfig = new TY_RLConfig(numFormSubms, intvSecs, allowedAttachments);
		return rlConfig;
	}

}