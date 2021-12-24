package com.ewadus.newsapi.repo

import com.ewadus.newsapi.network.api.RetrofitNews
import com.ewadus.newsapi.network.db.ArticleDatabase
import com.ewadus.newsapi.network.model.Article
import com.ewadus.newsapi.network.model.NewsResponse
import retrofit2.Response

class NewsRepository(val database: ArticleDatabase) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) : Response<NewsResponse> {
       return RetrofitNews.newsApi.getBreakingNews(countryCode, pageNumber)
    }

    suspend fun searchNews(inputSearch: String, pageNumber: Int) : Response<NewsResponse> {
        return RetrofitNews.newsApi.searchEverything(inputSearch, pageNumber)
    }

    suspend fun insert(article: Article) : Long {
       return database.getArticleDao().insert(article)
    }

    fun getSaveNews() = database.getArticleDao().getAllArticle()


    suspend fun delete(article: Article) {
        return  database.getArticleDao().deleteArticle(article)
    }
  }