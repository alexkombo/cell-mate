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

    fun readCSV(path: String): ArrayList<ArrayList<String>> {
        var lines = ArrayList<ArrayList<String>>()

//        if (isFileAvailable(path)) {
        val inputStream = context.assets.open(path)
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)

//        lines = bufferedReader.lines().collect(Collectors.toList())
//            val bufferedReader = BufferedReader(FileReader(path))

//        lines.forEach {
//            Timber.e(it)
//        }


        bufferedReader.forEachLine { line ->
            val cellItems = line.split(delimiter.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            cellItems.forEach {
                Timber.e(it)
            }
//            cellItems.forEach {
//                val items = ArrayList<String>()
//                items.add(it)
//                lines.add(items)
//            }
        }

//        lines.forEach {
//            Timber.e("${it[0]} / ${it[1]}")
//        }

//        while (bufferedReader.readLine() != null) {
//            val line = bufferedReader.readLine()
//
//            val cellItems = line?.split(delimiter)
//
////            if (cellItems != null) {
//                cellItems?.forEach {
//                    Timber.e(it)
//                }
////                lines.addAll(cellItems)
////            }
//
//        } else {
//            Timber.e("File does not exist")
//        }

        return lines
    }

    @Throws(FileNotFoundException::class)
    private fun isFileAvailable(path: String): Boolean {
        val file = File(path)

        return file.isFile
    }
}