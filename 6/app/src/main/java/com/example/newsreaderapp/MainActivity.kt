package com.example.newsreaderapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView

class MainActivity : AppCompatActivity() {

    private var isBookmarked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scrollView = findViewById<NestedScrollView>(R.id.scrollView)

        val btnIntro = findViewById<Button>(R.id.btnIntro)
        val btnKey = findViewById<Button>(R.id.btnKey)
        val btnAnalysis = findViewById<Button>(R.id.btnAnalysis)
        val btnConclusion = findViewById<Button>(R.id.btnConclusion)
        val btnTop = findViewById<Button>(R.id.btnTop)

        val sectionIntro = findViewById<TextView>(R.id.sectionIntro)
        val sectionKey = findViewById<TextView>(R.id.sectionKey)
        val sectionAnalysis = findViewById<TextView>(R.id.sectionAnalysis)
        val sectionConclusion = findViewById<TextView>(R.id.sectionConclusion)

        val btnBookmark = findViewById<ImageButton>(R.id.btnBookmark)
        val btnShare = findViewById<ImageButton>(R.id.btnShare)

        btnIntro.setOnClickListener {
            scrollView.smoothScrollTo(0, sectionIntro.top)
        }

        btnKey.setOnClickListener {
            scrollView.smoothScrollTo(0, sectionKey.top)
        }

        btnAnalysis.setOnClickListener {
            scrollView.smoothScrollTo(0, sectionAnalysis.top)
        }

        btnConclusion.setOnClickListener {
            scrollView.smoothScrollTo(0, sectionConclusion.top)
        }

        btnTop.setOnClickListener {
            scrollView.smoothScrollTo(0, 0)
        }

        btnBookmark.setOnClickListener {
            isBookmarked = !isBookmarked
            if (isBookmarked) {
                btnBookmark.setImageResource(android.R.drawable.btn_star_big_on)
                Toast.makeText(this, "Article Bookmarked", Toast.LENGTH_SHORT).show()
            } else {
                btnBookmark.setImageResource(android.R.drawable.btn_star_big_off)
                Toast.makeText(this, "Bookmark Removed", Toast.LENGTH_SHORT).show()
            }
        }

        btnShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "The Future of Artificial Intelligence")
            startActivity(Intent.createChooser(intent, "Share via"))
        }
    }
}