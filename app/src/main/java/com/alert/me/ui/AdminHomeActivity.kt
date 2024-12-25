package com.alert.me.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.alert.me.R
import com.alert.me.data.preferences.PreferenceProvider
import com.alert.me.databinding.DialogAlertBinding
import com.alert.me.firebase.FcmNotificationsSender
import com.alert.me.utils.BaseCustomDialog
import com.alert.me.utils.CustomProgress
import com.google.firebase.messaging.FirebaseMessaging
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AdminHomeActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()

    private val mPrefProvider: PreferenceProvider by instance()
    private val mCustomProgress: CustomProgress by instance()

    private lateinit var imgLogout: AppCompatImageView

    private var mLogoutWarningDialog: BaseCustomDialog<DialogAlertBinding>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        FirebaseMessaging.getInstance().subscribeToTopic("all")

        val title = findViewById<EditText>(R.id.et_title)
        val message = findViewById<EditText>(R.id.et_message)

        findViewById<View>(R.id.btn_send_notification).setOnClickListener { view: View? ->
            if (title.text.toString().isNotEmpty() && message.text.toString().isNotEmpty()) {
                mCustomProgress.show(this)
                val notificationsSender = FcmNotificationsSender(
                    "/topics/all",
                    title.text.toString(),
                    message.text.toString(),
                    this@AdminHomeActivity
                )
                notificationsSender.sendNotification(mCustomProgress)
                title.setText("")
                message.setText("")
            } else {
                Toast.makeText(this@AdminHomeActivity, "Please Enter Details", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        imgLogout = findViewById(R.id.img_logout)
        imgLogout.setOnClickListener { showAlertDialog() }
    }

    private fun showAlertDialog() {
        mLogoutWarningDialog = BaseCustomDialog(
            this,
            R.layout.dialog_alert,
            object : BaseCustomDialog.DialogListener {
                override fun onViewClick(view: View?) {
                    view?.let {
                        when (it.id) {
                            R.id.btn_cancel -> mLogoutWarningDialog?.dismiss()
                            R.id.btn_yes -> {
                                mLogoutWarningDialog?.dismiss()
                                mPrefProvider.setUserLoggedIn(false)
                                mPrefProvider.setAdminLoggedIn(false)
                                moveToUserLoginScreen()
                            }
                            else -> mLogoutWarningDialog?.dismiss()
                        }
                    }
                }
            })

        mLogoutWarningDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mLogoutWarningDialog?.getBinding()?.tvMessage?.text =
            getString(R.string.are_you_sure_to_logout)
        mLogoutWarningDialog?.setCancelable(true)
        mLogoutWarningDialog?.show()
    }

    private fun moveToUserLoginScreen() {
        startActivity(
            Intent(
                this,
                UserLoginActivity::class.java
            ).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
    }
}