package com.onenet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@SpringBootApplication
public class MqttControlApplication extends WebMvcConfigurationSupport {
		@Override
		protected void addResourceHandlers(ResourceHandlerRegistry registry) {
			//配置静态资源（js/css/html等）路径
			registry.addResourceHandler("/static/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX+"/static/");
			registry.addResourceHandler("/hplus/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX+"/hplus/");
			super.addResourceHandlers(registry);
		}
	public static void main(String[] args) {
		SpringApplication.run(MqttControlApplication.class, args);
	}

}
