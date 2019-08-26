package com.nogipx.stravi.models

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import java.io.File
import java.util.*

open class InternalStorage (
    val directory: String = "") {

    @Expose val uuid: String = UUID.randomUUID().toString()

    companion object {
        const val TAG = "InternalStorage"
    }

    inline fun <reified T : InternalStorage> fromJson(json: String) : T {
        val item = Gson().fromJson(json, T::class.java)
        Log.i(TAG, "Restore from json $item")
        return item
    }

    inline fun <reified T : InternalStorage> get(context: Context, uuid: String) : T? {
        if (uuid.isEmpty()) return null

        val item = getAll<T>(context).find { it.uuid == uuid }
        if (item != null)
            Log.i(TAG, "Got $item")

        return item
    }

    inline fun <reified T : InternalStorage> getAll(context: Context): List<T> {
        return InternalFileManager(context)
            .dirFiles(directory)!!.map { fromJson<T>(it.readText()) }
    }

    fun deleteDirectory(context: Context) =
        InternalFileManager(context).deleteDir(directory)

    fun deleteFiles(context: Context) =
        InternalFileManager(context).dirFiles(directory)?.forEach { it.delete() }

    fun isFilenameFree(context: Context, filename: String) =
        InternalFileManager(context).isFilenameFree(directory, filename)

    open fun toJson(): String {
        val json = GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
            .toJson(this)
        return json
    }

    protected fun className() : String =
        this.javaClass.toString().split(".").last()

    override fun toString(): String =
        "${className()}: uuid:$uuid"

    open fun save(context: Context,
                  filename: String = uuid,
                  data: ByteArray = toJson().toByteArray()) : File =
        InternalFileManager(context).saveData(directory, filename, data)

    open fun delete(context: Context, filename: String = uuid) =
        InternalFileManager(context).deleteFile(directory, filename)
}