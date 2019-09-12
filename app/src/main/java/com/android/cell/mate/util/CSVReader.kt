package com.android.cell.mate.util

import android.content.Context
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader

/**
 * Created by kombo on 2019-09-11.
 */
class CSVReader(private val context: Context, private val delimiter: String) {

    fun readCSV(path: String): ArrayList<String> {
        var lines = ArrayList<String>()

//        if (isFileAvailable(path)) {
        val inputStream = context.assets.open(path)
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)

//        bufferedReader.useLines { l ->
//            l.forEach { Timber.e(it) }
//        }

        bufferedReader.forEachLine { line ->
            val cellItems =
                line.split(delimiter.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val stringBuilder = StringBuilder()

            for(i in cellItems)
                stringBuilder.append(i).append(" ")

            Timber.e(stringBuilder.toString())

            lines.add(stringBuilder.toString())
        }

        return lines
    }

    @Throws(FileNotFoundException::class)
    private fun isFileAvailable(path: String): Boolean {
        val file = File(path)

        return file.isFile
    }
}