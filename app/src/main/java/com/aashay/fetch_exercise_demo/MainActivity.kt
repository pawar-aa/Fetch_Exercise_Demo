package com.aashay.fetch_exercise_demo

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: ItemAdapter
    private var list: MutableList<Item> = mutableListOf()
    private var filteredList: MutableList<Item> = mutableListOf()
    private var settingSelectedSortId: Int = -1
    private var settingSelectedCheckId: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val listView1: ListView = findViewById(R.id.lv_1)
        val searchView: SearchView = findViewById(R.id.sv_1)

        adapter = ItemAdapter(this, list)
        listView1.adapter = adapter

        lifecycleScope.launch {
            list = fetchData()
            adapter.clear()
            adapter.addAll(list)
            adapter.notifyDataSetChanged()
        }

        val btnSettings: ImageButton = findViewById(R.id.btn_4)
        btnSettings.setOnClickListener { showBottomSheetMenu() }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                settingSelectedSortId = -1
                settingSelectedCheckId = false
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String?) {
        filteredList.clear()
        if (query.isNullOrEmpty()) {
            filteredList.addAll(list)
        } else {
            val lowerCaseQuery = query.lowercase()
            filteredList.addAll(list.filter {
                it.id.toString().contains(lowerCaseQuery) ||
                        it.listId.toString().contains(lowerCaseQuery) ||
                        it.name?.lowercase()?.contains(lowerCaseQuery) == true
            })
        }
        adapter.clear()
        adapter.addAll(filteredList)
        adapter.notifyDataSetChanged()
    }

    private fun showBottomSheetMenu() {
        val bottomSheetDialog = BottomSheetMenuDialog(settingSelectedSortId, settingSelectedCheckId) { selectedId, hideNull ->
            settingSelectedSortId = selectedId
            settingSelectedCheckId = hideNull

            val groupedAndSortedList = when {
                hideNull && selectedId == -1 -> list.filter { !it.name.isNullOrBlank() }
                selectedId != -1 -> when (selectedId) {
                    R.id.radio_az_listid -> groupAndSortItems(list, ascending = true, hideNull)
                    R.id.radio_za_listid -> groupAndSortItems(list, ascending = false, hideNull)
                    R.id.radio_az_name -> groupAndSortItemsByName(list, ascending = true, hideNull)
                    R.id.radio_za_name -> groupAndSortItemsByName(list, ascending = false, hideNull)
                    R.id.radio_listId_name -> groupAndSortItemsByListIdAndName(list, hideNull)
                    else -> list
                }
                else -> list
            }

            adapter.clear()
            adapter.addAll(groupedAndSortedList)
            adapter.notifyDataSetChanged()
        }

        bottomSheetDialog.selectedOption = settingSelectedSortId
        bottomSheetDialog.hideNullName = settingSelectedCheckId
        bottomSheetDialog.show(supportFragmentManager, bottomSheetDialog.tag)
    }

    private fun groupAndSortItems(list: List<Item>, ascending: Boolean, hideNullName: Boolean): MutableList<Item> {
        val filteredList = if (hideNullName) {
            list.filter { !it.name.isNullOrBlank() }
        } else {
            list
        }

        return filteredList.groupBy { it.listId }
            .toSortedMap()
            .flatMap { it.value.sortedBy { item -> item.name } }
            .let { if (ascending) it else it.reversed() }
            .toMutableList()
    }

    private fun groupAndSortItemsByName(list: List<Item>, ascending: Boolean, hideNullName: Boolean): MutableList<Item> {
        val filteredList = if (hideNullName) {
            list.filter { !it.name.isNullOrBlank() }
        } else {
            list
        }

        return filteredList.sortedBy { it.name }
            .let { if (ascending) it else it.reversed() }
            .toMutableList()
    }

    private fun groupAndSortItemsByListIdAndName(list: List<Item>, hideNullName: Boolean): MutableList<Item> {
        val filteredList = if (hideNullName) {
            list.filter { !it.name.isNullOrBlank() }
        } else {
            list
        }

        return filteredList.groupBy { it.listId }
            .flatMap { it.value.sortedBy { item -> item.name } }
            .toMutableList()
    }

    private suspend fun fetchData(): MutableList<Item> {
        val list: MutableList<Item> = mutableListOf()
        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitInstance.api.getItems()
            }
            response.forEach { item ->
                Log.d("Output", "ID: ${item.id}, List ID: ${item.listId}, Name: ${item.name}")
                list.add(Item(item.id, item.listId, item.name))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Error loading data", Toast.LENGTH_LONG).show()
        }
        return list
    }
}