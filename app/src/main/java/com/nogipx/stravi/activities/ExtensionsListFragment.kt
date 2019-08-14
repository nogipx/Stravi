package com.nogipx.stravi.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nogipx.stravi.R
import com.nogipx.stravi.adapters.ExtensionsAdapter
import com.nogipx.stravi.models.WebExtension

class ExtensionsListFragment(
    private val selectedUuid: String = "",
    private val creatable: Boolean = false) : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ExtensionsAdapter

    private var extensions: List<WebExtension> = WebExtension().getAll(context!!)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_extensions_list, container, false)
        mAdapter = ExtensionsAdapter(extensions)

        if (selectedUuid.isNotEmpty()) {
            val extension = extensions.find { it.uuid == selectedUuid }
            if (extension != null) mAdapter.setSelection(extension)
        }

        mRecyclerView = view.findViewById(R.id.extensionList_recyclerView)
        mRecyclerView.adapter = mAdapter
        return view
    }

    fun filterByDomain(domain: String): ExtensionsListFragment {
        extensions = extensions.filter { it.domain == domain }
        mAdapter.notifyDataSetChanged()
        return this
    }

    fun getSelectedExtension() = mAdapter.selectedExtension
}
