package com.example.studentauthapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        rootView = findViewById(R.id.loginRoot)
        emailInput = findViewById(R.id.loginEmailInput)
        passwordInput = findViewById(R.id.loginPasswordInput)
        progressBar = findViewById(R.id.loginProgressBar)

        findViewById<Button>(R.id.loginButton).setOnClickListener { login() }
        findViewById<TextView>(R.id.registerLink).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun login() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        clearErrors()

        when {
            email.isEmpty() || password.isEmpty() -> {
                showMessage(getString(R.string.email_password_required))
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailInput.error = getString(R.string.invalid_email)
                return
            }
            password.length < 8 -> {
                passwordInput.error = getString(R.string.password_too_short)
                return
            }
        }

        if (!AuthStore.hasRegisteredAccount(this)) {
            showMessage(getString(R.string.no_account_found))
            return
        }

        progressBar.visibility = View.VISIBLE
        val isValidUser = AuthStore.attemptLogin(this, email, password)
        progressBar.visibility = View.GONE

        if (isValidUser) {
            showMessage(getString(R.string.login_success))
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            showMessage(getString(R.string.invalid_credentials))
        }
    }

    private fun clearErrors() {
        emailInput.error = null
        passwordInput.error = null
    }

    private fun showMessage(message: String) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show()
    }
}