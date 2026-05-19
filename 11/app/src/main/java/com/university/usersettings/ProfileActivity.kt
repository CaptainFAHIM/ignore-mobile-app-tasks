package com.university.usersettings

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.university.usersettings.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spinnerDepartment.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("CSE", "EEE", "BBA", "English", "Law"))
        binding.spinnerYear.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("1st Year", "2nd Year", "3rd Year", "4th Year"))

        binding.buttonSaveProfile.setOnClickListener { saveProfile() }
        restoreProfile()
        updateBanner()
    }

    override fun onResume() {
        super.onResume()
        updateBanner()
    }

    private fun saveProfile() {
        getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE).edit().apply {
            putString("KEY_STUDENT_NAME", binding.editFullName.text.toString())
            putString("KEY_STUDENT_ID", binding.editStudentId.text.toString())
            putString("KEY_DEPARTMENT", binding.spinnerDepartment.selectedItem.toString())
            putString("KEY_YEAR", binding.spinnerYear.selectedItem.toString())
            putString("KEY_EMAIL", binding.editEmail.text.toString())
            apply()
        }
        Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
        updateBanner()
    }

    private fun restoreProfile() {
        val prefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
        binding.editFullName.setText(prefs.getString("KEY_STUDENT_NAME", ""))
        binding.editStudentId.setText(prefs.getString("KEY_STUDENT_ID", ""))
        binding.editEmail.setText(prefs.getString("KEY_EMAIL", ""))
        setSpinnerValue(binding.spinnerDepartment, prefs.getString("KEY_DEPARTMENT", "CSE") ?: "CSE")
        setSpinnerValue(binding.spinnerYear, prefs.getString("KEY_YEAR", "1st Year") ?: "1st Year")
    }

    private fun updateBanner() {
        val prefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
        val name = prefs.getString("KEY_STUDENT_NAME", "Student").orEmpty().ifBlank { "Student" }
        binding.textWelcome.text = "Welcome back, $name!"
    }

    private fun setSpinnerValue(spinner: Spinner, value: String) {
        val adapter = spinner.adapter ?: return
        for (index in 0 until adapter.count) {
            if (adapter.getItem(index).toString() == value) {
                spinner.setSelection(index)
                return
            }
        }
    }
}