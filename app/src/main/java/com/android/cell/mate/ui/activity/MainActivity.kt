package com.android.cell.mate.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.cell.mate.R
import com.android.cell.mate.event.ErrorEvent
import com.android.cell.mate.event.InputEvent
import com.android.cell.mate.ui.dialog.PromptDialog
import com.android.cell.mate.util.CSVReader
import com.android.cell.mate.util.CSVWriter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var separator: String? = null
    private var csvUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        uploadCSV.setOnClickListener {
            tableLayout.removeAllViews()
            loadCSV()
        }
        writeCSV.setOnClickListener { writeCSV() }
    }

    private fun loadCSV() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/*"
        }

        startActivityForResult(
            intent,
            READ_REQUEST_CODE
        )
    }

    private fun writeCSV() {
        CSVWriter(this, DEFAULT_SEPARATOR).createCSV()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK -> {
                data?.let { intent ->
                    csvUri = intent.data
                    requestSeparator()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun requestSeparator() {
        supportFragmentManager.beginTransaction().add(PromptDialog(), "PromptDialog")
            .commitAllowingStateLoss()
    }

    private fun getDocument(uri: Uri) {
        try {
            val reader = CSVReader(this, separator!!)
            val items = reader.readCSV(uri)

            showRows(items)
        } catch (nf: FileNotFoundException) {
            nf.localizedMessage?.let {
                showSnackbar(it)
            }
        } catch (e: Exception) {
            e.localizedMessage?.let {
                showSnackbar(it)
            }
        } catch (io: IOException) {
            io.localizedMessage?.let {
                showSnackbar(it)
            }
        }
    }

    @Throws(Exception::class)
    private fun showRows(results: ArrayList<Map<String, ArrayList<String>>>) {
        val keys = ArrayList<String>()
        results.forEach {
            keys.add(it.keys.first())
        }

        addHeaderRow(keys)

        val data = ArrayList<ArrayList<String>>()
        results.forEachIndexed { index, map -> data.add(map[keys[index]] ?: error("")) }
        addDataRows(keys.size, data)
    }

    private fun addHeaderRow(names: ArrayList<String>) {
        val headerRow = TableRow(this)
        headerRow.gravity = Gravity.START

        names.forEach {
            val textView = TextView(this)
            textView.setTextAppearance(R.style.headerStyle)
            textView.text = it

            headerRow.addView(textView)
        }

        tableLayout.addView(headerRow)
    }

    private fun addDataRows(numOfColumns: Int, content: ArrayList<ArrayList<String>>) {
        Timber.e(content.size.toString())

        val size = content[0].size

        for (i in 0 until size) {
            val dataRow = TableRow(this)
            dataRow.gravity = Gravity.START

            for (j in 0 until numOfColumns) {
                val textView = TextView(this)
                textView.text = content[j][i]

                dataRow.addView(textView)
            }

            tableLayout.addView(dataRow)
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorEvent(errorEvent: ErrorEvent) {
        showSnackbar(errorEvent.message)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInputEvent(inputEvent: InputEvent) {
        separator = inputEvent.input ?: DEFAULT_SEPARATOR

        csvUri?.let {
            getDocument(it)
        }
    }

    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction(R.string.dismiss) {
            snackbar.dismiss()
        }
        snackbar.show()
    }

    companion object {
        const val READ_REQUEST_CODE = 214
        const val DEFAULT_SEPARATOR = ","
    }
}
