package com.android.cell.mate.util

import android.content.Context
import com.android.cell.mate.event.ErrorEvent
import com.android.cell.mate.event.WriteEvent
import com.android.cell.mate.ui.model.Song
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.Writer

/**
 * Created by kombo on 2019-09-12.
 */

/**
 * Creates a CSV from a list of objects
 * @param context context of the activity
 * @param separator user provided separator
 */
class CSVWriter(private val context: Context, private var separator: String?) {

    private var fileWriter: FileWriter? = null

    fun createCSV() {
        //use internal storage since it's always available
        val fileRoot = context.filesDir

        //create a new file
        val file = File(fileRoot.absolutePath, "songs.csv")

        try {
            fileWriter = FileWriter(file)
            fileWriter?.let {
                //add header row
                addLine(it, listOf("Title", "Artist", "Release Year", "Rating", "Is Remix"))

                //iterate through the sample data creating new rows with actual data
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

            //inform user of successful write
            EventBus.getDefault().post(WriteEvent("File saved successfully to ${file.absolutePath}"))
        } catch (io: IOException) {
            EventBus.getDefault().post(ErrorEvent(io.localizedMessage!!))
        }
    }

    /**
     * Creates a new row with data
     * @param values data to be written to CSV
     */
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