package com.ewadus.newsapi.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ewadus.newsapi.R
import com.ewadus.newsapi.adapter.NewsAdapter
import com.ewadus.newsapi.databinding.FragmentFeedsBinding
import com.ewadus.newsapi.network.db.ArticleDatabase
import com.ewadus.newsapi.repo.NewsRepository
import com.ewadus.newsapi.ui.viewmodel.news.NewsViewModel
import com.ewadus.newsapi.ui.viewmodel.news.NewsViewModelProviderFactory
import com.ewadus.newsapi.util.Resource

class FeedsFragment : Fragment() {

    private var _binding: FragmentFeedsBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: NewsViewModel
    private val newsAdapter = NewsAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFeedsBinding.inflate(inflater, container, false)

        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        val navController = NavHostFragment.findNavController(this)
        val entry = navController.getBackStackEntry(R.id.feedsFragment)
        viewModel =
            ViewModelProvider(entry, viewModelProviderFactory).get(NewsViewModel::class.java)

        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_feedsFragment_to_articleFragment, bundle)
//
        }

        newsAdapter.saveItemOnClickListener {
            viewModel.saveNews(it)
            Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
        }

        newsAdapter.shareItemOnCLickListener {
            val sendIntent: Intent = Intent().apply {
                this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT, it.url)
                this.type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }



        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPage = newsResponse.totalResults / 20 + 2
                        isLastPage = viewModel.breakingNewsPage == totalPage

                        if (isLastPage) {
                            binding.recyclerviewNewsFeed.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e("FeedsFragment", "Failed get data: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })


        binding.searchIcon.setOnClickListener {
            findNavController().navigate(R.id.action_feedsFragment_to_searchFragment)
        }




        return binding.root
    }


    private fun hideProgressBar() {
        binding.progressbar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.progressbar.visibility = View.VISIBLE
        isLoading = true

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
                viewModel.getBreakingNews("th")
                isScrolling = false
            }
        }
    }


    private fun setupRecyclerView() {
        binding.recyclerviewNewsFeed.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@FeedsFragment.scrollListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}