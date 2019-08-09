package com.nogipx.stravi.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nogipx.stravi.R
import com.nogipx.stravi.activities.WebPageActivity
import com.nogipx.stravi.models.WebPage

class PagesListAdapter (
    private val pages: List<WebPage>
) : RecyclerView.Adapter<PagesListAdapter.MyViewHolder>() {

    companion object {
        const val TAG = "PagesListAdapter"
    }

    override fun getItemCount(): Int = pages.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_pages_list_item, parent, false)
        return MyViewHolder(holder)
    }

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val pageNickname: TextView = view.findViewById(R.id.pageNickname)
        val pageUrl: TextView = view.findViewById(R.id.pageUrl)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val page: WebPage = pages[position]
        holder.pageNickname.text = page.name
        holder.pageUrl.text = page.url.toString()

        holder.view.setOnClickListener {
            val context = it.context
            val intent = WebPageActivity.createIntent(context, page.url, page.extension!!)
            context.startActivity(intent)
            Log.d(TAG, "Click on $position item.")
        }
    }
}