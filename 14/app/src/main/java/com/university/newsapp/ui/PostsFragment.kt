package com.university.newsapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.university.newsapp.R
import com.university.newsapp.model.Post
import com.university.newsapp.repository.PostRepository
import kotlinx.coroutines.launch
import java.io.IOException
import retrofit2.HttpException

class PostsFragment : Fragment() {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var searchView: SearchView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var retryButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter

    private var allPosts: List<Post> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swipeRefreshLayout = view.findViewById(R.id.posts_swipe_refresh)
        searchView = view.findViewById(R.id.search_view)
        progressBar = view.findViewById(R.id.posts_progress)
        errorText = view.findViewById(R.id.posts_error_text)
        retryButton = view.findViewById(R.id.posts_retry_button)
        recyclerView = view.findViewById(R.id.posts_recycler)

        adapter = PostAdapter { post ->
            startActivity(
                Intent(requireContext(), PostDetailActivity::class.java)
                    .putExtra(PostDetailActivity.EXTRA_POST_ID, post.id)
            )
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        swipeRefreshLayout.setColorSchemeResources(R.color.brand)
        swipeRefreshLayout.setOnRefreshListener { loadPosts(true) }
        retryButton.setOnClickListener { loadPosts(false) }

        searchView.queryHint = getString(R.string.search_posts)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterPosts(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterPosts(newText.orEmpty())
                return true
            }
        })

        loadPosts(false)
    }

    private fun loadPosts(isRefresh: Boolean) {
        if (isRefresh) {
            swipeRefreshLayout.isRefreshing = true
        } else {
            progressBar.visibility = View.VISIBLE
            errorText.visibility = View.GONE
            retryButton.visibility = View.GONE
            recyclerView.visibility = View.GONE
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                allPosts = PostRepository.getAllPosts()
                filterPosts(searchView.query?.toString().orEmpty())
                recyclerView.visibility = View.VISIBLE
                errorText.visibility = View.GONE
                retryButton.visibility = View.GONE
            } catch (error: HttpException) {
                showError("Server error: ${error.code()}")
            } catch (error: IOException) {
                showError("Network error. Check your connection.")
            } catch (error: Exception) {
                showError("Something went wrong: ${error.message.orEmpty()}")
            } finally {
                progressBar.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun filterPosts(query: String) {
        val filtered = if (query.isBlank()) {
            allPosts
        } else {
            allPosts.filter { post ->
                post.title.contains(query, ignoreCase = true)
            }
        }
        adapter.submitList(filtered)
        if (filtered.isEmpty() && allPosts.isNotEmpty()) {
            recyclerView.visibility = View.GONE
            errorText.visibility = View.VISIBLE
            errorText.text = "No posts found."
            retryButton.visibility = View.GONE
        } else if (allPosts.isNotEmpty()) {
            recyclerView.visibility = View.VISIBLE
            errorText.visibility = View.GONE
        }
    }

    private fun showError(message: String) {
        recyclerView.visibility = View.GONE
        errorText.visibility = View.VISIBLE
        errorText.text = message
        retryButton.visibility = View.VISIBLE
    }
}
