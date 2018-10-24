package com.self.ex.myapplication

import android.util.Log
import io.reactivex.Observable
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipOutputStream

private const val TAG = "Compress"
private const val BUFFER_SIZE = 2048

// it will take list of file/files and output path as parameters.
fun zip(filesToSend: List<File>, outputPath: String): Observable<String> {

    return Observable.create {

        try {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(outputPath))).use { zos ->

                for (f in filesToSend) {
                    if (f.exists() && !f.name.contains(".zip")) { // if folder is present and not a zipped folder

                        //Write file to zip
                        writeToZip(f, zos, createZipEntry(f.name, f))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        it.onNext(outputPath)
        it.onComplete()
    }
}

// it will take directory path as string and output path where file will be saved.
fun zipAll(directory: String, outputPath: String): Observable<Boolean> {
    val sourceFile = File(directory)

    return Observable.create {
        val emitter = it

        ZipOutputStream(BufferedOutputStream(FileOutputStream(outputPath))).use {
            it.use {
                zipFiles(it, sourceFile, "")
                emitter.onNext(true)
                emitter.onComplete()
            }
        }

    }
}

private fun zipFiles(zipOut: ZipOutputStream, sourceFile: File, parentDirPath: String) {

    for (f in sourceFile.listFiles()) {

        if (f.isDirectory) {

            val path = f.name + File.separator
            try {
                zipOut.putNextEntry(createZipEntry(path, f))
            } catch (e: ZipException) {
                Log.e(TAG, e.message)
            }
            Log.i(TAG, "Adding directory: $path")

            //Call recursively to add files within this directory
            zipFiles(zipOut, f, f.name)
        } else {
            if (!f.name.contains(".zip")) { //If folder contains a file with extension ".zip", skip it
                val path = parentDirPath + File.separator + f.name

                //Write file to zip
                writeToZip(f, zipOut, createZipEntry(path, f))
            } else {
                zipOut.closeEntry()
                zipOut.close()
            }
        }
    }
}

private fun createZipEntry(path: String, f: File): ZipEntry {

    val entry = ZipEntry(path)
    entry.time = f.lastModified()
    entry.isDirectory
    entry.size = f.length()

    return entry
}

private fun writeToZip(f: File, zos: ZipOutputStream, zipEntry: ZipEntry) {
    val data = ByteArray(BUFFER_SIZE)

    FileInputStream(f).use { fi ->
        BufferedInputStream(fi).use { origin ->

            try {
                zos.putNextEntry(zipEntry)
            } catch (e: ZipException) {
                Log.e(TAG, e.message)
            }
            Log.i(TAG, "Adding file: ${f.path}")

            while (true) {
                val readBytes = origin.read(data)
                if (readBytes == -1) {
                    break
                }
                try {
                    zos.write(data, 0, readBytes)
                } catch (e: ZipException) {
                    Log.e(TAG, e.message)
                }
            }
        }
    }
}
