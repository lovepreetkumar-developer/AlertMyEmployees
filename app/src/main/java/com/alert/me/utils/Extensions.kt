package com.alert.me.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.Toast
import com.alert.me.R
import com.alert.me.databinding.DialogMessageBinding
import java.util.*

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

private var messageDialog: BaseCustomDialog<DialogMessageBinding>? = null

fun Context.showMessageDialog(message: String) {
    messageDialog?.dismiss()
    messageDialog = BaseCustomDialog(
        this,
        R.layout.dialog_message,
        object : BaseCustomDialog.DialogListener {
            override fun onViewClick(view: View?) {
                messageDialog?.dismiss()
            }
        })

    Objects.requireNonNull<Window>(messageDialog?.window).setBackgroundDrawable(
        ColorDrawable(
            Color.TRANSPARENT
        )
    )
    messageDialog?.getBinding()?.tvMessage?.text = message
    messageDialog?.setCancelable(false)
    messageDialog?.show()
}