package com.nogipx.stravi.models

import android.content.Context
import com.google.gson.annotations.Expose

class LastOpenedTab(
    @Expose val lastUuid: String = ""
)   : InternalStorage("last_opened_page") {

    companion object {
        const val TAG = "LastOpenedTab"
    }

    fun getUuid(context: Context) : LastOpenedTab {
        val files = getAll<LastOpenedTab>(context)

        return if (files.isNotEmpty())
            files.first()
        else this
    }

    fun peekTab(context: Context) : WebTab {
        return if (lastUuid.isNotEmpty())
            WebTab().get(context, lastUuid) ?: WebTab()

        else WebTab()
    }

    fun save(context: Context) {
        deleteFiles(context)
        super.save(context, uuid, toJson().toByteArray())
    }

}