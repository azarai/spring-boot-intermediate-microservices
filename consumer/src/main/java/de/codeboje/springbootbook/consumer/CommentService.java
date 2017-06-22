package de.codeboje.springbootbook.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import de.codeboje.springbootbook.commons.CommentDTO;

@Service
public class CommentService {

	@Value("${commentstore.endpoint}")
	private String endpoint;

	@Autowired
	private RestTemplate restTemplate;

	public CommentDTO[] getComments(String productId) {
		CommentDTO[] response = restTemplate.getForObject(endpoint + "/list/" + productId,
				new CommentDTO[0].getClass());

		return response;
	}

	public String postComment(CommentForm comment) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("emailAddress", comment.getEmailAddress());
		map.add("comment", comment.getEmailAddress());
		map.add("pageId", comment.getProductId());
		map.add("username", comment.getUsername());
		;
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<String> response = restTemplate.postForEntity( endpoint + "/create/", request , String.class );
		
		return response.getBody();
	}
}