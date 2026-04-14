package com.example.loginprofileapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val username = findViewById<EditText>(R.id.usernameInput)
        val password = findViewById<EditText>(R.id.passwordInput)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val forgot = findViewById<TextView>(R.id.forgotPassword)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val profileCard = findViewById<View>(R.id.profileCard)

        val logo = findViewById<View>(R.id.logoImage)
        val title = findViewById<View>(R.id.titleText)

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)

        loginBtn.setOnClickListener {
            val user = username.text.toString()
            val pass = password.text.toString()

            if (user == "admin" && pass == "1234") {

                progressBar.visibility = View.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed({

                    progressBar.visibility = View.GONE

                    // hide login UI
                    username.visibility = View.GONE
                    password.visibility = View.GONE
                    loginBtn.visibility = View.GONE
                    forgot.visibility = View.GONE
                    logo.visibility = View.GONE
                    title.visibility = View.GONE

                    // show profile
                    profileCard.visibility = View.VISIBLE

                }, 1500)

            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        forgot.setOnClickListener {
            Toast.makeText(
                this,
                "Password reset link sent to your email",
                Toast.LENGTH_SHORT
            ).show()
        }

        logoutBtn.setOnClickListener {

            username.setText("")
            password.setText("")

            // show login UI
            username.visibility = View.VISIBLE
            password.visibility = View.VISIBLE
            loginBtn.visibility = View.VISIBLE
            forgot.visibility = View.VISIBLE
            logo.visibility = View.VISIBLE
            title.visibility = View.VISIBLE

            // hide profile
            profileCard.visibility = View.GONE
        }
    }
}