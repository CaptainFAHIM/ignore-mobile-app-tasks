package com.example.contactbookapp

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ContactAdapter
    private val contactList = mutableListOf<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView = findViewById<ListView>(R.id.listView)
        val searchView = findViewById<SearchView>(R.id.searchView)
        val fab = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab)
        val emptyView = findViewById<TextView>(R.id.emptyView)

        adapter = ContactAdapter(this, contactList)
        listView.adapter = adapter
        listView.emptyView = emptyView

        // FAB Add Contact
        fab.setOnClickListener {
            showAddContactDialog()
        }

        // Item Click
        listView.setOnItemClickListener { _, _, position, _ ->
            val c = contactList[position]
            Toast.makeText(this,
                "Name: ${c.name}\nPhone: ${c.phone}\nEmail: ${c.email}",
                Toast.LENGTH_LONG).show()
        }

        // Long Press Delete
        listView.setOnItemLongClickListener { _, _, position, _ ->
            AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes") { _, _ ->
                    contactList.removeAt(position)
                    adapter.notifyDataSetChanged()
                }
                .setNegativeButton("No", null)
                .show()
            true
        }

        // Search Filter
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })
    }

    private fun showAddContactDialog() {
        val view = layoutInflater.inflate(android.R.layout.simple_list_item_2, null)

        val nameInput = EditText(this)
        nameInput.hint = "Name"

        val phoneInput = EditText(this)
        phoneInput.hint = "Phone"

        val emailInput = EditText(this)
        emailInput.hint = "Email"

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 20, 40, 10)
        layout.addView(nameInput)
        layout.addView(phoneInput)
        layout.addView(emailInput)

        AlertDialog.Builder(this)
            .setTitle("Add Contact")
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val name = nameInput.text.toString()
                val phone = phoneInput.text.toString()
                val email = emailInput.text.toString()

                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    contactList.add(Contact(name, phone, email))
                    adapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}