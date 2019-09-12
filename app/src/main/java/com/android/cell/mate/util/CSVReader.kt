package com.android.cell.mate.util

import android.content.Context
import android.net.Uri
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

/**
 * Created by kombo on 2019-09-11.
 */
class CSVReader(private val context: Context, private val delimiter: String) {

    private var contentMaps: ArrayList<Map<String, ArrayList<String>>>? = null
    private var headerNames: ArrayList<String>? = null

    fun readCSV(uri: Uri): ArrayList<Map<String, ArrayList<String>>> {

        val inputStream = context.contentResolver.openInputStream(uri)
        val inputStreamReader = InputStreamReader(inputStream!!)
        val bufferedReader = BufferedReader(inputStreamReader)

        val list = bufferedReader.lines().limit(1).collect(Collectors.toList())
        contentMaps = getHeaders(list.first())

        bufferedReader.useLines { lines ->
            lines.forEach { line ->
                val cellItems =
                    line.split(delimiter.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                for ((index, value) in cellItems.withIndex()) {
                    contentMaps!![index][headerNames!![index]]?.add(value)
                }
            }
        }

        return contentMaps!!
    }

    private fun getHeaders(headerStream: String): ArrayList<Map<String, ArrayList<String>>> {
        val headers =
            headerStream.split(delimiter.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        headerNames = ArrayList()
        val items = ArrayList<Map<String, ArrayList<String>>>()

        for (header in headers) {
            headerNames!!.add(header)
            items.add(mapOf(header to ArrayList()))
        }

        return items
    }
}