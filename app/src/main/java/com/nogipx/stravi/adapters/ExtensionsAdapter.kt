package com.nogipx.stravi.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nogipx.stravi.R
import com.nogipx.stravi.models.WebExtension

class ExtensionsAdapter(
    private val extensions: List<WebExtension>,
    private var selectedPosition: Int = 0)
    : RecyclerView.Adapter<ExtensionsAdapter.MyViewHolder>(){

    private lateinit var selectedHolder: MyViewHolder
    lateinit var selectedExtension: WebExtension


    override fun getItemCount(): Int = extensions.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_extension, parent, false)
        return MyViewHolder(view)
    }

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.extension_name)
        val domain: TextView = view.findViewById(R.id.extension_domain)
        val selectionIcon: ImageView = view.findViewById(R.id.extension_selectionIcon)
        val favicon: ImageView = view.findViewById(R.id.extension_favicon)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val extension = extensions[position]

        if (position == selectedPosition) {
            selectedHolder = holder
            selectedExtension = extension
        }

        holder.view.setOnClickListener {
            setSelection(position)
            selectedHolder.selectionIcon.imageTintList = null
            holder.selectionIcon.imageTintList = ColorStateList.valueOf(R.color.colorPrimary)
            selectedHolder = holder
        }

        with(holder) {
            name.text = extension.name
            domain.text = extension.domain
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setSelection(position: Int) {
        selectedPosition = position
        selectedExtension = extensions[position]
    }

    fun setSelection(extension: WebExtension) {
        val position = findPosition(extension)
        position ?: return
        setSelection(position)
    }

    private fun findPosition(extension: WebExtension): Int? {
        for ((i, localExtension) in extensions.withIndex()) {
            if (localExtension.uuid == extension.uuid) return i
        }
        return null
    }
}