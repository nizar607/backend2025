package com.example.stage24.basket.service.implementation;


import com.example.stage24.article.domain.Article;
import com.example.stage24.article.domain.Category;
import com.example.stage24.article.model.request.NewArticle;
import com.example.stage24.article.model.response.ResponseArticle;
import com.example.stage24.article.repository.CategoryRepository;
import com.example.stage24.article.service.interfaces.CategoryServiceInterface;
import com.example.stage24.basket.service.interfaces.ItemServiceInterface;
import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.article.repository.ArticleRepository;
import com.example.stage24.article.service.interfaces.ArticleServiceInterface;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedList;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImplementation implements ItemServiceInterface {




}

