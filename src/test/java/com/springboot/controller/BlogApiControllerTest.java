package com.springboot.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.util.List;

import com.springboot.domain.User;
import com.springboot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.domain.Article;
import com.springboot.dto.AddArticleRequest;
import com.springboot.dto.UpdateArticleRequest;
import com.springboot.repository.BlogRepository;

@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest {

	@Autowired
	protected MockMvc mockMvc;
	
	@Autowired
	protected ObjectMapper objectMapper;
	
	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	BlogRepository blogRepository;
	
	@BeforeEach
	public void mockMvcSetUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
		blogRepository.deleteAll();
	}

	
	@Test
	@DisplayName("findAllArticles:블로그 글 목록 조회에 성공한다")
	public void findAllArticles() throws Exception{
		final String url = "/api/articles";

		Article savedArticle = createDefaultArticle();

		
		final ResultActions resultActions = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
		
		resultActions.andExpect(status().isOk()).andExpect(jsonPath("$[0].content").value(savedArticle.getContent())).andExpect(jsonPath("$[0].title").value(savedArticle.getTitle()));
	}
	
	@Test
	@DisplayName("findArticle:블로그 글 조회에 성공한다")
	public void findArticle() throws Exception{
		final String url="/api/articles/{id}";
		Article savedArticle = createDefaultArticle();

		final ResultActions resultActions = mockMvc.perform(get(url, savedArticle.getId()));
		
		resultActions.andExpect(status().isOk()).andExpect(jsonPath("$.content").value(savedArticle.getContent())).andExpect(jsonPath("$.title").value(savedArticle.getTitle()));
	}
	
	@Test
	@DisplayName("delete:블로그 글 삭제에 성공한다")
	public void deleteArticle() throws Exception{
		final String url="/api/articles/{id}";
		Article savedArticle = createDefaultArticle();

		mockMvc.perform(delete(url, savedArticle.getId())).andExpect(status().isOk());
		
		List<Article> articles=blogRepository.findAll();
		assertThat(articles).isEmpty();
	}
	
	@Test
	@DisplayName("update:블로그 글 수정에 성공한다")
	public void updateArticle() throws Exception{
		final String url = "/api/articles/{id}";
		Article savedArticle = createDefaultArticle();

		final String newTitle = "changed title";
		final String newContent = "changed content";
		
		UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);
		
		
		ResultActions resultAction = mockMvc.perform(put(url, savedArticle.getId()).contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(request)));
		
		resultAction.andExpect(status().isOk());
		
		Article article = blogRepository.findById(savedArticle.getId()).get();
		
		assertThat(article.getTitle()).isEqualTo(newTitle);
		assertThat(article.getContent()).isEqualTo(newContent);
		
	}

	@Autowired
	UserRepository userRepository;

	User user;

	@BeforeEach
	void setSecurityContext(){
		userRepository.deleteAll();
		user = userRepository.save(User.builder()
				.email("user@gmail.com")
				.password("test")
				.build());
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
	}

	@DisplayName("addArticle: 블로그 글 추가 성공")
	@Test
	public void addArticle() throws Exception{
		//given
		final String url="/api/articles";
		final String title = "title";
		final String content = "content";
		final AddArticleRequest userRequest = new AddArticleRequest(title, content);
		final String requestBody = objectMapper.writeValueAsString(userRequest);

		Principal principal = Mockito.mock(Principal.class);
		Mockito.when(principal.getName()).thenReturn("username");

		ResultActions result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_VALUE).principal(principal).content(requestBody));

		result.andExpect(status().isCreated());

		List<Article> articles = blogRepository.findAll();

		assertThat(articles.size()).isEqualTo(1);
		assertThat(articles.get(0).getTitle()).isEqualTo(title);
		assertThat(articles.get(0).getContent()).isEqualTo(content);
	}

	private Article createDefaultArticle() {
		return blogRepository.save(Article.builder()
				.title("title")
				.author(user.getUsername())
				.content("content")
				.build());
	}

}
