package com.example.photogalleryapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView

class PhotoAdapter(
    private val context: Context,
    private var photos: List<Photo>,
    private var isSelectionMode: Boolean = false
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = photos.size

    override fun getItem(position: Int): Photo = photos[position]

    override fun getItemId(position: Int): Long = photos[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_photo, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val photo = getItem(position)

        // Set image resource
        holder.imageView.setImageResource(photo.resourceId)
        
        // Set title
        holder.titleView.text = photo.title
        
        // Handle selection mode
        holder.checkBox.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
        holder.checkBox.isChecked = photo.isSelected
        
        // Set checkbox click listener
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            photo.isSelected = isChecked
        }

        return view
    }

    fun updateData(newPhotos: List<Photo>) {
        photos = newPhotos
        notifyDataSetChanged()
    }

    fun setSelectionMode(selectionMode: Boolean) {
        isSelectionMode = selectionMode
        notifyDataSetChanged()
    }

    fun getSelectedPhotos(): List<Photo> {
        return photos.filter { it.isSelected }
    }

    fun clearSelections() {
        photos.forEach { it.isSelected = false }
        notifyDataSetChanged()
    }

    fun removeSelectedPhotos() {
        photos = photos.filter { !it.isSelected }
        notifyDataSetChanged()
    }

    private class ViewHolder(view: View) {
        val imageView: ImageView = view.findViewById(R.id.ivPhoto)
        val titleView: TextView = view.findViewById(R.id.tvTitle)
        val checkBox: CheckBox = view.findViewById(R.id.cbSelection)
    }
}