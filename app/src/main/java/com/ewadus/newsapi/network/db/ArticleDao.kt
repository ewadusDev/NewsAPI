package com.ewadus.newsapi.network.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ewadus.newsapi.network.model.Article


@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article): Long

    @Query("Select * From articles")
     fun getAllArticle(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}

