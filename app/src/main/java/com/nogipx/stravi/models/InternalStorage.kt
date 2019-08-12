package com.nogipx.stravi.models

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import java.io.File
import java.util.*

open class InternalStorage (val directory: String = "") {

    @Expose val uuid: String = UUID.randomUUID().toString().replace("-", "")

    inline fun <reified T : InternalStorage> fromJson(json: String) : T =
        Gson().fromJson(json, T::class.java)

    inline fun <reified T : InternalStorage> get(context: Context, uuid: String) : T? =
        getAll<T>(context).find { it.uuid == uuid }

    inline fun <reified T : InternalStorage> getAll(context: Context): List<T> =
        InternalManager(context).dirFiles(directory)!!.map { fromJson<T>(it.readText()) }

    fun deleteAll(context: Context) =
        InternalManager(context).deleteDir(directory)

    fun isFilenameFree(context: Context, filename: String) =
        InternalManager(context).isFilenameFree(directory, filename)

    open fun toJson(): String = GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .create()
        .toJson(this)

    open fun save(context: Context, filename: String, data: ByteArray) : File =
        InternalManager(context).saveData(directory, filename, data)

    open fun delete(context: Context, filename: String) =
        InternalManager(context).deleteFile(directory, filename)
}