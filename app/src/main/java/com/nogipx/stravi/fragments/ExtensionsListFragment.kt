package com.nogipx.stravi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nogipx.stravi.R
import com.nogipx.stravi.adapters.ExtensionsAdapter
import com.nogipx.stravi.models.WebExtension

class ExtensionsListFragment(private val selectedUuid: String = "") : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: ExtensionsAdapter
    lateinit var mTracker: SelectionTracker<String>

    private lateinit var extensions: List<WebExtension>

    companion object {
        const val TAG = "ExtensionsListFragment"
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_extensions_list, container, false)

        // Find views
        mRecyclerView = view.findViewById(R.id.extensionList_recyclerView)


        // Setup data
        extensions = WebExtension().getAll(context!!)

        if (selectedUuid.isNotEmpty()) {
            val extension = extensions.find { it.uuid == selectedUuid }
            if (extension != null) mAdapter.activeUuid = extension.uuid
        }


        // Setup recycler view
        mAdapter = ExtensionsAdapter(extensions)
        mRecyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(activity)
        }


        // Setup recycler selection
        mTracker = SelectionTracker.Builder<String>(
            "extensionSelection",
            mRecyclerView,
            ExtensionsAdapter.MyItemKeyProvider(mRecyclerView),
            ExtensionsAdapter.MyItemDetailsLookup(mRecyclerView),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectSingleAnything()
        ).build()

        mTracker.onRestoreInstanceState(savedInstanceState)

        mAdapter.mTracker = mTracker


        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
}
