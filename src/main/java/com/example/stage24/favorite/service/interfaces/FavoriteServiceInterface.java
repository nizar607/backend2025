package com.example.stage24.favorite.service.interfaces;

import com.example.stage24.favorite.domain.Favorite;
import com.example.stage24.favorite.model.request.NewFavorite;
import com.example.stage24.favorite.model.response.ArticleInfo;
import com.example.stage24.favorite.model.response.ResponseFavorite;

import java.util.List;
import java.util.Optional;

public interface FavoriteServiceInterface {

    public Favorite addFavorite(NewFavorite favorite);
    
    public Favorite updateFavorite(Long id, NewFavorite favorite);

    public void deleteFavorite(Long id);

    public Optional<Favorite> getFavorite(Long id);

    public Optional<ResponseFavorite> getFavoriteResponse(Long id);

    public List<Favorite> getAllFavorites();

    public List<ResponseFavorite> getAllFavoritesResponse();

    public Optional<List<ResponseFavorite>> getFavoritesByArticleId(Long articleId);

    public Optional<List<ResponseFavorite>> getFavoritesByUserId();
    
    public Optional<List<ArticleInfo>> getFavoriteArticlesByUser();

    public Optional<Boolean> isFavorite(Long articleId);
    
    public void deleteFavoriteByArticleId(Long articleId);

    public int getFavoriteCountByUser();
}