package com.nogipx.stravi.gateways.internal_storage.datatypes_manage_lists

import com.nogipx.stravi.gateways.internal_storage.datatypes.WebTab

class TabsManageAdapter (
    private val defaultTabs: List<WebTab>
)   : DatatypeManageAdapter(defaultTabs) {

    companion object {
        const val TAG = "TabsManageAdapter"
    }

    override fun onBindViewHolder(holder: DatatypeViewHolder, position: Int) {
        val tab: WebTab = defaultTabs[position]

        holder.apply {
            primaryText.text = tab.title
            extraText.text = tab.url
        }
    }


}