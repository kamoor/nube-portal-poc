package com.nube.server;

import java.util.Arrays;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.MultiPartConfigFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.nube.core.util.jvm.JVMStatus;

/**
 * Spring boot to start server on the fly and run an app.
 * Parameters:
 * --debug to start in debug mode
 *    
 * @author kamoorr
 * 
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@PropertySource(value =	{"classpath:nube-core.properties","classpath:application.properties","classpath:nube-portal.properties","classpath:admin.properties"})
@ImportResource(value = { "classpath*:spring/*.xml" })
public class NubeServer {
	
	
	

	public static void main(String[] args) {
		System.out.println("Start Spring Application: nube-portal");
		ApplicationContext ctx = SpringApplication.run(NubeServer.class, args);
		//printBeans(ctx);
		JVMStatus.printJVMStatus();
	}

	/**
	 * Print all the beans
	 * @param ctx
	 */
	private static void printBeans(ApplicationContext ctx) {
		String[] beanNames = ctx.getBeanDefinitionNames();
		Arrays.sort(beanNames);
		for (String beanName : beanNames) {
				System.out.println("Bean Found: " + beanName);
			
		}
	}
	
	/**
	 * Create a MultiPart config to upload file
	 * @return
	 */
	@Bean
    MultipartConfigElement multipartConfigElement() {
        MultiPartConfigFactory factory = new MultiPartConfigFactory();
        factory.setMaxFileSize("5MB");
        factory.setMaxRequestSize("15MB");
        return factory.createMultipartConfig();
    }
	
	/**
	 * Define a server. Using tomcat or jetty will be out of the box
	 * 
	 * @return
	 */
	// @Bean
	// public EmbeddedServletContainerFactory servletContainer() {
	// System.out.println("Reconfiguring servlet container to tomcat");
	// TomcatEmbeddedServletContainerFactory factory = new
	// TomcatEmbeddedServletContainerFactory();
	// factory.setPort(9000);
	// factory.setBaseDirectory(new File("target")); // look for work folder
	// factory.setContextPath("/api");
	//
	// factory.setSessionTimeout(30, TimeUnit.MINUTES);
	// factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND,
	// "/error_404.html"));
	//
	// //System.out.println("Base path"+ factory.getB.getAbsolutePath());
	//
	// return factory;
	// }

}
