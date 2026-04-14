package com.captainfahim.studentregistrationapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var etStudentId: EditText
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var etAge: EditText

    lateinit var radioGroupGender: RadioGroup

    lateinit var cbFootball: CheckBox
    lateinit var cbCricket: CheckBox
    lateinit var cbBasketball: CheckBox
    lateinit var cbBadminton: CheckBox

    lateinit var spinnerCountry: Spinner
    lateinit var btnDate: Button
    lateinit var btnSubmit: Button
    lateinit var btnReset: Button

    var selectedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // findViewById
        etStudentId = findViewById(R.id.etStudentId)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etAge = findViewById(R.id.etAge)

        radioGroupGender = findViewById(R.id.radioGroupGender)

        cbFootball = findViewById(R.id.cbFootball)
        cbCricket = findViewById(R.id.cbCricket)
        cbBasketball = findViewById(R.id.cbBasketball)
        cbBadminton = findViewById(R.id.cbBadminton)

        spinnerCountry = findViewById(R.id.spinnerCountry)
        btnDate = findViewById(R.id.btnDate)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnReset = findViewById(R.id.btnReset)

        // Spinner Data
        val countries = arrayOf("Bangladesh", "India", "USA", "UK", "Canada")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCountry.adapter = adapter

        // DatePicker
        btnDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this,
                { _, y, m, d ->
                    selectedDate = "$d/${m + 1}/$y"
                    btnDate.text = selectedDate
                }, year, month, day)

            datePickerDialog.show()
        }

        // Submit Button
        btnSubmit.setOnClickListener {

            val id = etStudentId.text.toString()
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val ageText = etAge.text.toString()

            val selectedGenderId = radioGroupGender.checkedRadioButtonId

            if (id.isEmpty() || name.isEmpty() || email.isEmpty() ||
                password.isEmpty() || ageText.isEmpty() ||
                selectedGenderId == -1 || selectedDate.isEmpty()) {

                Toast.makeText(this,
                    "Please complete all required fields",
                    Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val age = ageText.toInt()

            if (age <= 0 || !email.contains("@")) {
                Toast.makeText(this,
                    "Please complete all required fields",
                    Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val gender = findViewById<RadioButton>(selectedGenderId).text.toString()

            val sportsList = mutableListOf<String>()
            if (cbFootball.isChecked) sportsList.add("Football")
            if (cbCricket.isChecked) sportsList.add("Cricket")
            if (cbBasketball.isChecked) sportsList.add("Basketball")
            if (cbBadminton.isChecked) sportsList.add("Badminton")

            val sports = if (sportsList.isEmpty()) "None" else sportsList.joinToString(", ")

            val country = spinnerCountry.selectedItem.toString()

            val message = """
        ID: $id
        Name: $name
        Gender: $gender
        Sports: $sports
        Country: $country
        DOB: $selectedDate
    """.trimIndent()

            // 🔥 CUSTOM TOAST STARTS HERE
            val inflater = layoutInflater
            val layout = inflater.inflate(R.layout.custom_toast, null)

            val textView = layout.findViewById<TextView>(R.id.toastText)
            textView.text = message

            val toast = Toast(applicationContext)
            toast.duration = Toast.LENGTH_LONG
            toast.view = layout
            toast.show()
        }

        // Reset Button
        btnReset.setOnClickListener {
            etStudentId.text.clear()
            etName.text.clear()
            etEmail.text.clear()
            etPassword.text.clear()
            etAge.text.clear()

            radioGroupGender.clearCheck()

            cbFootball.isChecked = false
            cbCricket.isChecked = false
            cbBasketball.isChecked = false
            cbBadminton.isChecked = false

            spinnerCountry.setSelection(0)

            selectedDate = ""
            btnDate.text = "Select Date"
        }
    }
}