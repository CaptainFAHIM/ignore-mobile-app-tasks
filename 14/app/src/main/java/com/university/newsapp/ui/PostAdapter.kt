package com.university.newsapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.university.newsapp.R
import com.university.newsapp.model.Post

class PostAdapter(
    private val onClick: (Post) -> Unit
) : ListAdapter<Post, PostAdapter.PostViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PostViewHolder(
        itemView: View,
        private val onClick: (Post) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.post_title)
        private val bodyText: TextView = itemView.findViewById(R.id.post_body)
        private val userBadge: TextView = itemView.findViewById(R.id.user_badge)
        private val postIdText: TextView = itemView.findViewById(R.id.post_id)

        fun bind(post: Post) {
            titleText.text = post.title
            bodyText.text = post.body
            userBadge.text = "User ${post.userId}"
            postIdText.text = "Post #${post.id}"
            itemView.setOnClickListener { onClick(post) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
    }
}
