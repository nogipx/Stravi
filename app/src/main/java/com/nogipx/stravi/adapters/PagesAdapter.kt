package com.nogipx.stravi.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.nogipx.stravi.R
import com.nogipx.stravi.activities.WebPageActivity
import com.nogipx.stravi.models.WebExtension
import com.nogipx.stravi.models.WebPage
import java.net.URL

class PagesAdapter (defaultPages: List<WebPage>)
    : RecyclerView.Adapter<PagesAdapter.MyViewHolder>() {

    var activePages: List<WebPage> = defaultPages

    companion object {
        const val TAG = "PagesAdapter"
    }

    override fun getItemCount(): Int = activePages.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_page, parent, false)
        return MyViewHolder(holder)
    }

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val mPageLabel: TextView = view.findViewById(R.id.pageNickname)
        val mPageUrl: TextView = view.findViewById(R.id.pageUrl)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val page: WebPage = activePages[position]

        holder.apply {
            mPageLabel.text = page.label
            mPageUrl.text = page.url

            view.setOnClickListener {

                val context = it.context
                val extension = WebExtension().get<WebExtension>(context, page.extensionId)

                if (extension == null) {
                    val msg = "Extension with UUID='${page.extensionId}' not found."
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "$msg Position: $position")
                    return@setOnClickListener
                }

                Log.i(TAG, "Extension found. $extension")
                val intent = WebPageActivity.createIntent(context, URL(page.url), extension)
                context.startActivity(intent)

            }
        }
    }
}