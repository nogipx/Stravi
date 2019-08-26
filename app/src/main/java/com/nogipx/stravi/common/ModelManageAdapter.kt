package com.nogipx.stravi.common

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
import com.nogipx.stravi.models.InternalStorage

open class ModelManageAdapter(
    protected val defaultItems: List<InternalStorage>
)   : RecyclerView.Adapter<ModelManageAdapter.ModelViewHolder>(){

    var activeUuid: String = ""
    var mTracker: SelectionTracker<String>? = null

    var activeItems: List<InternalStorage> = defaultItems
    var selectedItem: InternalStorage? = null

    companion object {
        const val TAG = "ModelManageAdapter"
    }

    override fun getItemCount(): Int = activeItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_web_extension, parent, false)
        return ModelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        if (activeItems.isEmpty()) return

        val item = activeItems[position]

        mTracker?.let {
            val isActivated = it.isSelected(item.uuid)
            holder.setActivation(isActivated)
        }

        // Change selection on item click
        holder.view.setOnClickListener {
            mTracker?.select(item.uuid)
            selectedItem = activeItems[position]
        }
    }


    open class ModelViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.extension_name)
        val domain: TextView = view.findViewById(R.id.extension_domain)
        val selectionIcon: ImageView = view.findViewById(R.id.extension_selectionIcon)


        fun getItemDetails(listAdapter: ModelManageAdapter) : ItemDetailsLookup.ItemDetails<String> =
           object : ItemDetailsLookup.ItemDetails<String>() {
               override fun getSelectionKey(): String? =
                   listAdapter.activeItems[layoutPosition].uuid

               override fun getPosition(): Int = layoutPosition
           }

        fun setActivation(activation: Boolean) {
            view.isActivated = activation
            if (view.isActivated) onActivated()
            else onDeactivated()
        }

        private fun onActivated() {
            Log.i(TAG, "Activate holder: $this")
            selectionIcon.visibility = View.VISIBLE
        }

        private fun onDeactivated() {
            Log.v(TAG, "Deactivate holder: $this")
            selectionIcon.visibility = View.INVISIBLE
        }

        override fun toString(): String = "Name:${name.text}"
    }

    fun filter(newActiveItems: List<InternalStorage>) {
        activeItems = newActiveItems
        Log.i(TAG, "Filtered ${newActiveItems.size} items")
        notifyDataSetChanged()
    }

    fun resetFilter() {
        activeItems = defaultItems
        Log.i(TAG, "Filter reset")
        notifyDataSetChanged()
    }


    class ModelItemDetailsLookup(private val recyclerView: RecyclerView)
        : ItemDetailsLookup<String>() {
        override fun getItemDetails(e: MotionEvent): ItemDetails<String>? {
            val view = recyclerView.findChildViewUnder(e.x, e.y)
            if (view != null) {
                val holder = recyclerView.getChildViewHolder(view) as ModelViewHolder
                return holder.getItemDetails(recyclerView.adapter as ModelManageAdapter)
            }
            return null
        }
    }


    /**
     * Use particular extension's uuid as key for its view holder.
     */
    class ModelItemKeyProvider(private val recyclerView: RecyclerView) :
        ItemKeyProvider<String>(SCOPE_MAPPED) {

        override fun getKey(position: Int): String? {
            val mAdapter = recyclerView.adapter as ModelManageAdapter
            val extensions = mAdapter.activeItems
            if (position > extensions.size - 1) return null
            val uuid = extensions[position].uuid

            Log.d(TAG, "position: $position -> uuid: $uuid")
            return uuid
        }

        override fun getPosition(key: String): Int {
            val mAdapter = recyclerView.adapter as ModelManageAdapter
            val extensions = mAdapter.activeItems

            for ((i, v) in extensions.withIndex()) {
                if (v.uuid == key) {
                    Log.d(TAG, "uuid: $key -> position: $i")
                    return i
                }
            }

            return RecyclerView.NO_POSITION
        }
    }
}

