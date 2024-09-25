package com.aashay.fetch_exercise_demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.aashay.fetch_exercise_demo.Item
import com.aashay.fetch_exercise_demo.MainActivity
import com.aashay.fetch_exercise_demo.R

class ItemAdapter(
    context: MainActivity,
    private val items: List<Item>
) : ArrayAdapter<Item>(context, R.layout.list_item, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        val currentItem = items[position]
        val idTextView: TextView = view.findViewById(R.id.itemId)
        val listIdTextView: TextView = view.findViewById(R.id.itemListId)
        val nameTextView: TextView = view.findViewById(R.id.itemName)

        idTextView.text = currentItem.id.toString()
        listIdTextView.text = currentItem.listId.toString()
        nameTextView.text = currentItem.name

        return view
    }
}