package com.university.usersettings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.university.usersettings.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val languages = listOf("English", "Bangla", "Arabic", "French")
        binding.spinnerLanguage.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languages)
        binding.seekBarFont.max = 12
        binding.seekBarFont.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.textFontSize.text = "Font Size: ${progress + 12}sp"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        binding.buttonSave.setOnClickListener { saveSettings() }
        binding.buttonReset.setOnClickListener { resetSettings() }
        binding.buttonViewSaved.setOnClickListener { startActivity(Intent(this, SettingsViewerActivity::class.java)) }
        binding.buttonProfile.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }

        restoreSettings()
    }

    override fun onResume() {
        super.onResume()
        restoreSettings()
    }

    private fun saveSettings() {
        val theme = when (binding.themeGroup.checkedRadioButtonId) {
            binding.radioDark.id -> "dark"
            binding.radioSystem.id -> "system"
            else -> "light"
        }
        getSharedPreferences("AppSettings", Context.MODE_PRIVATE).edit().apply {
            putString("KEY_THEME", theme)
            putBoolean("KEY_NOTIFICATIONS", binding.switchNotifications.isChecked)
            putString("KEY_LANGUAGE", binding.spinnerLanguage.selectedItem.toString())
            putInt("KEY_FONT_SIZE", binding.seekBarFont.progress + 12)
            putLong("KEY_LAST_SAVED", System.currentTimeMillis())
            apply()
        }
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
    }

    private fun resetSettings() {
        getSharedPreferences("AppSettings", Context.MODE_PRIVATE).edit().clear().apply()
        restoreDefaultUi()
        Toast.makeText(this, "Settings reset to default", Toast.LENGTH_SHORT).show()
    }

    private fun restoreSettings() {
        val prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        when (prefs.getString("KEY_THEME", "light")) {
            "dark" -> binding.radioDark.isChecked = true
            "system" -> binding.radioSystem.isChecked = true
            else -> binding.radioLight.isChecked = true
        }
        binding.switchNotifications.isChecked = prefs.getBoolean("KEY_NOTIFICATIONS", true)
        val fontSize = prefs.getInt("KEY_FONT_SIZE", 16)
        binding.seekBarFont.progress = fontSize - 12
        binding.textFontSize.text = "Font Size: ${fontSize}sp"
        val language = prefs.getString("KEY_LANGUAGE", "English") ?: "English"
        val position = listOf("English", "Bangla", "Arabic", "French").indexOf(language)
        binding.spinnerLanguage.setSelection(if (position >= 0) position else 0)
    }

    private fun restoreDefaultUi() {
        binding.radioLight.isChecked = true
        binding.switchNotifications.isChecked = true
        binding.spinnerLanguage.setSelection(0)
        binding.seekBarFont.progress = 4
        binding.textFontSize.text = "Font Size: 16sp"
    }
}