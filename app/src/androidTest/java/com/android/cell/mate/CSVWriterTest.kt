package com.android.cell.mate

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.android.cell.mate.util.CSVWriter
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by kombo on 2019-09-12.
 */
@RunWith(AndroidJUnit4::class)
class CSVWriterTest {

    @Test
    @Throws(Exception::class)
    fun test_write_to_csv(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val csvWriter = CSVWriter(appContext, ",")


    }
}