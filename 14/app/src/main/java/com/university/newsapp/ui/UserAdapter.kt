package com.university.newsapp.ui

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.university.newsapp.R
import com.university.newsapp.model.User

class UserAdapter(
    private val onClick: (User) -> Unit
) : ListAdapter<User, UserAdapter.UserViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UserViewHolder(
        itemView: View,
        private val onClick: (User) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val avatarText: TextView = itemView.findViewById(R.id.user_avatar)
        private val nameText: TextView = itemView.findViewById(R.id.user_name)
        private val usernameText: TextView = itemView.findViewById(R.id.user_username)
        private val emailText: TextView = itemView.findViewById(R.id.user_email)

        fun bind(user: User) {
            avatarText.text = initialsFor(user.name)
            avatarText.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
            val avatarBackground = avatarText.background as GradientDrawable
            avatarBackground.setColor(avatarColorFor(user.name))
            nameText.text = user.name
            usernameText.text = "@${user.username}"
            emailText.text = user.email
            itemView.setOnClickListener { onClick(user) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
    }
}
