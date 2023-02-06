package com.sap.cap.esmapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@PropertySources( 
{ @PropertySource("classpath:application.properties") }) 
@EnableAspectJAutoProxy
public class Application 
{

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
