package com.example.contactbookapp

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*

class ContactAdapter(
    context: Context,
    private val contacts: MutableList<Contact>
) : ArrayAdapter<Contact>(context, 0, contacts) {

    private val originalList = ArrayList(contacts)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)
            holder = ViewHolder()
            holder.avatar = view.findViewById(R.id.tvAvatar)
            holder.name = view.findViewById(R.id.tvName)
            holder.phone = view.findViewById(R.id.tvPhone)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val contact = contacts[position]
        holder.name?.text = contact.name
        holder.phone?.text = contact.phone
        holder.avatar?.text = contact.initial

        // Dynamic avatar color
        val colors = arrayOf("#F44336","#9C27B0","#3F51B5","#009688","#FF9800","#795548")
        val index = contact.initial[0].code % colors.size
        holder.avatar?.setBackgroundColor(Color.parseColor(colors[index]))

        return view
    }

    fun filter(text: String) {
        contacts.clear()
        if (text.isEmpty()) {
            contacts.addAll(originalList)
        } else {
            val filtered = originalList.filter {
                it.name.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            }
            contacts.addAll(filtered)
        }
        notifyDataSetChanged()
    }

    private class ViewHolder {
        var avatar: TextView? = null
        var name: TextView? = null
        var phone: TextView? = null
    }
}