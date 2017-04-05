package com.neelesh.configuration;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component 
public class AppConfig {

/*	@Value("${key.value1}")
	private String value;

	@Value("${key.value1}")
	private String value;
	*/
	
	private static UrlConfig urlConfig ; 
	
	
	public AppConfig() { 
/*		Resource xmlResource = new FileSystemResource("configuration.xml");
//		System.out.println( xmlResource.toString() + " "  + xmlResource.getInputStream()) ; 
		BeanFactory factory = new XmlBeanFactory(xmlResource);
		urlConfig = (UrlConfig)factory.getBean("urlconfig");*/
	}
	
	
	

}