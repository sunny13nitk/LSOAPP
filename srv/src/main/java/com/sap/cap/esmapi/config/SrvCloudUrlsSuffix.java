package com.sap.cap.esmapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import com.sap.cap.esmapi.utilities.pojos.TY_SrvCloudUrlsSuffix;

@Configuration
@PropertySources(
{ @PropertySource("classpath:srvcloudurls.properties") })
public class SrvCloudUrlsSuffix
{

    @Bean
    @Autowired // For PropertySourcesPlaceholderConfigurer
    public TY_SrvCloudUrlsSuffix SrvCloudUrlsSuffixVals(@Value("${casesurl}") final String casesUrl,
            @Value("${cpurl}") final String cpUrl, @Value("${accountsurl}") final String acUrl,
            @Value("${notesurl}") final String notesUrl, @Value("${topN}") final String topN,
            @Value("${caseTemplateUrl}") final String caseTemplateUrl,
            @Value("${catgTreeUrl}") final String catgTreeUrl, @Value("${docSrvUrl}") final String docSrvUrl,
            @Value("${emplSrvUrl}") final String emplSrvUrl, @Value("${vhlpUrl}") final String vhlpUrl,
            @Value("${caseDetailsUrl}") final String caseDetailsUrl,
            @Value("${statusCfgUrl}") final String statusCfgUrl, @Value("${accByEmail}") final String accByEmailUrl,
            @Value("${conByEmail}") final String conByEmailUrl, @Value("${empById}") final String empByIdUrl,
            @Value("${casesByAcc}") final String casesByAccUrl, @Value("${casesByEmpl}") final String casesByEmplUrl,
            @Value("${customerurl}") final String customerUrl, @Value("${prevAtt}") final String prevAtt,
            @Value("${dlAtt}") final String dlAtt

    )

    {
        TY_SrvCloudUrlsSuffix srvClUrls = new TY_SrvCloudUrlsSuffix(casesUrl, cpUrl, acUrl, notesUrl, topN,
                caseTemplateUrl, catgTreeUrl, docSrvUrl, emplSrvUrl, vhlpUrl, caseDetailsUrl, statusCfgUrl,
                accByEmailUrl, conByEmailUrl, empByIdUrl, casesByAccUrl, casesByEmplUrl, customerUrl, prevAtt, dlAtt);

        return srvClUrls;
    }

}
