package com.example.universityeventapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    data class Event(
        val id: Int,
        val title: String,
        val description: String,
        val price: Double
    )

    private lateinit var events: ArrayList<Event>
    private lateinit var listView: ListView

    private var selectedEvent: Event? = null
    private lateinit var seats: MutableList<String>
    private val selectedSeats = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutHome = findViewById<LinearLayout>(R.id.layoutHome)
        val layoutEvents = findViewById<LinearLayout>(R.id.layoutEvents)
        val layoutDetail = findViewById<LinearLayout>(R.id.layoutDetail)
        val layoutBooking = findViewById<LinearLayout>(R.id.layoutBooking)

        // Sample events
        events = arrayListOf(
            Event(1,"Tech Fest","Biggest tech festival.",100.0),
            Event(2,"Football Tournament","Inter department match.",50.0),
            Event(3,"Cultural Night","Music and dance show.",80.0),
            Event(4,"AI Workshop","Hands-on AI session.",150.0),
            Event(5,"Career Seminar","Career guidance event.",0.0),
            Event(6,"Cricket Match","Friendly match.",30.0),
            Event(7,"Drama Show","Department drama.",60.0),
            Event(8,"Social Meetup","Networking event.",20.0)
        )

        // HOME -> EVENTS
        findViewById<Button>(R.id.btnBrowseEvents).setOnClickListener {
            layoutHome.visibility = View.GONE
            layoutEvents.visibility = View.VISIBLE
        }

        // EVENTS LIST
        listView = findViewById(R.id.listViewEvents)
        updateList(events)

        listView.setOnItemClickListener { _, _, position, _ ->
            selectedEvent = events[position]

            findViewById<TextView>(R.id.tvDetailTitle).text =
                selectedEvent!!.title

            findViewById<TextView>(R.id.tvDetailDesc).text =
                selectedEvent!!.description

            layoutEvents.visibility = View.GONE
            layoutDetail.visibility = View.VISIBLE
        }

        // SEARCH
        findViewById<SearchView>(R.id.searchView)
            .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = false
                override fun onQueryTextChange(newText: String?): Boolean {
                    val filtered = events.filter {
                        it.title.contains(newText ?: "", true)
                    }
                    updateList(ArrayList(filtered))
                    return true
                }
            })

        // DETAIL -> BOOKING
        findViewById<Button>(R.id.btnGoBooking).setOnClickListener {
            layoutDetail.visibility = View.GONE
            layoutBooking.visibility = View.VISIBLE
            setupSeats()
        }

        // CONFIRM BOOKING
        findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            Toast.makeText(this,"Booking Confirmed!",Toast.LENGTH_SHORT).show()
            selectedSeats.clear()
            layoutBooking.visibility = View.GONE
            layoutHome.visibility = View.VISIBLE
        }

        // MODERN BACK HANDLING
        onBackPressedDispatcher.addCallback(this) {

            when {
                layoutBooking.visibility == View.VISIBLE -> {
                    if (selectedSeats.isNotEmpty()) {
                        AlertDialog.Builder(this@MainActivity)
                            .setMessage("Leave without booking?")
                            .setPositiveButton("Yes") { _, _ ->
                                layoutBooking.visibility = View.GONE
                                layoutDetail.visibility = View.VISIBLE
                            }
                            .setNegativeButton("No", null)
                            .show()
                    } else {
                        layoutBooking.visibility = View.GONE
                        layoutDetail.visibility = View.VISIBLE
                    }
                }

                layoutDetail.visibility == View.VISIBLE -> {
                    layoutDetail.visibility = View.GONE
                    layoutEvents.visibility = View.VISIBLE
                }

                layoutEvents.visibility == View.VISIBLE -> {
                    layoutEvents.visibility = View.GONE
                    layoutHome.visibility = View.VISIBLE
                }

                else -> finish()
            }
        }
    }

    private fun updateList(list: ArrayList<Event>) {
        listView.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            list.map { it.title }
        )
    }

    private fun setupSeats() {

        seats = MutableList(48) { "Available" }
        selectedSeats.clear()

        repeat(14) {
            seats[Random.nextInt(48)] = "Booked"
        }

        val grid = findViewById<GridView>(R.id.gridSeats)
        val summary = findViewById<TextView>(R.id.tvSummary)

        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            seats
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                when (seats[position]) {
                    "Available" -> view.setBackgroundColor(Color.GREEN)
                    "Booked" -> view.setBackgroundColor(Color.RED)
                    "Selected" -> view.setBackgroundColor(Color.BLUE)
                }
                return view
            }
        }

        grid.adapter = adapter

        grid.setOnItemClickListener { _, _, position, _ ->

            if (seats[position] == "Booked") return@setOnItemClickListener

            if (selectedSeats.contains(position)) {
                selectedSeats.remove(position)
                seats[position] = "Available"
            } else {
                selectedSeats.add(position)
                seats[position] = "Selected"
            }

            adapter.notifyDataSetChanged()

            val price = selectedEvent?.price ?: 0.0
            summary.text =
                "${selectedSeats.size} seats | Total: ${selectedSeats.size * price}"
        }
    }
}