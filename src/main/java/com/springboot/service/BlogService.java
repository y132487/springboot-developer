package com.springboot.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.domain.Article;
import com.springboot.dto.AddArticleRequest;
import com.springboot.dto.UpdateArticleRequest;
import com.springboot.repository.BlogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlogService {
	
	private final BlogRepository blogRepository;
	
	public Article save(AddArticleRequest request) {
		return blogRepository.save(request.toEntity());
	}
	
	public List<Article> findAll(){
		return blogRepository.findAll();
	}
	
	public Article findById(long id) {
		return blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found: " + id));
	}
	
	public void delete(long id) {
		blogRepository.deleteById(id);
	}
	
	@Transactional
	public Article update(long id, UpdateArticleRequest request) {
		Article article = blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found:" + id));
		article.update(request.getTitle(), request.getContent());
		
		return article;
		
	}

}
