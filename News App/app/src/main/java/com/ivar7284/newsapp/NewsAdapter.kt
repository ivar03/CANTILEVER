package com.ivar7284.newsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ivar7284.newsapp.models.Article
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewsAdapter(private val onItemClick: (Article) -> Unit) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private var articles = listOf<Article>()

    fun updateArticles(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount() = articles.size

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tvDescription)
        private val sourceTextView: TextView = itemView.findViewById(R.id.tvSource)
        private val timeTextView: TextView = itemView.findViewById(R.id.tvTime)
        private val imageView: ImageView = itemView.findViewById(R.id.ivArticleImage)

        fun bind(article: Article) {
            titleTextView.text = article.title
            descriptionTextView.text = article.description ?: "No description available"
            sourceTextView.text = article.source.name

            // Format time - GNews API uses ISO 8601 format
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            try {
                val date = inputFormat.parse(article.publishedAt)
                timeTextView.text = outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                timeTextView.text = "Unknown"
            }

            // Load image using the 'image' field instead of 'urlToImage'
            Glide.with(itemView.context)
                .load(article.image)
                .placeholder(R.drawable.ic_placeholder)
                .into(imageView)

            itemView.setOnClickListener {
                onItemClick(article)
            }
        }
    }
}