package com.nogipx.stravi.gateways.internal_storage.datatypes_manage_lists

import com.nogipx.stravi.gateways.internal_storage.datatypes.WebExtension

class WebExtensionManageAdapter(
    var extensions: List<WebExtension>
)   : DatatypeManageAdapter(extensions) {

    override fun onBindViewHolder(holder: DatatypeViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val extension = extensions[position]

        holder.apply {
            primaryText.text = extension.name
            extraText.text = extension.host
        }
    }

    fun filterByHost(host: String) {
        if (host.isEmpty()) super.resetFilter()
        else {
            val newActive = extensions.filter {
                it.host.startsWith(host)
            }
            super.filter(newActive)
        }
    }
}