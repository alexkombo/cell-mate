package com.android.cell.mate.util

import android.content.Context
import com.android.cell.mate.ui.model.Song
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.Writer

/**
 * Created by kombo on 2019-09-12.
 */
class CSVWriter(private val context: Context, private var separator: String?) {

    private var fileWriter: FileWriter? = null

    fun createCSV() {
        val fileRoot = context.filesDir
        val file = File(fileRoot.absolutePath, "songs.csv")

        try {
            fileWriter = FileWriter(file)
            fileWriter?.let {
                addLine(it, listOf("Title", "Artist", "Release Year", "Rating", "Is Remix"))

                for (song in SAMPLE_DATA) {
                    addLine(
                        it,
                        listOf(
                            song.title,
                            song.artist,
                            song.releaseYear.toString(),
                            song.rating.toString(),
                            song.isRemix.toString()
                        )
                    )
                }
            }
            fileWriter?.flush()
            fileWriter?.close()
        } catch (io: IOException) {
            Timber.e(io)
        }
    }

    @Throws(IOException::class)
    private fun addLine(writer: Writer, values: List<String>) {
        var firstItem = true

        if (separator == null)
            separator = DEFAULT_SEPARATOR

        val lines = StringBuilder()

        for (value in values) {
            if (!firstItem) {
                lines.append(separator)
            }

            lines.append(value)

            firstItem = false
        }

        lines.append("\n")

        Timber.e(lines.toString())
        writer.append(lines.toString())
    }

    companion object {
        const val DEFAULT_SEPARATOR = ","
        private val SAMPLE_DATA = listOf(
            Song("What To Do", "&Me", 2009, 9.8F, true),
            Song("Webaba", "Culoe De Song", 2016, 9.0F, false),
            Song("Lavendar Boogie", "Tampa", 2019, 8.8F, false),
            Song("Olappa", "Lunar", 2018, 8.0F, true),
            Song("The Journey", "Black Motion", 2017, 8.6F, false)
        )
    }
}