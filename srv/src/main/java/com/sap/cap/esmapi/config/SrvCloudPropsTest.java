package com.sap.cap.esmapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import com.sap.cap.esmapi.utilities.constants.GC_Constants;
import com.sap.cap.esmapi.utilities.pojos.TY_SrvCloudUrls;

@Configuration
@Profile(GC_Constants.gc_TESTProfile)
@PropertySources(
{ @PropertySource("classpath:srvcloudurls-test.properties") })
public class SrvCloudPropsTest
{
    @Bean
    @Autowired // For PropertySourcesPlaceholderConfigurer
    @Profile(GC_Constants.gc_TESTProfile)
    public TY_SrvCloudUrls SrvCloudUrls(@Value("${username}") final String userName,
            @Value("${password}") final String password, @Value("${casesurl}") final String casesUrl,
            @Value("${cpurl}") final String cpUrl, @Value("${accountsurl}") final String acUrl,
            @Value("${notesurl}") final String notesUrl, @Value("${topN}") final String topN,
            @Value("${caseTemplateUrl}") final String caseTemplateUrl,
            @Value("${catgTreeUrl}") final String catgTreeUrl, @Value("${docSrvUrl}") final String docSrvUrl,
            @Value("${emplSrvUrl}") final String emplSrvUrl, @Value("${vhlpUrl}") final String vhlpUrl,
            @Value("${caseDetailsUrl}") final String caseDetailsUrl, @Value("${statusCfgUrl}") final String statusCfgUrl

    )

    {
        TY_SrvCloudUrls srvClUrls = new TY_SrvCloudUrls(userName, password, casesUrl, cpUrl, acUrl, notesUrl, topN,
                caseTemplateUrl, catgTreeUrl, docSrvUrl, emplSrvUrl, vhlpUrl, caseDetailsUrl, statusCfgUrl);

        return srvClUrls;
    }

}
