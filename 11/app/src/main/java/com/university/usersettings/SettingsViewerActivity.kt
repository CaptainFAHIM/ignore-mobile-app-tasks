package com.university.usersettings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.university.usersettings.databinding.ActivitySettingsViewerBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener { finish() }
        binding.buttonEdit.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        showSavedSettings()
    }

    private fun showSavedSettings() {
        val prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        if (!prefs.contains("KEY_LAST_SAVED")) {
            binding.placeholderText.text = "No settings saved. Go back and save your preferences."
            binding.placeholderText.visibility = View.VISIBLE
            binding.cardContainer.visibility = View.GONE
            return
        }

        binding.placeholderText.visibility = View.GONE
        binding.cardContainer.visibility = View.VISIBLE

        binding.valueTheme.text = prefs.getString("KEY_THEME", "light")
        binding.valueNotifications.text = if (prefs.getBoolean("KEY_NOTIFICATIONS", true)) "Enabled" else "Disabled"
        binding.valueLanguage.text = prefs.getString("KEY_LANGUAGE", "English")
        binding.valueFontSize.text = "${prefs.getInt("KEY_FONT_SIZE", 16)}sp"
        val savedTime = prefs.getLong("KEY_LAST_SAVED", 0L)
        binding.valueLastSaved.text = if (savedTime > 0) {
            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(savedTime))
        } else {
            "N/A"
        }
    }
}