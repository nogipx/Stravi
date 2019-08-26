package com.nogipx.stravi.web_page

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.nogipx.stravi.R
import com.nogipx.stravi.browser.WebBrowserActivity
import com.nogipx.stravi.models.WebExtension
import com.nogipx.stravi.models.WebTab
import java.net.URL

class PagesListAdapter (defaultTabs: List<WebTab>)
    : RecyclerView.Adapter<PagesListAdapter.MyViewHolder>() {

    var activeTabs: List<WebTab> = defaultTabs

    companion object {
        const val TAG = "PagesListAdapter"
    }

    override fun getItemCount(): Int = activeTabs.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_web_tab, parent, false)
        return MyViewHolder(holder)
    }

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val mPageLabel: TextView = view.findViewById(R.id.pageNickname)
        val mPageUrl: TextView = view.findViewById(R.id.pageUrl)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val tab: WebTab = activeTabs[position]

        holder.apply {
            mPageLabel.text = tab.title
            mPageUrl.text = tab.url

            view.setOnClickListener {

                val context = it.context
                val extension = WebExtension().get<WebExtension>(context, tab.extensionId)

                if (extension == null) {
                    val msg = "Extension with UUID='${tab.extensionId}' not found."
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "$msg Position: $position")
                    return@setOnClickListener
                }

                Log.i(TAG, "Extension found. $extension")
                val intent = WebBrowserActivity.createIntent(context, URL(tab.url), extension)
                context.startActivity(intent)

            }
        }
    }


}