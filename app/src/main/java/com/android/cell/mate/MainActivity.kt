package com.android.cell.mate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.cell.mate.util.CSVReader
import timber.log.Timber
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            val reader = CSVReader(this, ",")
            val items = reader.readCSV("samples/movies.csv")

//            val string = StringBuilder()
//            items.forEach {
//                string.append(it)
//                string.append("\n")
//            }
//
//            sample.text = string
        } catch (nf: FileNotFoundException){
            Timber.e(nf)
        }
    }
}
