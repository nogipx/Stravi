package com.nogipx.stravi.gateways.internal_storage.datatypes_manage_lists

import android.util.Log
import android.widget.Toast
import com.nogipx.stravi.browser.WebBrowserActivity
import com.nogipx.stravi.gateways.internal_storage.datatypes.WebExtension
import com.nogipx.stravi.gateways.internal_storage.datatypes.WebTab
import java.net.URL

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

            view.setOnClickListener {

                val context = it.context
                val extension = WebExtension()
                    .get<WebExtension>(context, tab.extensionId)

                if (extension == null) {
                    val msg = "Extension with UUID='${tab.extensionId}' not found."
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "$msg Position: $position")
                    return@setOnClickListener
                }

                Log.i(TAG, "Extension found. $extension")

            }
        }
    }


}