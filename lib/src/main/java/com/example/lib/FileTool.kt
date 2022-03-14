package com.example.lib

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class FileTool {

    companion object {

        fun writeFile(path: String, data: String) {
            val file = File(path)
            if (!createOrExistsFile(path)) return
            file.printWriter().use { out ->
                out.write(data)
            }
        }

        fun createOrExistsFile(path: String): Boolean {
            val file = File(path)
            if (file.exists()) return file.isFile
            if (!createOrExistDir(file.parentFile.path)) return false
            return file.createNewFile()
        }

        fun createOrExistDir(path: String): Boolean {
            val file = File(path)
            return if (file.exists()) {
                file.isDirectory
            } else {
                file.mkdirs()
            }
        }
    }
}