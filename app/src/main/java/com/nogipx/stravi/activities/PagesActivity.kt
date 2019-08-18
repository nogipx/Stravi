package com.nogipx.stravi.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nogipx.stravi.R
import com.nogipx.stravi.adapters.PagesAdapter
import com.nogipx.stravi.models.WebPage

class PagesActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "activities.PagesList"
        const val CREATE_WEBPAGE_REQUEST = 1
    }

    private lateinit var mPages: List<WebPage>

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPagesAdapter: PagesAdapter
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    private lateinit var mActionButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pages_list)

//        pages.forEach {it.save(applicationContext)}
//        extensions.values.forEach {it.save(applicationContext)}

        mPages = WebPage().getAll(applicationContext)

        mLayoutManager = LinearLayoutManager(this)
        mPagesAdapter = PagesAdapter(mPages)

        mRecyclerView = findViewById<RecyclerView>(R.id.pagesList).apply {
            layoutManager = mLayoutManager
            adapter = mPagesAdapter
        }

        mActionButton = findViewById(R.id.pagesList_actionButton)
        mActionButton.setOnClickListener {
            val intent = Intent(it.context, PageSettingsActivity::class.java)
            startActivityForResult(intent, CREATE_WEBPAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            CREATE_WEBPAGE_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    val pageJson = data!!.getStringExtra(PageSettingsActivity.EXTRA_PAGE)

                    if (pageJson != null) {
                        val item = WebPage().fromJson<WebPage>(pageJson)

                        item.save(applicationContext)

                        mPagesAdapter.activePages = WebPage().getAll(applicationContext)
                        mPagesAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}
