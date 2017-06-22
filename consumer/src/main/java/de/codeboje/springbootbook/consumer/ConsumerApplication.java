package de.codeboje.springbootbook.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.codeboje.springbootbook.commons.CommentstoreObjectMapper;

@SpringBootApplication
@EnableRetry
public class ConsumerApplication {

	@Bean
	@Primary
	ObjectMapper objectMapper() {
		return new CommentstoreObjectMapper();
	}

	@Bean
	RestTemplate restTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add(0, new BasicAuthorizationInterceptor("admin", "mypassword"));
		return restTemplate;
	}

	@Bean
	ClientHttpRequestInterceptor clientHttpRequestInterceptor(@Value("${commentstore.auth.user}") String username, @Value("${commentstore.auth.password}") String password ) {
		return new BasicAuthorizationInterceptor(username, password);
	}

	@Bean
	CommentService commentService() {
		return new CommentService();
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ConsumerApplication.class, args);
	}

}
