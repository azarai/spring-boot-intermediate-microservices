package de.codeboje.springbootbook.consumer;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.Instant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.codeboje.springbootbook.commons.CommentDTO;

@RunWith(SpringRunner.class)
@RestClientTest(CommentService.class)
public class ConsumerServiceTest {

	@Value("${commentstore.endpoint}")
	private String endpoint;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
    private MockRestServiceServer server;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	public void whenGetComments_thenReturnEmpty() {
		
		final String productId = "product4712";
		this.server.expect(requestTo(endpoint + "/list/" + productId))
        .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
		
		CommentDTO[] comments = commentService.getComments(productId);
		
		assertEquals(0, comments.length);
 
	}
	
	@Test
	public void whenGetComments_thenReturnOne() throws JsonProcessingException {
		
		final String productId = "product4713";
		CommentDTO mockComment = new CommentDTO();
		mockComment.setComment("Test commnent");
		mockComment.setCreated(Instant.now());
		mockComment.setEmailAddress("me@me.com");
		mockComment.setId("12345");
		mockComment.setUsername("reader");
		
		final CommentDTO[] mockComments = new CommentDTO[1];
		mockComments[0]= mockComment;
		
		this.server.expect(requestTo(endpoint + "/list/" + productId))
        .andRespond(withSuccess(objectMapper.writeValueAsString(mockComments), MediaType.APPLICATION_JSON));
		
		CommentDTO[] comments = commentService.getComments(productId);
		
		assertEquals(1, comments.length);
		assertEquals(mockComment.getEmailAddress(), comments[0].getEmailAddress());
		assertEquals(mockComment.getComment(), comments[0].getComment());
		assertEquals(mockComment.getUsername(), comments[0].getUsername());
		assertEquals(mockComment.getCreated(), comments[0].getCreated());
		assertEquals(mockComment.getId(), comments[0].getId());
	}
}
