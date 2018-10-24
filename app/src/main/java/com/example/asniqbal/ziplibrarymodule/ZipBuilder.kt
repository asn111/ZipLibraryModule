package com.self.ex.myapplication

import io.reactivex.rxkotlin.subscribeBy
import java.io.File


class ZipFileBulder {

    private var files: List<File>? = null
    private var filePath: String? = null
    private var outputPath: String? = null

    // get selected files
    fun getFile(files: List<File>): ZipFileBulder {
        this.files = files
        return this
    }

    // get Directory path
    fun getDirPath(filePath: String): ZipFileBulder {
        this.filePath = filePath
        return this
    }

    // get Output path
    fun getOutputPath(outputPath: String): ZipFileBulder {
        this.outputPath = outputPath
        return this
    }

    // function to zip all files in a directory
    fun zipAllFiles(): ZipFileBulder {

        if (filePath == null) {
            throw RuntimeException("File Path can not be a null value")
        } else {
            val zip = zipAll(filePath!!, outputPath!!)
            zip.subscribeBy(onNext = { println(it) },
                    onError = { it.printStackTrace() },
                    onComplete = { println("Done!") }
            )
        }
        return this
    }

    // function to zip one or different files from a directory
    fun zipSelectedFiles(): ZipFileBulder {

        if (files == null) {
            throw RuntimeException("Files can not be a null value")
        } else {
            val zip = zip(files!!, outputPath!!)

            zip.subscribeBy(onNext = { println(it) },
                    onError = { it.printStackTrace() },
                    onComplete = { println("Done!") }
            )
        }
        return this
    }

    fun build(): ZipFileBulder {

        if (outputPath == null) {
            throw RuntimeException("Output Path can not be a null value")
        }

        return this
    }

}
