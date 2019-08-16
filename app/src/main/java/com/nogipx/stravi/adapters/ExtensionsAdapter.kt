package com.nogipx.stravi.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.nogipx.stravi.R
import com.nogipx.stravi.models.WebExtension

class ExtensionsAdapter(
    private val extensions: List<WebExtension>,
    private val defaultPosition: Int = 0)
    : RecyclerView.Adapter<ExtensionsAdapter.MyViewHolder>(){

    var activeUuid: String = ""
    var mTracker: SelectionTracker<String>? = null

    var activeExtensions: List<WebExtension> = extensions

    companion object {
        const val TAG = "ExtensionAdapter"
    }

    override fun getItemCount(): Int = activeExtensions.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_extension, parent, false)
        return MyViewHolder(view)
    }


    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.extension_name)
        val domain: TextView = view.findViewById(R.id.extension_domain)
        val selectionIcon: ImageView = view.findViewById(R.id.extension_selectionIcon)


        fun getItemDetails(adapter: ExtensionsAdapter) : ItemDetailsLookup.ItemDetails<String> =
           object : ItemDetailsLookup.ItemDetails<String>() {
               override fun getSelectionKey(): String? =
                   adapter.activeExtensions[layoutPosition].uuid

               override fun getPosition(): Int = layoutPosition
           }

        fun setActivation(activation: Boolean) {
            view.isActivated = activation
            if (view.isActivated) onActivated()
            else onDeactivated()
        }

        @SuppressLint("ResourceAsColor")
        fun onActivated() {
            Log.v(TAG, "Activate holder: Name: ${name.text}")
            selectionIcon.visibility = View.VISIBLE
//            view.setBackgroundColor(R.color.backgroundColor)
        }

        @SuppressLint("ResourceAsColor")
        fun onDeactivated() {
            Log.v(TAG, "Deactivate holder: Name: ${name.text}")
            selectionIcon.visibility = View.INVISIBLE
//            view.setBackgroundColor(R.color.white)
        }
    }


    class MyItemDetailsLookup(private val recyclerView: RecyclerView)
        : ItemDetailsLookup<String>() {
        override fun getItemDetails(e: MotionEvent): ItemDetails<String>? {
            val view = recyclerView.findChildViewUnder(e.x, e.y)
            if (view != null) {
                val holder = recyclerView.getChildViewHolder(view) as MyViewHolder
                return holder.getItemDetails(recyclerView.adapter as ExtensionsAdapter)
            }
            return null
        }
    }

    class MyItemKeyProvider(private val recyclerView: RecyclerView) :
        ItemKeyProvider<String>(SCOPE_MAPPED) {

        override fun getKey(position: Int): String? {
            val mAdapter = recyclerView.adapter as ExtensionsAdapter
            val extensions = mAdapter.activeExtensions
            if (position > extensions.size - 1) return null
            val uuid = extensions[position].uuid

            Log.d(TAG, "getKey(position=$position) -> uuid: $uuid")
            return uuid
        }

        override fun getPosition(key: String): Int {
            val mAdapter = recyclerView.adapter as ExtensionsAdapter
            val extensions = mAdapter.activeExtensions

            for ((i, v) in extensions.withIndex()) {
                if (v.uuid == key) {
                    Log.d(TAG, "getPosition(uuid=$key) -> position: $i")
                    return i
                }
            }

            return RecyclerView.NO_POSITION
        }
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (activeExtensions.isEmpty()) return

        val extension = activeExtensions[position]

        mTracker?.let {
            val isActivated = it.isSelected(extension.uuid)
            holder.setActivation(isActivated)
        }

        // Fill data to holder
        holder.apply {
            name.text = extension.name
            domain.text = extension.domain
        }

        // Change selection on item click
        holder.view.setOnClickListener {
            mTracker?.select(extension.uuid)
        }

    }

    fun filterByDomain(domain: String) {
        if (domain.isEmpty()) unfilter()
        else {
            activeExtensions = extensions.filter { it.domain.startsWith(domain) }
            Log.v(TAG, "Filter result: ${activeExtensions.size} extensions")
        }
        notifyDataSetChanged()
    }

    fun unfilter() {
        activeExtensions = extensions
        Log.v(TAG, "Unfiltered")
        notifyDataSetChanged()
    }
}

