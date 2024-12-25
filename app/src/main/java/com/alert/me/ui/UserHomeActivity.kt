package com.alert.me.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.alert.me.R
import com.alert.me.data.preferences.PreferenceProvider
import com.alert.me.databinding.DialogAlertBinding
import com.alert.me.models.Member
import com.alert.me.utils.BaseCustomDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class UserHomeActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()

    private val mPrefProvider: PreferenceProvider by instance()

    private lateinit var imgLogout: AppCompatImageView

    private var mLogoutWarningDialog: BaseCustomDialog<DialogAlertBinding>? = null

    private lateinit var btnLoginAsAdmin: TextView
    private lateinit var tvMessage: TextView
    private lateinit var tvNoMessage: TextView

    var firebaseDatabase: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null
    var member: Member
    var str1: String? = null
    var str2: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)
        tvMessage = findViewById(R.id.tv_message)
        tvNoMessage = findViewById(R.id.tv_no_message)
        setNotificationData(intent.extras)
        btnLoginAsAdmin = findViewById(R.id.btn_login_as_admin)
        btnLoginAsAdmin.setOnClickListener {
            val s = Intent(this@UserHomeActivity, AdminLoginActivity::class.java)
            startActivity(s)
        }

        imgLogout = findViewById(R.id.img_logout)
        imgLogout.setOnClickListener { showAlertDialog() }
    }

    init {
        member = Member()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver,
            IntentFilter("MyData")
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            setNotificationData(intent.extras)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setNotificationData(bundle: Bundle?) {
        if (bundle != null) {
            tvNoMessage.visibility = View.GONE
            tvMessage.visibility = View.VISIBLE
            tvMessage.text = bundle.getString("title") + "\n" + bundle.getString("message")
        } else {
            tvNoMessage.visibility = View.VISIBLE
            tvMessage.visibility = View.INVISIBLE
        }
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