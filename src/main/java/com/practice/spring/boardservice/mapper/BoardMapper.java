package com.practice.spring.boardservice.mapper;

import com.practice.spring.boardservice.model.Article;
import com.practice.spring.boardservice.model.Paging;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {
    void saveArticle(Article article);
    List<Article> getArticles(Paging page);
    int getArticleCnt();
    Article getArticleById(long id);
}
