package com.sap.cap.esmapi.config;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.CsvToBeanBuilder;
import com.sap.cap.esmapi.catg.pojos.TY_CatgCus;
import com.sap.cap.esmapi.catg.pojos.TY_CatgCusItem;
import com.sap.cap.esmapi.catg.pojos.TY_CatgTemplates;
import com.sap.cap.esmapi.catg.pojos.TY_CatgTemplatesCus;
import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.vhelps.cus.TY_VHelpsRoot;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class AppInitializrConfig
{
    private final String configPath = "/configCatg/config.csv";
    private final String configCatgTemplates = "/configCatg/templates.csv";
    private final String configPathVHelpsJSON = "/vhelps/VHelps.json";

    @Autowired
    private MessageSource msgSrc;

    @Bean
    public TY_CatgCus loadCaseTypes4mConfig()
    {
        TY_CatgCus caseCus = null;

        try
        {

            ClassPathResource classPathResource = new ClassPathResource(configPath);
            if (classPathResource != null)
            {
                Reader reader = new InputStreamReader(classPathResource.getInputStream());
                if (reader != null)
                {
                    log.info("Resource Bound... ");
                    List<TY_CatgCusItem> configs = new CsvToBeanBuilder(reader).withSkipLines(1)
                            .withType(TY_CatgCusItem.class).build().parse();

                    if (!CollectionUtils.isEmpty(configs))
                    {
                        log.info("Entries in Config Found for Case Types Config. : " + configs.size());
                        caseCus = new TY_CatgCus(configs);
                    }
                }
            }

        }
        catch (Exception e)
        {
            throw new EX_ESMAPI(msgSrc.getMessage("ERR_CASETYPE_CFG", new Object[]
            { configPath, e.getLocalizedMessage() }, Locale.ENGLISH));
        }

        return caseCus;
    }

    @Bean
    public TY_CatgTemplatesCus loadTemplatesCatg4mConfig()
    {
        TY_CatgTemplatesCus catgTempCus = null;

        try
        {

            ClassPathResource classPathResource = new ClassPathResource(configCatgTemplates);
            if (classPathResource != null)
            {
                Reader reader = new InputStreamReader(classPathResource.getInputStream());
                if (reader != null)
                {
                    log.info("Resource Bound... ");
                    List<TY_CatgTemplates> configs = new CsvToBeanBuilder(reader).withSkipLines(1)
                            .withType(TY_CatgTemplates.class).build().parse();

                    if (!CollectionUtils.isEmpty(configs))
                    {
                        log.info("Entries in Config. Found for Case Categories and Templates: " + configs.size());
                        catgTempCus = new TY_CatgTemplatesCus(configs);
                    }
                }
            }

        }
        catch (Exception e)
        {
            throw new EX_ESMAPI(msgSrc.getMessage("ERR_CASETYPE_CFG", new Object[]
            { configPath, e.getLocalizedMessage() }, Locale.ENGLISH));
        }

        return catgTempCus;
    }

    @Bean
    public TY_VHelpsRoot loadLOBVHelpsCus()
    {
        TY_VHelpsRoot vHelpCus = null;

        try
        {
            ObjectMapper om = new ObjectMapper();

            ClassPathResource classPathResource = new ClassPathResource(configPathVHelpsJSON);
            if (classPathResource != null && om != null)
            {
                Reader reader = new InputStreamReader(classPathResource.getInputStream());
                if (reader != null)
                {

                    vHelpCus = om.readValue(reader, TY_VHelpsRoot.class);
                    log.info("LOB Custom Fields Customization Loaded!");

                }

            }
        }
        catch (Exception e)
        {
            log.error("LOB Custom Fields Customization Could not be Loaded!");
        }
        return vHelpCus;
    }

}
