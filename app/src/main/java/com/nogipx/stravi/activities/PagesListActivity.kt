package com.nogipx.stravi.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nogipx.stravi.R
import com.nogipx.stravi.adapters.PagesListAdapter
import com.nogipx.stravi.extensions
import com.nogipx.stravi.models.WebExtension
import com.nogipx.stravi.models.WebPage
import com.nogipx.stravi.pages


class PagesListActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "activities.PagesList"
        const val CREATE_WEBPAGE_REQUEST = 0
        const val EXTRA_PAGE = CreateWebPageActivity.EXTRA_PAGE
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var actionButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pages_list)

        viewManager = LinearLayoutManager(this)

        viewAdapter = PagesListAdapter(pages)


        extensions.values.forEach { it.toStorage(applicationContext) }
        WebExtension.delete(applicationContext, "mega-mult.ru")


        recyclerView = findViewById<RecyclerView>(R.id.pagesList).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        actionButton = findViewById(R.id.pagesList_actionButton)
        actionButton.setOnClickListener {
            val intent = Intent(it.context, CreateWebPageActivity::class.java)
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            CREATE_WEBPAGE_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    val pageJson = data!!.getStringExtra(EXTRA_PAGE)

                    if (pageJson != null) pages.add(WebPage.fromJson(pageJson))
                }
            }
        }
    }
}
