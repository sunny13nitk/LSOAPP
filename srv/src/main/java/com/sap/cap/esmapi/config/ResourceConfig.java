package com.sap.cap.esmapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceConfig implements WebMvcConfigurer
{

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/images/**", "/css/**", "/static/**")
                .addResourceLocations("classpath:/images/", "classpath:/static/css/", "classpath:/static/");
    }

}
