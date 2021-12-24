package com.ewadus.newsapi.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ewadus.newsapi.R
import com.ewadus.newsapi.adapter.NewsAdapter
import com.ewadus.newsapi.databinding.FragmentSearchBinding
import com.ewadus.newsapi.network.db.ArticleDatabase
import com.ewadus.newsapi.repo.NewsRepository
import com.ewadus.newsapi.ui.viewmodel.news.NewsViewModel
import com.ewadus.newsapi.ui.viewmodel.news.NewsViewModelProviderFactory
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: NewsViewModel
    private val searchAdapter = NewsAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)
        setupRecyclerView()

        var job: Job? = null
        binding.editInput.addTextChangedListener {editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                editable?.let {
                    if (editable.toString().isNotEmpty()){
                        viewModel.searchNews(it.toString())
                    }
                }
            }
        }

        searchAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_searchFragment_to_articleFragment,bundle)
        }




        viewModel.searchDataNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is com.ewadus.newsapi.util.Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        searchAdapter.differ.submitList(it.articles)
                        val totalPage = it.totalResults / 20 + 2
                        isLastPage = viewModel.searchPageNumber == totalPage
                        if (isLastPage) {
                            binding.recyclerviewSearch.setPadding(0,0,0,0)
                        }
                    }
                }
                is com.ewadus.newsapi.util.Resource.Error -> {
                    hideProgressBar()
                    Log.e("SearchFragment", "Failed Loading ${response.message}")
                }
                is com.ewadus.newsapi.util.Resource.Loading -> {
                    showProgressBar()
                }
            }
        })


        return binding.root
    }

    private fun hideProgressBar() {
        binding.progressbar.visibility = View.INVISIBLE
    }
    private fun showProgressBar() {
        binding.progressbar.visibility = View.VISIBLE
    }

    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalItemMoreThanVisible = totalItemCount >= 20
            val shouldPaging =
                isNotLoadingAndNotLastPage && isLastItem && isNotAtBeginning && isTotalItemMoreThanVisible

            if (shouldPaging == true) {
                viewModel.searchNews(binding.editInput.toString())
                isScrolling = false
            }

        }
    }

    private fun setupRecyclerView() {
        binding.recyclerviewSearch.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(this@SearchFragment.scrollListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}