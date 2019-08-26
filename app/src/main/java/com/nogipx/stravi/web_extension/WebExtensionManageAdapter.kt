package com.nogipx.stravi.web_extension

import com.nogipx.stravi.common.ModelManageAdapter
import com.nogipx.stravi.models.WebExtension

class WebExtensionManageAdapter(
    var extensions: List<WebExtension>
)   : ModelManageAdapter(extensions) {

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val extension = extensions[position]

        holder.apply {
            name.text = extension.name
            domain.text = extension.host
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