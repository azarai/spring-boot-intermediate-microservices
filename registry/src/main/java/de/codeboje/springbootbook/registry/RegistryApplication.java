package de.codeboje.springbootbook.registry;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class RegistryApplication {
	public static void main(String[] args) {
		new SpringApplicationBuilder(RegistryApplication.class).web(WebApplicationType.SERVLET).run(args);
	}
}
