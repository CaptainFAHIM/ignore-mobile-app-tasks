package com.university.newsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.appbar.MaterialToolbar
import com.university.newsapp.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        if (savedInstanceState == null) {
            showFragment(PostsFragment())
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_posts -> showFragment(PostsFragment())
                R.id.nav_users -> showFragment(UsersFragment())
            }
            true
        }

        bottomNavigation.selectedItemId = R.id.nav_posts
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
