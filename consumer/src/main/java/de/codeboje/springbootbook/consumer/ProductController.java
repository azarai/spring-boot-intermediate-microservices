package de.codeboje.springbootbook.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import de.codeboje.springbootbook.commons.CommentDTO;

@Controller
public class ProductController {

	class Product {
		private String id;
		private String title;
		private String description;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
	}
	
	@Autowired
	private CommentService commentService;
	
	private Product getDummyProduct() {
		final Product p = new Product();
		p.setId("product4711");
		p.setTitle("Product 4711 - Title");
		p.setDescription("Product 4711 is the greatest product in the world.");
		return p;
	}
	
	@RequestMapping("/")
	public String home(Model model) {
		final Product product = getDummyProduct();
		
		final CommentDTO[] comments = commentService.getComments(product.getId());
		
		model.addAttribute("product", product);
		model.addAttribute("comments", comments);
		model.addAttribute("newcomment", new CommentForm());
		return "index";
	}
	
	@PostMapping("/postComment")
	public String commentSubmit(@ModelAttribute CommentForm comment) {
		commentService.postComment(comment);
		return "redirect:/";
	}
}
