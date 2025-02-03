package com.alaturing.umusicapp.common.validation

import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout


fun EditText.isEmpty(label:TextInputLayout?=null):Boolean {
    val isEmpty = this.text.isEmpty()
    label?.error = if (isEmpty)  "Field cannot be empty" else null
    return isEmpty
}

fun EditText.differentContent(compare:EditText,compareLabel:TextInputLayout?=null):Boolean {
    val contentIsDifferent = this.text.toString() != compare.text.toString()
    compareLabel?.error = if (contentIsDifferent) "Does not match" else null
    return contentIsDifferent

}