package com.university.newsapp.ui

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.university.newsapp.R
import com.university.newsapp.model.Post
import com.university.newsapp.model.User
import com.university.newsapp.repository.PostRepository
import kotlinx.coroutines.launch
import java.io.IOException
import retrofit2.HttpException

class UserProfileActivity : AppCompatActivity() {
    private lateinit var profileProgress: ProgressBar
    private lateinit var profileErrorText: TextView
    private lateinit var profileRetryButton: Button
    private lateinit var profileContent: View
    private lateinit var avatarText: TextView
    private lateinit var nameText: TextView
    private lateinit var usernameText: TextView
    private lateinit var emailText: TextView
    private lateinit var phoneText: TextView
    private lateinit var websiteText: TextView
    private lateinit var companyText: TextView
    private lateinit var catchphraseText: TextView
    private lateinit var userPostsProgress: ProgressBar
    private lateinit var userPostsErrorText: TextView
    private lateinit var userPostsRetryButton: Button
    private lateinit var userPostsRecyclerView: RecyclerView
    private lateinit var postsAdapter: PostAdapter

    private var currentUserId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        currentUserId = intent.getIntExtra(EXTRA_USER_ID, 0)
        if (currentUserId == 0) {
            finish()
            return
        }

        bindViews()
        setupPostsList()
        profileRetryButton.setOnClickListener { loadUser() }
        userPostsRetryButton.setOnClickListener { loadUserPosts() }
        loadUser()
    }

    private fun bindViews() {
        profileProgress = findViewById(R.id.profile_progress)
        profileErrorText = findViewById(R.id.profile_error_text)
        profileRetryButton = findViewById(R.id.profile_retry_button)
        profileContent = findViewById(R.id.profile_content)
        avatarText = findViewById(R.id.profile_avatar)
        nameText = findViewById(R.id.profile_name)
        usernameText = findViewById(R.id.profile_username)
        emailText = findViewById(R.id.profile_email)
        phoneText = findViewById(R.id.profile_phone)
        websiteText = findViewById(R.id.profile_website)
        companyText = findViewById(R.id.profile_company)
        catchphraseText = findViewById(R.id.profile_catchphrase)
        userPostsProgress = findViewById(R.id.user_posts_progress)
        userPostsErrorText = findViewById(R.id.user_posts_error_text)
        userPostsRetryButton = findViewById(R.id.user_posts_retry_button)
        userPostsRecyclerView = findViewById(R.id.user_posts_recycler)
    }

    private fun setupPostsList() {
        postsAdapter = PostAdapter { post ->
            startActivity(
                Intent(this, PostDetailActivity::class.java)
                    .putExtra(PostDetailActivity.EXTRA_POST_ID, post.id)
            )
        }
        userPostsRecyclerView.layoutManager = LinearLayoutManager(this)
        userPostsRecyclerView.adapter = postsAdapter
    }

    private fun loadUser() {
        profileProgress.visibility = View.VISIBLE
        profileErrorText.visibility = View.GONE
        profileRetryButton.visibility = View.GONE
        profileContent.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val user = PostRepository.getUserById(currentUserId)
                showUser(user)
                profileContent.visibility = View.VISIBLE
                loadUserPosts()
            } catch (error: HttpException) {
                showProfileError("Server error: ${error.code()}")
            } catch (error: IOException) {
                showProfileError("Network error. Check your connection.")
            } catch (error: Exception) {
                showProfileError("Something went wrong: ${error.message.orEmpty()}")
            } finally {
                profileProgress.visibility = View.GONE
            }
        }
    }

    private fun showUser(user: User) {
        avatarText.text = initialsFor(user.name)
        val background = avatarText.background as GradientDrawable
        background.setColor(avatarColorFor(user.name))
        avatarText.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        nameText.text = user.name
        usernameText.text = "@${user.username}"
        emailText.text = user.email
        phoneText.text = user.phone
        websiteText.text = user.website
        companyText.text = user.company.name
        catchphraseText.text = user.company.catchPhrase
    }

    private fun loadUserPosts() {
        userPostsProgress.visibility = View.VISIBLE
        userPostsErrorText.visibility = View.GONE
        userPostsRetryButton.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val posts = PostRepository.getPostsByUser(currentUserId)
                postsAdapter.submitList(posts)
                userPostsRecyclerView.visibility = View.VISIBLE
            } catch (error: HttpException) {
                showUserPostsError("Server error: ${error.code()}")
            } catch (error: IOException) {
                showUserPostsError("Network error. Check your connection.")
            } catch (error: Exception) {
                showUserPostsError("Something went wrong: ${error.message.orEmpty()}")
            } finally {
                userPostsProgress.visibility = View.GONE
            }
        }
    }

    private fun showProfileError(message: String) {
        profileErrorText.visibility = View.VISIBLE
        profileErrorText.text = message
        profileRetryButton.visibility = View.VISIBLE
    }

    private fun showUserPostsError(message: String) {
        userPostsRecyclerView.visibility = View.GONE
        userPostsErrorText.visibility = View.VISIBLE
        userPostsErrorText.text = message
        userPostsRetryButton.visibility = View.VISIBLE
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}
