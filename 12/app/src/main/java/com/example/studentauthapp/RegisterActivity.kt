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

class RegisterActivity : AppCompatActivity() {
    private lateinit var fullNameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        rootView = findViewById(R.id.registerRoot)
        fullNameInput = findViewById(R.id.fullNameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        progressBar = findViewById(R.id.registerProgressBar)

        findViewById<Button>(R.id.registerButton).setOnClickListener { register() }
        findViewById<TextView>(R.id.loginLink).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun register() {
        val fullName = fullNameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString()

        clearErrors()

        when {
            fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                showMessage(getString(R.string.missing_registration_fields))
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
            password != confirmPassword -> {
                confirmPasswordInput.error = getString(R.string.password_mismatch)
                return
            }
        }

        progressBar.visibility = View.VISIBLE
        AuthStore.saveAccount(this, fullName, email, password)
        progressBar.visibility = View.GONE

        showMessage(getString(R.string.registration_success))
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun clearErrors() {
        fullNameInput.error = null
        emailInput.error = null
        passwordInput.error = null
        confirmPasswordInput.error = null
    }

    private fun showMessage(message: String) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show()
    }
}