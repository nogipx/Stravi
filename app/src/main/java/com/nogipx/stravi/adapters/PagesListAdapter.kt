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

class PagesListAdapter (messyPages: List<WebPage>)
    : RecyclerView.Adapter<PagesListAdapter.MyViewHolder>() {

    private val pages = messyPages.filter { it.isNotEmpty() }

    companion object {
        const val TAG = "PagesListAdapter"
    }

    override fun getItemCount(): Int = pages.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_pages_list_item, parent, false)
        return MyViewHolder(holder)
    }

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val pageLabel: TextView = view.findViewById(R.id.pageNickname)
        val pageUrl: TextView = view.findViewById(R.id.pageUrl)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val page: WebPage = pages[position]

        holder.pageLabel.text = page.label
        holder.pageUrl.text = page.url.toString()

        holder.view.setOnClickListener {
            Log.d(TAG, "Click on $position item.")

            val context = it.context
            val extension = WebExtension.get(context, page.extensionId)

            if (extension == null) {
                Toast.makeText(context, "Extension not found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = WebPageActivity.createIntent(context, page.url, extension)
            context.startActivity(intent)

        }
    }
}