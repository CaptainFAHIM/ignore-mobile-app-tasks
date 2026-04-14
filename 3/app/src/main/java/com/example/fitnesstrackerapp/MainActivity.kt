package com.example.fitnesstrackerapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var steps = 0
    private val goal = 10000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val stepsText = findViewById<TextView>(R.id.stepsText)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val progressPercent = findViewById<TextView>(R.id.progressPercent)
        val updateBtn = findViewById<Button>(R.id.updateBtn)

        fun updateUI() {
            stepsText.text = steps.toString()

            val progress = ((steps.toDouble() / goal) * 100).toInt()
            progressBar.progress = progress
            progressPercent.text = "$progress%"

            if (progress >= 100) {
                Toast.makeText(
                    this,
                    "🎉 Goal Completed!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        updateBtn.setOnClickListener {
            val input = EditText(this)
            input.hint = "Enter steps"

            AlertDialog.Builder(this)
                .setTitle("Update Steps")
                .setView(input)
                .setPositiveButton("Update") { _, _ ->
                    steps = input.text.toString().toIntOrNull() ?: 0
                    updateUI()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        updateUI()
    }
}