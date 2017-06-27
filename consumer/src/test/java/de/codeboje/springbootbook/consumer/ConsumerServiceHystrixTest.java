package de.codeboje.springbootbook.consumer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.codeboje.springbootbook.commons.CommentDTO;
import de.codeboje.springbootbook.commons.CommentstoreObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=ConsumerServiceHystrixTest.TestConfig.class)
public class ConsumerServiceHystrixTest {

	@Configuration
	@EnableAutoConfiguration
	@EnableCircuitBreaker
	public static class TestConfig {

		@Bean
		@Primary
		ObjectMapper objectMapper() {
			return new CommentstoreObjectMapper();
		}

		@Bean
		@LoadBalanced
		RestTemplate restTemplate(@Value("${commentstore.auth.user}") String username,
				@Value("${commentstore.auth.password}") String password, RestTemplateBuilder restTemplateBuilder) {
			final RestTemplate restTemplate = restTemplateBuilder.build();
			restTemplate.getInterceptors().add(0, new BasicAuthorizationInterceptor(username, password));
			return restTemplate;
		}

		@Bean
		CommentService commentService() {
			return new CommentService();
		}
	}
	
	@Autowired
	private CommentService commentService;

	@Test
	public void whenGetComments_thenReturnError() {

		final String productId = "product4712";

		CommentDTO[] comments = commentService.getComments(productId);

		assertEquals(0, comments.length);
	}
}
