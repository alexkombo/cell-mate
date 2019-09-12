package com.android.cell.mate.util

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import com.android.cell.mate.ui.model.Song
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * Created by kombo on 2019-09-12.
 */
class CSVWriter(private val context: Context) {

    private var writer: FileWriter? = null

    fun createCSV() {
        val fileRoot = ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_DOWNLOADS)
        val file = File(fileRoot[1].absolutePath, "songs.csv")

        try {
            writer = FileWriter(file)
            addHeader()
            addRows(SAMPLE_DATA)
        } catch (io: IOException){
            Timber.e(io)
        } finally {
            writer?.flush()
            writer?.close()
        }
    }

    @Throws(IOException::class)
    private fun addHeader() {
        val line = buildString {
            append("Title")
            append(DEFAULT_SEPARATOR)
            append("Artist")
            append(DEFAULT_SEPARATOR)
            append("Release Year")
            append(DEFAULT_SEPARATOR)
            append("Rating")
            append(DEFAULT_SEPARATOR)
            append("Is Remix")
        }

        Timber.e(line)

        writer?.write(line)
    }

    @Throws(IOException::class)
    private fun addRows(songs: ArrayList<Song>) {
        songs.forEach {
            val lines = buildString {
                append(it.title)
                append(DEFAULT_SEPARATOR)
                append(it.artist)
                append(DEFAULT_SEPARATOR)
                append(it.releaseYear.toString())
                append(DEFAULT_SEPARATOR)
                append(it.rating.toString())
                append(DEFAULT_SEPARATOR)
                append(it.isRemix.toString())
            }

            Timber.e(lines)

            writer?.write(lines)
        }
    }

    companion object {
        const val DEFAULT_SEPARATOR = ","
        private val SAMPLE_DATA = arrayListOf(
            Song("What To Do", "&Me", 2009, 9.8F, true),
            Song("Webaba", "Culoe De Song", 2016, 9.0F, false),
            Song("Lavendar Boogie", "Tampa", 2019, 8.8F, false),
            Song("Olappa", "Lunar", 2018, 8.0F, true),
            Song("The Journey", "Black Motion", 2017, 8.6F, false)
        )
    }
}