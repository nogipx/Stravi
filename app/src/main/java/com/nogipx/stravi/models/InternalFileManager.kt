package com.nogipx.stravi.models

import android.content.Context
import android.util.Log
import java.io.File

open class InternalFileManager(
    private val context: Context) {

    companion object {
        const val TAG = "InternalFileManager"
    }

    fun saveData(dirname: String, filename: String, data: ByteArray) : File {
        val dir = context.getDir(dirname, 0)
        val file = File(dir, filename)
        file.writeBytes(data)

        Log.i(TAG, "Saved file: '${file.path}'")
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

    fun dirFiles(dirname: String) : Array<File>? {
        val dir = context.getDir(dirname, 0)
        val files = dir.listFiles()

        Log.i(TAG, "Returned ${files!!.size} files from ${dir.path}. Names: ${files.joinToString { it.name }}")
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
        Log.i(TAG, "Force delete directory '${dir.path}'")
    }

    fun deleteFile(dirname: String, filename: String) {
        val dir = context.getDir(dirname, 0)
        val file = File(dir, filename)
        if (file.exists()) file.delete()
        Log.i(TAG, "Delete file: '${file.path}'")
    }

    fun deleteEmptyDirs() {
        val root = context.filesDir
        val dirs = root.listFiles()!!.filter { it.listFiles()!!.isEmpty() }
        dirs.forEach { it.delete() }
    }
}

