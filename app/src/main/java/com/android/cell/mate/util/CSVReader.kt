package com.android.cell.mate.util

import android.content.Context
import android.net.Uri
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

/**
 * Created by kombo on 2019-09-11.
 */

/**
 * Does the actual CSV parsing
 * @param context Context of the activity
 * @param delimiter separator entered by user
 */
class CSVReader(private val context: Context, private val delimiter: String) {

    private var contentMaps: ArrayList<Map<String, ArrayList<String>>>? = null
    private var headerNames: ArrayList<String>? = null

    fun readCSV(uri: Uri): ArrayList<Map<String, ArrayList<String>>> {
        //open file and use bufferedReader to read content
        val inputStream = context.contentResolver.openInputStream(uri)
        val inputStreamReader = InputStreamReader(inputStream!!)
        val bufferedReader = BufferedReader(inputStreamReader)

        //get the first row which contains headers
        val list = bufferedReader.lines().limit(1).collect(Collectors.toList())
        contentMaps = getHeaders(list.first())

        //read each line
        bufferedReader.useLines { lines ->

            //iterate through each line
            lines.forEach { line ->
                //split the string by provided delimiter
                val cellItems =
                    line.split(delimiter.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                //split data by columns and write to the array of maps
                for ((index, value) in cellItems.withIndex()) {
                    contentMaps!![index][headerNames!![index]]?.add(value)
                }
            }
        }

        return contentMaps!!
    }

    /**
     * Creates an array that contains a map of the header names and an array to hold the corresponding column values
     * Reading the data in columns allows for additional processing of data since each array contains data of one type
     */
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