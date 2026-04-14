package com.example.photogalleryapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private lateinit var adapter: PhotoAdapter
    private lateinit var selectionToolbar: LinearLayout
    private lateinit var tvSelectedCount: TextView
    private lateinit var btnDelete: Button
    private lateinit var btnShare: Button
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var categoryContainer: LinearLayout

    private var isSelectionMode = false
    private var allPhotos = mutableListOf<Photo>()
    private var filteredPhotos = mutableListOf<Photo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupSampleData()
        setupAdapter()
        setupEventListeners()
    }

    private fun initializeViews() {
        gridView = findViewById(R.id.gridView)
        selectionToolbar = findViewById(R.id.selectionToolbar)
        tvSelectedCount = findViewById(R.id.tvSelectedCount)
        btnDelete = findViewById(R.id.btnDelete)
        btnShare = findViewById(R.id.btnShare)
        fabAdd = findViewById(R.id.fabAdd)
        categoryContainer = findViewById(R.id.categoryContainer)
    }

    private fun setupSampleData() {
        // Create sample photos with different categories
        allPhotos = mutableListOf(
            Photo(1, R.drawable.ic_nature, "Mountain View", "Nature"),
            Photo(2, R.drawable.ic_nature, "Forest Path", "Nature"),
            Photo(3, R.drawable.ic_city, "City Skyline", "City"),
            Photo(4, R.drawable.ic_city, "Urban Street", "City"),
            Photo(5, R.drawable.ic_animals, "Wild Deer", "Animals"),
            Photo(6, R.drawable.ic_animals, "Bird Watching", "Animals"),
            Photo(7, R.drawable.ic_food, "Gourmet Dish", "Food"),
            Photo(8, R.drawable.ic_food, "Fresh Fruits", "Food"),
            Photo(9, R.drawable.ic_travel, "Beach Vacation", "Travel"),
            Photo(10, R.drawable.ic_travel, "Mountain Hike", "Travel"),
            Photo(11, R.drawable.ic_nature, "Sunset Lake", "Nature"),
            Photo(12, R.drawable.ic_city, "Night City", "City")
        )
        filteredPhotos.addAll(allPhotos)
    }

    private fun setupAdapter() {
        adapter = PhotoAdapter(this, filteredPhotos)
        gridView.adapter = adapter
    }

    private fun setupEventListeners() {
        // GridView item click
        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            if (isSelectionMode) {
                // Toggle selection in selection mode
                val photo = adapter.getItem(position)
                photo.isSelected = !photo.isSelected
                adapter.notifyDataSetChanged()
                updateSelectionCount()
            } else {
                // Open fullscreen in normal mode
                val photo = adapter.getItem(position)
                val intent = Intent(this, FullscreenActivity::class.java)
                intent.putExtra("imageResource", photo.resourceId)
                startActivity(intent)
            }
        }

        // GridView long press to enter selection mode
        gridView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            if (!isSelectionMode) {
                enterSelectionMode()
                val photo = adapter.getItem(position)
                photo.isSelected = true
                adapter.notifyDataSetChanged()
                updateSelectionCount()
            }
            true
        }

        // Delete button
        btnDelete.setOnClickListener {
            val selectedCount = adapter.getSelectedPhotos().size
            if (selectedCount > 0) {
                adapter.removeSelectedPhotos()
                exitSelectionMode()
                Toast.makeText(this, "$selectedCount photos deleted", Toast.LENGTH_SHORT).show()
            }
        }

        // Share button
        btnShare.setOnClickListener {
            val selectedCount = adapter.getSelectedPhotos().size
            Toast.makeText(this, "Sharing $selectedCount photos", Toast.LENGTH_SHORT).show()
        }

        // FAB to add photo
        fabAdd.setOnClickListener {
            // Simulate adding a random photo
            val randomPhoto = allPhotos.random()
            val newPhoto = Photo(
                allPhotos.size + 1,
                randomPhoto.resourceId,
                "New Photo ${System.currentTimeMillis() % 1000}",
                randomPhoto.category
            )
            allPhotos.add(newPhoto)
            filterPhotos("All") // Refresh with current filter
            Toast.makeText(this, "Photo added", Toast.LENGTH_SHORT).show()
        }

        // Category buttons
        val categories = listOf("All", "Nature", "City", "Animals", "Food", "Travel")
        for (i in 0 until categoryContainer.childCount) {
            val button = categoryContainer.getChildAt(i) as ToggleButton
            button.setOnClickListener {
                // Uncheck all other buttons
                for (j in 0 until categoryContainer.childCount) {
                    val otherButton = categoryContainer.getChildAt(j) as ToggleButton
                    otherButton.isChecked = (j == i)
                }
                filterPhotos(categories[i])
            }
        }
    }

    private fun enterSelectionMode() {
        isSelectionMode = true
        selectionToolbar.visibility = View.VISIBLE
        adapter.setSelectionMode(true)
        updateSelectionCount()
    }

    private fun exitSelectionMode() {
        isSelectionMode = false
        selectionToolbar.visibility = View.GONE
        adapter.setSelectionMode(false)
        adapter.clearSelections()
    }

    private fun updateSelectionCount() {
        val selectedCount = adapter.getSelectedPhotos().size
        tvSelectedCount.text = "$selectedCount selected"
    }

    private fun filterPhotos(category: String) {
        filteredPhotos.clear()
        if (category == "All") {
            filteredPhotos.addAll(allPhotos)
        } else {
            filteredPhotos.addAll(allPhotos.filter { it.category == category })
        }
        adapter.updateData(filteredPhotos)
    }

    override fun onBackPressed() {
        if (isSelectionMode) {
            exitSelectionMode()
        } else {
            super.onBackPressed()
        }
    }
}