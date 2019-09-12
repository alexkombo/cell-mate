package com.android.cell.mate.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.cell.mate.R
import com.android.cell.mate.event.ErrorEvent
import com.android.cell.mate.event.InputEvent
import com.android.cell.mate.ui.dialog.PromptDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private var separator: String? = null
    private var csvUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        uploadCSV.setOnClickListener { loadCSV() }
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

    private fun requestSeparator(){
        supportFragmentManager.beginTransaction().add(PromptDialog(), "PromptDialog").commitAllowingStateLoss()
    }

    private fun getDocument(uri: Uri) {

//        try {
//            val reader = CSVReader(this, ",")
//            val items = reader.readCSV(uri)
//
//            val string = StringBuilder()
//            items.forEach {
//                string.append(it)
//                string.append("\n")
//            }
//
////            sample.text = string
//        } catch (nf: FileNotFoundException) {
//            Timber.e(nf)
//        }
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
    fun onErrorEvent(errorEvent: ErrorEvent){
        val snackbar = Snackbar.make(parentLayout, errorEvent.message, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction(R.string.dismiss) {
            snackbar.dismiss()
        }
        snackbar.show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInputEvent(inputEvent: InputEvent){
        separator = inputEvent.input ?: DEFAULT_SEPARATOR

        Timber.e(separator)
    }

    companion object {
        const val READ_REQUEST_CODE = 214
        const val DEFAULT_SEPARATOR = ","
    }
}
