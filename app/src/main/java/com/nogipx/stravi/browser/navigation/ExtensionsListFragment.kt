package com.nogipx.stravi.browser.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.LinearLayoutManager
import com.nogipx.stravi.R
import com.nogipx.stravi.gateways.internal_storage.datatypes.WebExtension
import com.nogipx.stravi.gateways.internal_storage.datatypes_manage_lists.DatatypeManageAdapter
import com.nogipx.stravi.gateways.internal_storage.datatypes_manage_lists.WebExtensionManageAdapter
import kotlinx.android.synthetic.main.fragment_extensions_list.view.*

class ExtensionsListFragment(private val selectedUuid: String = "") : Fragment() {

    lateinit var mAdapter: DatatypeManageAdapter
    lateinit var mTracker: SelectionTracker<String>

    private lateinit var extensions: List<WebExtension>

    companion object {
        const val TAG = "ExtensionsListFragment"
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_extensions_list, container, false)

        // Setup data
        extensions = WebExtension().getAll(context!!)

        if (selectedUuid.isNotEmpty()) {
            mAdapter.activeUuid = selectedUuid
        }

        // Setup recycler view
        mAdapter = WebExtensionManageAdapter(extensions)
        view.extension_list_recycler_view.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        return view
    }
}
