package com.nogipx.stravi.models

import android.content.Context
import android.util.Log
import java.io.File

open class InternalManager(
    private val context: Context) {

    companion object {
        const val TAG = "models.InternalManager"
    }

    fun toStorage(dirname: String, filename: String, data: String) : File {
        val dir = context.getDir(dirname, 0)
        val file = File(dir, filename)
        file.writeBytes(data.toByteArray())

        Log.d(TAG, "Saved file: '${file.path}'")
        return file
    }

    fun fromStorage(dirname: String, filename: String) : File? {
        val dir = context.getDir(dirname, 0)
        val file = File(dir, filename)
        return if (file.exists()) file
        else {
            dir.delete()
            null
        }
    }

    fun allFromStorage(dirname: String) : Array<File>? {
        val dir = context.getDir(dirname, 0)
        val files = dir.listFiles()

        Log.d(TAG, "Returned ${files!!.size} files from ${dir.path}")
        return files
    }

    fun isFilenameFree(dirname: String, filename: String) : Boolean {
        val dir = context.getDir(dirname, 0)
        val file = File(dir, filename)
        return if (file.exists()) false
        else {
            dir.delete()
            true
        }
    }

    fun deleteDir(dirname: String) {
        val dir = context.getDir(dirname, 0)
        dir.deleteRecursively()
        Log.d(TAG, "Force delete directory '${dir.path}'")
    }

    fun deleteFile(dirname: String, filename: String) {
        val dir = context.getDir(dirname, 0)
        val file = File(dir, filename)

        file.delete()
        Log.d(TAG, "Delete file: '${file.path}'")
    }

    fun deleteEmptyDirs() {
        val root = context.filesDir
        val dirs = root.listFiles()!!.filter { it.listFiles()!!.isEmpty() }
        dirs.forEach { it.delete() }
    }
}

