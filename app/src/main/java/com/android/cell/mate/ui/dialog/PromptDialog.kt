package com.android.cell.mate.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.cell.mate.R
import com.android.cell.mate.event.ErrorEvent
import com.android.cell.mate.event.InputEvent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.prompt_layout.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by kombo on 2019-09-12.
 */
class PromptDialog: BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.prompt_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        done.setOnClickListener {
            validate(separatorEt.text.toString())
        }
    }

    private fun validate(input: String?){
        if(input.isNullOrEmpty())
            EventBus.getDefault().post(ErrorEvent(getString(R.string.please_enter_separator)))
        else
            EventBus.getDefault().post(InputEvent(input))

        dismissAllowingStateLoss()
    }
}