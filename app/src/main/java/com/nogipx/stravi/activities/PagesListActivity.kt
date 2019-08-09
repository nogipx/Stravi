package com.nogipx.stravi.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nogipx.stravi.R
import com.nogipx.stravi.adapters.PagesListAdapter
import com.nogipx.stravi.pages

class PagesListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pages_list)

        viewManager = LinearLayoutManager(this)

        viewAdapter = PagesListAdapter(pages)

        recyclerView = findViewById<RecyclerView>(R.id.pagesList).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}
