package com.ewadus.newsapi.ui.viewmodel.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ewadus.newsapi.network.model.Article
import com.ewadus.newsapi.network.model.NewsResponse
import com.ewadus.newsapi.repo.NewsRepository
import com.ewadus.newsapi.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(val newsRepository: NewsRepository) : ViewModel() {

    // Make it Livedata for observe at news feed fragment
    private var _breakingNews = MutableLiveData<Resource<NewsResponse>>()
    val breakingNews: LiveData<Resource<NewsResponse>> get() = _breakingNews

    // determine only at fist page
    var breakingNewsPage = 1

    // Make it Livedata for data of searching
    private var _searchDataNews = MutableLiveData<Resource<NewsResponse>>()
    val searchDataNews: LiveData<Resource<NewsResponse>> get() = _searchDataNews
    var searchPageNumber = 1

    // variable for paging preload data
    var breakingNewsResponse: NewsResponse? = null
    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("th")
    }

    //get breakingNews  on retrofit via  repository class
    fun getBreakingNews(countryCode: String) {
        viewModelScope.launch {
            _breakingNews.postValue(Resource.Loading())
            val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
            _breakingNews.postValue(handleBreakingNewsResponse(response))
        }
    }

    // check success / failed  of  retrofit response
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                }else {
                    // replace old response and add response form page2 to replace old response
                    val oldArticle = breakingNewsResponse?.articles
                    val newArticle = resultResponse.articles
                    if (newArticle != null) {
                        oldArticle?.addAll(newArticle)
                    }
                }
                return Resource.Success( breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    // search news
    fun searchNews(inputText: String) {
        viewModelScope.launch {
            _searchDataNews.postValue(Resource.Loading())
            val response = newsRepository.searchNews(inputText, searchPageNumber)
            _searchDataNews.postValue(handleSearchNewsResponse(response))
        }

    }

    // check success / failed  of  retrofit response search
    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchPageNumber++
                if ( searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                }else {

                    // replace old response and add response form page2 to replace old response
                    val oldResponse  = searchNewsResponse?.articles
                    val newResponse = resultResponse.articles
                    if (newResponse != null) {
                        oldResponse?.addAll(newResponse)
                    }
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


    fun saveNews(article: Article) {
        viewModelScope.launch {
            newsRepository.insert(article)
        }
    }

    fun getSaveNews() = newsRepository.getSaveNews()


    fun deleteArticle(article: Article) {
        viewModelScope.launch {
            newsRepository.delete(article)
        }
    }
}