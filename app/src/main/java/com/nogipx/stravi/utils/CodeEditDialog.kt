package com.nogipx.stravi.utils

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nogipx.stravi.R
import com.susmit.aceeditor.AceEditor
import kotlinx.android.synthetic.main.dialog_codeview.view.*

class CodeEditDialog(
    private val lang: String,
    private var code: String,
//    private val fullscreen: Boolean = false,
    private val callback: (AceEditor, Action) -> Any = { _, _ -> }
) : DialogFragment() {

    enum class Action {
        POSITIVE, NEGATIVE
    }

    lateinit var model: CodeEditViewModel
    private lateinit var mCodeEditor: AceEditor

    class CodeEditViewModel: ViewModel() {
        val code: MutableLiveData<String> = MutableLiveData()
        val lang: MutableLiveData<String> = MutableLiveData()
    }

    fun updateText() = mCodeEditor.requestText()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = activity!!.layoutInflater.inflate(R.layout.dialog_codeview, null)
        mCodeEditor = view.codeEditor

        mCodeEditor.setResultReceivedListener { FLAG_VALUE, results ->
            when (FLAG_VALUE) {
                AceEditor.Request.TEXT_REQUEST -> {
                    model.code.value = results.joinToString(" ")
                }
            }
        }

        return AlertDialog.Builder(activity)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                callback(view.codeEditor, Action.POSITIVE)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                callback(view.codeEditor, Action.NEGATIVE)
            }
            .create()
    }
}