package com.ewadus.newsapi.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.ewadus.newsapi.databinding.FragmentArticleBinding
import com.ewadus.newsapi.network.db.ArticleDatabase
import com.ewadus.newsapi.repo.NewsRepository
import com.ewadus.newsapi.ui.viewmodel.news.NewsViewModel
import com.ewadus.newsapi.ui.viewmodel.news.NewsViewModelProviderFactory
import com.google.android.material.snackbar.Snackbar


class ArticleFragment : Fragment() {

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: NewsViewModel

    private val args : ArticleFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(NewsViewModel::class.java)

        val article = args.article
        binding.webView.apply {
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }

        binding.favBtn.setOnClickListener {
            viewModel.saveNews(article)
            Snackbar.make(requireView(),"Article was saved",Snackbar.LENGTH_SHORT).show()

        }

        return binding.root
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}