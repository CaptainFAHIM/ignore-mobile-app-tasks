package com.university.newsapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.university.newsapp.R
import com.university.newsapp.model.Comment
import com.university.newsapp.model.Post
import com.university.newsapp.model.User
import com.university.newsapp.repository.PostRepository
import kotlinx.coroutines.launch
import java.io.IOException
import retrofit2.HttpException

class PostDetailActivity : AppCompatActivity() {
    private lateinit var postProgress: ProgressBar
    private lateinit var postErrorText: TextView
    private lateinit var postRetryButton: Button
    private lateinit var postContent: View
    private lateinit var titleText: TextView
    private lateinit var bodyText: TextView
    private lateinit var authorCard: CardView
    private lateinit var authorProgress: ProgressBar
    private lateinit var authorErrorText: TextView
    private lateinit var authorContent: View
    private lateinit var authorNameText: TextView
    private lateinit var authorEmailText: TextView
    private lateinit var authorCompanyText: TextView
    private lateinit var authorCatchphraseText: TextView
    private lateinit var commentsProgress: ProgressBar
    private lateinit var commentsErrorText: TextView
    private lateinit var commentsRetryButton: Button
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentsAdapter: CommentAdapter

    private var currentPostId = 0
    private var currentUserId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        currentPostId = intent.getIntExtra(EXTRA_POST_ID, 0)
        if (currentPostId == 0) {
            finish()
            return
        }

        bindViews()
        setupCommentsList()
        postRetryButton.setOnClickListener { loadPost() }
        commentsRetryButton.setOnClickListener { loadComments() }
        loadPost()
    }

    private fun bindViews() {
        postProgress = findViewById(R.id.post_progress)
        postErrorText = findViewById(R.id.post_error_text)
        postRetryButton = findViewById(R.id.post_retry_button)
        postContent = findViewById(R.id.post_content)
        titleText = findViewById(R.id.post_title)
        bodyText = findViewById(R.id.post_body)
        authorCard = findViewById(R.id.author_card)
        authorProgress = findViewById(R.id.author_progress)
        authorErrorText = findViewById(R.id.author_error_text)
        authorContent = findViewById(R.id.author_content)
        authorNameText = findViewById(R.id.author_name)
        authorEmailText = findViewById(R.id.author_email)
        authorCompanyText = findViewById(R.id.author_company)
        authorCatchphraseText = findViewById(R.id.author_catchphrase)
        commentsProgress = findViewById(R.id.comments_progress)
        commentsErrorText = findViewById(R.id.comments_error_text)
        commentsRetryButton = findViewById(R.id.comments_retry_button)
        commentsRecyclerView = findViewById(R.id.comments_recycler)
    }

    private fun setupCommentsList() {
        commentsAdapter = CommentAdapter()
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        commentsRecyclerView.adapter = commentsAdapter
    }

    private fun loadPost() {
        postProgress.visibility = View.VISIBLE
        postErrorText.visibility = View.GONE
        postRetryButton.visibility = View.GONE
        postContent.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val post = PostRepository.getPostById(currentPostId)
                showPost(post)
                currentUserId = post.userId
                loadAuthor(post.userId)
                loadComments()
            } catch (error: HttpException) {
                showPostError("Server error: ${error.code()}")
            } catch (error: IOException) {
                showPostError("Network error. Check your connection.")
            } catch (error: Exception) {
                showPostError("Something went wrong: ${error.message.orEmpty()}")
            } finally {
                postProgress.visibility = View.GONE
            }
        }
    }

    private fun showPost(post: Post) {
        titleText.text = post.title
        bodyText.text = post.body
        postContent.visibility = View.VISIBLE
        authorCard.setOnClickListener {
            if (currentUserId != 0) {
                startActivity(
                    Intent(this, UserProfileActivity::class.java)
                        .putExtra(UserProfileActivity.EXTRA_USER_ID, currentUserId)
                )
            }
        }
    }

    private fun loadAuthor(userId: Int) {
        authorProgress.visibility = View.VISIBLE
        authorErrorText.visibility = View.GONE
        authorNameText.text = ""
        authorEmailText.text = ""
        authorCompanyText.text = ""
        authorCatchphraseText.text = ""

        lifecycleScope.launch {
            try {
                val user = PostRepository.getUserById(userId)
                showAuthor(user)
            } catch (error: Exception) {
                authorErrorText.visibility = View.VISIBLE
                authorErrorText.text = "Could not load author."
            } finally {
                authorProgress.visibility = View.GONE
            }
        }
    }

    private fun showAuthor(user: User) {
        authorContent.visibility = View.VISIBLE
        authorNameText.text = user.name
        authorEmailText.text = user.email
        authorCompanyText.text = user.company.name
        authorCatchphraseText.text = user.company.catchPhrase
    }

    private fun loadComments() {
        commentsProgress.visibility = View.VISIBLE
        commentsErrorText.visibility = View.GONE
        commentsRetryButton.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val comments = PostRepository.getCommentsByPost(currentPostId)
                commentsAdapter.submitList(comments)
                commentsRecyclerView.visibility = View.VISIBLE
            } catch (error: HttpException) {
                showCommentsError("Server error: ${error.code()}")
            } catch (error: IOException) {
                showCommentsError("Network error. Check your connection.")
            } catch (error: Exception) {
                showCommentsError("Something went wrong: ${error.message.orEmpty()}")
            } finally {
                commentsProgress.visibility = View.GONE
            }
        }
    }

    private fun showPostError(message: String) {
        postErrorText.visibility = View.VISIBLE
        postErrorText.text = message
        postRetryButton.visibility = View.VISIBLE
    }

    private fun showCommentsError(message: String) {
        commentsRecyclerView.visibility = View.GONE
        commentsErrorText.visibility = View.VISIBLE
        commentsErrorText.text = message
        commentsRetryButton.visibility = View.VISIBLE
    }

    companion object {
        const val EXTRA_POST_ID = "extra_post_id"
    }
}
