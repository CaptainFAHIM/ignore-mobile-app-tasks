package com.example.gradereportapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var totalSubjects = 0
    private var passed = 0
    private var failed = 0
    private var totalGpaPoints = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val table = findViewById<TableLayout>(R.id.gradeTable)
        val summaryText = findViewById<TextView>(R.id.summaryText)
        val gpaText = findViewById<TextView>(R.id.gpaText)

        val subjectInput = findViewById<EditText>(R.id.subjectInput)
        val obtainedInput = findViewById<EditText>(R.id.obtainedInput)
        val totalInput = findViewById<EditText>(R.id.totalInput)
        val addBtn = findViewById<Button>(R.id.addBtn)
        val printBtn = findViewById<Button>(R.id.printBtn)

        addBtn.setOnClickListener {

            val subject = subjectInput.text.toString()
            val obtained = obtainedInput.text.toString().toIntOrNull()
            val total = totalInput.text.toString().toIntOrNull()

            if (subject.isEmpty() || obtained == null || total == null || total == 0) {
                Toast.makeText(this, "Invalid Input!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val percentage = (obtained * 100) / total
            val (grade, gpaPoint) = calculateGrade(percentage)

            val row = TableRow(this)

            val bgColor = if (totalSubjects % 2 == 0) "#F5F5F5" else "#FFFFFF"
            row.setBackgroundColor(Color.parseColor(bgColor))

            val subjectTv = TextView(this)
            subjectTv.text = subject
            subjectTv.setPadding(8, 8, 8, 8)

            val obtainedTv = TextView(this)
            obtainedTv.text = obtained.toString()
            obtainedTv.setPadding(8, 8, 8, 8)

            val totalTv = TextView(this)
            totalTv.text = total.toString()
            totalTv.setPadding(8, 8, 8, 8)

            val gradeTv = TextView(this)
            gradeTv.text = grade
            gradeTv.setPadding(8, 8, 8, 8)

            if (grade == "F") {
                gradeTv.setTextColor(Color.RED)
                failed++
            } else {
                gradeTv.setTextColor(Color.parseColor("#2E7D32"))
                passed++
            }

            row.addView(subjectTv)
            row.addView(obtainedTv)
            row.addView(totalTv)
            row.addView(gradeTv)

            table.addView(row)

            totalSubjects++
            totalGpaPoints += gpaPoint

            val gpa = totalGpaPoints / totalSubjects

            summaryText.text =
                "Total Subjects: $totalSubjects | Passed: $passed | Failed: $failed"

            gpaText.text = "GPA: %.2f".format(gpa)

            subjectInput.text.clear()
            obtainedInput.text.clear()
            totalInput.text.clear()
        }

        printBtn.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, gpaText.text.toString())
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
    }

    private fun calculateGrade(percent: Int): Pair<String, Double> {
        return when (percent) {
            in 90..100 -> Pair("A+", 4.0)
            in 80..89 -> Pair("A", 3.7)
            in 70..79 -> Pair("B+", 3.3)
            in 60..69 -> Pair("B", 3.0)
            in 50..59 -> Pair("C", 2.0)
            in 40..49 -> Pair("D", 1.0)
            else -> Pair("F", 0.0)
        }
    }
}