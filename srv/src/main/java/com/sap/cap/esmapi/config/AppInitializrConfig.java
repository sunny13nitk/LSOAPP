package com.sap.cap.esmapi.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Locale;

import com.opencsv.bean.CsvToBeanBuilder;
import com.sap.cap.esmapi.catg.pojos.TY_CatgCus;
import com.sap.cap.esmapi.catg.pojos.TY_CatgCusItem;
import com.sap.cap.esmapi.exceptions.EX_ESMAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;

@Configuration
public class AppInitializrConfig 
{
    private final String configPath = "/configCatg/config.csv";

    @Autowired
    private MessageSource msgSrc;

    @Bean
    public TY_CatgCus loadCaseTypes4mConfig()
    {
        TY_CatgCus caseCus = null;

        try
        {

            ClassPathResource classPathResource = new ClassPathResource(configPath);
            if(classPathResource != null)
            {
                Reader reader = new InputStreamReader(classPathResource.getInputStream());
                if (reader != null)
				{
                    System.out.println("Resource Bound... ");
                    List<TY_CatgCusItem> configs = new CsvToBeanBuilder(reader).withSkipLines(1)
						        .withType(TY_CatgCusItem.class).build().parse();
						
						if (!CollectionUtils.isEmpty(configs))
						{
                            System.out.println("Entries in Config Found : " + configs.size());
							caseCus = new TY_CatgCus(configs);
						}
                }
            }

        }
        catch (Exception e)
			{
                throw new EX_ESMAPI(msgSrc.getMessage("ERR_CASETYPE_CFG", new Object[]{configPath, e.getLocalizedMessage()}, Locale.ENGLISH));
			}
         		
        


        return caseCus;
    }

}
