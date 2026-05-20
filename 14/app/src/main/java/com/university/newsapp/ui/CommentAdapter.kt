package com.university.newsapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.university.newsapp.R
import com.university.newsapp.model.Comment

class CommentAdapter : ListAdapter<Comment, CommentAdapter.CommentViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.comment_name)
        private val emailText: TextView = itemView.findViewById(R.id.comment_email)
        private val bodyText: TextView = itemView.findViewById(R.id.comment_body)

        fun bind(comment: Comment) {
            nameText.text = comment.name
            emailText.text = comment.email
            bodyText.text = comment.body
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment) = oldItem == newItem
    }
}
