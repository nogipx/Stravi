package com.nogipx.stravi.models

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import java.io.File
import java.util.*

open class InternalStorage (
    val directory: String = "",
    private val debug: Boolean = false) {

    @Expose val uuid: String = UUID.randomUUID().toString()

    companion object {
        const val TAG = "InternalStorage"
    }

    fun debug(msg: String) {
        if (debug) Log.i(TAG, msg)
    }

    inline fun <reified T : InternalStorage> fromJson(json: String) : T {
        val item = Gson().fromJson(json, T::class.java)
        debug("Restore <${className<T>()}> from json. UUID: ${item.uuid}")
        return item
    }

    inline fun <reified T : InternalStorage> get(context: Context, uuid: String) : T? {
        val item = getAll<T>(context).find { it.uuid == uuid }
        if (item != null)
            debug("Got <${className<T>()}> UUID: ${item.uuid}")
        return item
    }

    inline fun <reified T : InternalStorage> getAll(context: Context): List<T> {
        val items = InternalFileManager(context).dirFiles(directory)!!.map { fromJson<T>(it.readText()) }
        debug("Got ${items.size} <${className<T>()}> items")
        return items
    }

    inline fun <reified T> className() : String =
        T::class.java.toString().split(".").last()

    fun deleteAll(context: Context) =
        InternalFileManager(context).deleteDir(directory)

    fun isFilenameFree(context: Context, filename: String) =
        InternalFileManager(context).isFilenameFree(directory, filename)

    open fun toJson(): String {
        val json = GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
            .toJson(this)
        debug("<${this.javaClass}> to json result: $json")
        return json
    }

    open fun save(context: Context, filename: String, data: ByteArray) : File =
        InternalFileManager(context).saveData(directory, filename, data)

    open fun delete(context: Context, filename: String) =
        InternalFileManager(context).deleteFile(directory, filename)
}