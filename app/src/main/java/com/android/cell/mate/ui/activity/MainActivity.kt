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
import com.android.cell.mate.event.WriteEvent
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
            //purge all existing views
            tableLayout.removeAllViews()

            //show file selector
            loadCSV()
        }
        writeCSV.setOnClickListener { writeCSV() }
    }

    /**
     * Opens device storage to allow user to select file
     */
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

    /**
     * Creates a csv file from set values and displays location
     */
    private fun writeCSV() {
        CSVWriter(this, DEFAULT_SEPARATOR).createCSV()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK -> {
                data?.let { intent ->
                    //get path to file
                    csvUri = intent.data

                    //prompt user for separator
                    requestSeparator()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * Load dialog to allow user to enter separator
     */
    private fun requestSeparator() {
        supportFragmentManager.beginTransaction().add(PromptDialog(), "PromptDialog")
            .commitAllowingStateLoss()
    }

    /**
     * load the csv file from device and parse the data with provided separator
     * errors will be shown on snackbar
     * @param uri Path to the csv on device
     */
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

    /**
     * Populates the UI with values read from CSV
     * @param results contains array of values organized by columns by header name
     */
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

    /**
     * Creates the first row that names the column values
     * @param names array of header values
     */
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

    /**
     * Adds the rows containing the actual data
     * @param numOfColumns number of columns of data
     * @param content contains data to be displayed
     */
    private fun addDataRows(numOfColumns: Int, content: ArrayList<ArrayList<String>>) {
        Timber.e(content.size.toString())

        //get the size of the first item, this will be the number of rows
        val size = content[0].size

        //iterate to create as many rows as required
        for (i in 0 until size) {
            val dataRow = TableRow(this)
            dataRow.gravity = Gravity.START

            //iterate to add data to the columns
            for (j in 0 until numOfColumns) {
                val textView = TextView(this)
                textView.text = content[j][i]

                dataRow.addView(textView)
            }

            tableLayout.addView(dataRow)
        }
    }

    /**
     * Register EventBus
     */
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    /**
     * Unregister EventBus
     */
    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    /**
     * Listen for errors and display them
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorEvent(errorEvent: ErrorEvent) {
        showSnackbar(errorEvent.message)
    }

    /**
     * Listen for separator input from dialog
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInputEvent(inputEvent: InputEvent) {
        separator = inputEvent.input ?: DEFAULT_SEPARATOR

        csvUri?.let {
            getDocument(it)
        }
    }

    /**
     * Display the path of created CSV and allow user to access storage to find it
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWriteEvent(writeEvent: WriteEvent){
        val snackbar = Snackbar.make(parentLayout, writeEvent.message, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction(R.string.dismiss) {
            loadCSV()
        }
        snackbar.show()
    }

    /**
     * Show snackbar
     * @param message snackbar message be displayed
     */
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
