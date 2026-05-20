package com.example.studentauthapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val welcomeText = findViewById<TextView>(R.id.homeWelcomeText)
        val name = AuthStore.getFullName(this)

        welcomeText.text = if (name.isNullOrBlank()) {
            getString(R.string.welcome_home)
        } else {
            getString(R.string.welcome_home) + ", $name"
        }

        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            AuthStore.logout(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}