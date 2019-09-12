package com.android.cell.mate.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.cell.mate.R
import com.android.cell.mate.util.CSVReader
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        uploadCSV.setOnClickListener { loadCSV() }

        try {
            val reader = CSVReader(this, ",")
            val items = reader.readCSV("samples/movies.csv")

            val string = StringBuilder()
            items.forEach {
                string.append(it)
                string.append("\n")
            }

//            sample.text = string
        } catch (nf: FileNotFoundException) {
            Timber.e(nf)
        }
    }

    private fun loadCSV() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/*"
        }

        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK -> {
                data?.let { intent ->
                    getDocument(intent.data!!)
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getDocument(uri: Uri){
        Timber.e("Here")
    }

    companion object {
        const val READ_REQUEST_CODE = 214
    }
}
