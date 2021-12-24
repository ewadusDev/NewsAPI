package com.ewadus.newsapi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ewadus.newsapi.R
import com.ewadus.newsapi.network.model.Article
import kotlinx.android.synthetic.main.item_preview_article.view.*
import kotlinx.android.synthetic.main.item_preview_article.view.btn_read
import kotlinx.android.synthetic.main.item_preview_article.view.img_article
import kotlinx.android.synthetic.main.item_preview_article.view.img_share
import kotlinx.android.synthetic.main.item_preview_article.view.touch_area
import kotlinx.android.synthetic.main.item_preview_article.view.tvDescription
import kotlinx.android.synthetic.main.item_preview_article.view.tvPublishedAt
import kotlinx.android.synthetic.main.item_preview_article.view.tvSource
import kotlinx.android.synthetic.main.item_preview_article.view.tvTitle
import kotlinx.android.synthetic.main.item_preview_save.view.*

class SavesAdapter: RecyclerView.Adapter<SavesAdapter.SaveHolder>() {

    class SaveHolder(itemview: View):RecyclerView.ViewHolder(itemview)

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveHolder {
        return SaveHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_preview_save,parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SaveHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(img_article)
            tvSource.text = article.source?.name
            tvDescription.text = article.description
            tvTitle.text = article.title
            tvPublishedAt.text = article.publishedAt
        }

        holder.itemView.touch_area.setOnClickListener {
            onItemClickListener?.let {
                it(article)
            }
        }

        holder.itemView.btn_read.setOnClickListener {
            onItemClickListener?.let {
                it(article)
            }
        }

        holder.itemView.img_share.setOnClickListener {
            onItemShareListener?.let {
                it(article)
            }
        }
    }



    private var onItemClickListener: ((Article) -> Unit)? = null
    private var onItemShareListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnItemShareListener(listener: (Article) -> Unit ) {
        onItemShareListener = listener
    }

}