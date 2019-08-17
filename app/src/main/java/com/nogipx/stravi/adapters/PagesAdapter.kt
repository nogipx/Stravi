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

class PagesAdapter (messyPages: List<WebPage>)
    : RecyclerView.Adapter<PagesAdapter.MyViewHolder>() {

    private val pages = messyPages.filter { it.isNotEmpty() }

    companion object {
        const val TAG = "adapters.PagesAdapter"
    }

    override fun getItemCount(): Int = pages.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_page, parent, false)
        return MyViewHolder(holder)
    }

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val pageLabel: TextView = view.findViewById(R.id.pageNickname)
        val pageUrl: TextView = view.findViewById(R.id.pageUrl)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val page: WebPage = pages[position]

        holder.apply {
            pageLabel.text = page.label
            pageUrl.text = page.url

            view.setOnClickListener {
                Log.d(TAG, "Click on $position item.")

                val context = it.context
                val extension = WebExtension().get<WebExtension>(context, page.extensionId)

                if (extension == null) {
                    Log.e(TAG, "Extension not found. Position: $position")
                    Toast.makeText(context, "Extension not found", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val intent = WebPageActivity.createIntent(context, URL(page.url), extension)
                context.startActivity(intent)

            }
        }
    }
}