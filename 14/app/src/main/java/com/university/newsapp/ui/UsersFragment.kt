package com.university.newsapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.university.newsapp.R
import com.university.newsapp.model.User
import com.university.newsapp.repository.PostRepository
import kotlinx.coroutines.launch
import java.io.IOException
import retrofit2.HttpException

class UsersFragment : Fragment() {
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var retryButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter

    private var users: List<User> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        progressBar = view.findViewById(R.id.users_progress)
        errorText = view.findViewById(R.id.users_error_text)
        retryButton = view.findViewById(R.id.users_retry_button)
        recyclerView = view.findViewById(R.id.users_recycler)

        adapter = UserAdapter { user ->
            startActivity(
                Intent(requireContext(), UserProfileActivity::class.java)
                    .putExtra(UserProfileActivity.EXTRA_USER_ID, user.id)
            )
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        retryButton.setOnClickListener { loadUsers() }
        loadUsers()
    }

    private fun loadUsers() {
        progressBar.visibility = View.VISIBLE
        errorText.visibility = View.GONE
        retryButton.visibility = View.GONE
        recyclerView.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                users = PostRepository.getAllUsers()
                adapter.submitList(users)
                recyclerView.visibility = View.VISIBLE
            } catch (error: HttpException) {
                showError("Server error: ${error.code()}")
            } catch (error: IOException) {
                showError("Network error. Check your connection.")
            } catch (error: Exception) {
                showError("Something went wrong: ${error.message.orEmpty()}")
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showError(message: String) {
        errorText.visibility = View.VISIBLE
        errorText.text = message
        retryButton.visibility = View.VISIBLE
    }
}
