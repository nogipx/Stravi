package com.nogipx.stravi.browser.navigation

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
import com.nogipx.stravi.common.ModelManageAdapter
import com.nogipx.stravi.models.WebExtension
import com.nogipx.stravi.web_extension.WebExtensionManageAdapter

class ExtensionsListFragment(private val selectedUuid: String = "") : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    lateinit var mListAdapter: ModelManageAdapter
    lateinit var mTracker: SelectionTracker<String>

    private lateinit var extensions: List<WebExtension>

    companion object {
        const val TAG = "ExtensionsListFragment"
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_extensions_list, container, false)

        // Find views
        mRecyclerView = view.findViewById(R.id.extension_list_recycler_view)


        // Setup data
        extensions = WebExtension().getAll(context!!)

        if (selectedUuid.isNotEmpty()) {
            mListAdapter.activeUuid = selectedUuid
        }


        // Setup recycler view
        mListAdapter = WebExtensionManageAdapter(extensions)
        mRecyclerView.apply {
            adapter = mListAdapter
            layoutManager = LinearLayoutManager(activity)
        }


        // Setup recycler selection
        mTracker = SelectionTracker.Builder<String>(
            "extensionSelection",
            mRecyclerView,
            ModelManageAdapter.ModelItemKeyProvider(mRecyclerView),
            ModelManageAdapter.ModelItemDetailsLookup(mRecyclerView),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectSingleAnything()
        ).build()

        mTracker.onRestoreInstanceState(savedInstanceState)

        mListAdapter.mTracker = mTracker


        return view
    }
}
