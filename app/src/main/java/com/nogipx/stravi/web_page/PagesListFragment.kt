package com.nogipx.stravi.web_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nogipx.stravi.R
import com.nogipx.stravi.models.WebTab

class PagesListFragment : Fragment() {

    companion object {

        private const val TAG = "activities.PagesList"
        const val CREATE_WEBPAGE_REQUEST = 1
    }

    private lateinit var mTabs: List<WebTab>

    private lateinit var mRecyclerView: RecyclerView

    private lateinit var mPagesListAdapter: PagesListAdapter
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var mActionButton: FloatingActionButton


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_pages_list, container, false)

        mTabs = WebTab().getAll(activity!!.applicationContext)

        mLayoutManager = LinearLayoutManager(activity)
        mPagesListAdapter = PagesListAdapter(mTabs)

        mRecyclerView = view.findViewById<RecyclerView>(R.id.pagesList).apply {
            layoutManager = mLayoutManager
            adapter = mPagesListAdapter
        }

        mActionButton = view.findViewById(R.id.pagesList_actionButton)

        return view
    }
}
