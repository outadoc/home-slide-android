package fr.outadoc.homeslide.util.view

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView

fun TextView.addTextChangedListener(onTextChanged: (String) -> Unit) {
    val tw = object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let { onTextChanged(s.toString()) }
        }
    }

    addTextChangedListener(tw)
}