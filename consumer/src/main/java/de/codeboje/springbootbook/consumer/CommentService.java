package de.codeboje.springbootbook.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import de.codeboje.springbootbook.commons.CommentDTO;

@Service
@RibbonClient(name = "commentstore", configuration = RibbonConfig.class)
public class CommentService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommentService.class);

	private static final String ENDPOINT = "https://commentstore";

	@Autowired
	@LoadBalanced
	private RestTemplate restTemplate;

	@Retryable(maxAttempts=1)
	public CommentDTO[] getComments(String productId) {
		LOGGER.info("requesting comments for product {}", productId);
		CommentDTO[] response = restTemplate.getForObject(ENDPOINT + "/list/" + productId,
				new CommentDTO[0].getClass());

		return response;
	}
	
	@Recover
	public CommentDTO[] recover(Throwable e, String productId) {
		LOGGER.info("requesting comments for product {} failed, retries exceeded", productId);
		return new CommentDTO[0];
	}

	@Recover()
	public String recoverPost(Throwable e, CommentForm comment) {
		LOGGER.info("posting comments for product {} failed, retries exceeded", comment.getProductId());
		return "";
	}

	@Retryable(maxAttempts=5, backoff=@Backoff(delay=2000))
	public String postComment(CommentForm comment) {

		LOGGER.info("posting comments for product {}", comment.getProductId());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("emailAddress", comment.getEmailAddress());
		map.add("comment", comment.getComment());
		map.add("pageId", comment.getProductId());
		map.add("username", comment.getUsername());
		;
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(ENDPOINT + "/create/", request, String.class);

		return response.getBody();
	}
}