package com.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.domain.Article;

public interface BlogRepository extends JpaRepository<Article, Long> {

}
