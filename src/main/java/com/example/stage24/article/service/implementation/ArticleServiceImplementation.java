// package com.example.stage24.article.service.implementation;


// import com.example.stage24.article.domain.Article;
// import com.example.stage24.shared.SharedServiceInterface;
// import com.example.stage24.user.domain.User;
// import com.example.stage24.article.repository.ArticleRepository;
// import com.example.stage24.article.service.interfaces.ArticleServiceInterface;


// import lombok.AllArgsConstructor;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;
// import org.springframework.web.server.ResponseStatusException;
// import java.io.IOException;
// import java.util.Optional;
// import java.util.List;


// @Service
// @AllArgsConstructor
// public class ArticleServiceImplementation implements ArticleServiceInterface {

//     private ArticleRepository articleRepository;
//     private SharedServiceInterface sharedService;

//     @Override
//     public Article addArticle(Article article) {
//         return articleRepository.save(article);
//     }

//     @Override
//     public Article updateArticle(Article article) {
//         return articleRepository.save(article);
//     }

//     @Override
//     public void deleteArticle(Long id) {
//         articleRepository.deleteById(id);
//     }

//     @Override
//     public Optional<Article> getArticle(Long id) {
//         return articleRepository.findById(id);
//     }

//     @Override
//     public List<Article> getAllArticles() {
//         return articleRepository.findAll();
//     }




// }

