package com.nogipx.stravi.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.nogipx.stravi.R
import com.nogipx.stravi.adapters.PagesAdapter
import com.nogipx.stravi.models.WebPage
import com.nogipx.stravi.pages

class PagesActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "activities.PagesList"
        const val CREATE_WEBPAGE_REQUEST = 0
        const val EXTRA_PAGE = PageSettingsActivity.EXTRA_PAGE
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var actionButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pages_list)

        viewManager = LinearLayoutManager(this)
        viewAdapter = PagesAdapter(WebPage().getAll(applicationContext))

        recyclerView = findViewById<RecyclerView>(R.id.pagesList).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        actionButton = findViewById(R.id.pagesList_actionButton)
        actionButton.setOnClickListener {
            val intent = Intent(it.context, PageSettingsActivity::class.java)
            startActivityForResult(intent, CREATE_WEBPAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            CREATE_WEBPAGE_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    val pageJson = data!!.getStringExtra(EXTRA_PAGE)

                    if (pageJson != null) pages.add(WebPage().fromJson(pageJson))
                }
            }
        }
    }
}
