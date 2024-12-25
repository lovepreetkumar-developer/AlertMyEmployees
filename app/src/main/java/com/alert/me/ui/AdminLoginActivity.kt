package com.alert.me.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alert.me.R
import com.alert.me.data.preferences.PreferenceProvider
import com.alert.me.utils.FieldsValidator
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AdminLoginActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()

    private val mPrefProvider: PreferenceProvider by instance()
    private val mFieldsValidator: FieldsValidator by instance()

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        if (mPrefProvider.isAdminLoggedIn()) {
            moveToAdminHomeScreen()
        } else {
            etUsername = findViewById(R.id.et_username)
            etPassword = findViewById(R.id.et_password)
            val btnLogin = findViewById<Button>(R.id.btn_login)
            btnLogin.setOnClickListener {
                val idCheck = "admin1"
                val passwordCheck = "123456"
                if (validateFields()) {
                    if (etUsername.text.toString() == idCheck && etPassword.text.toString() == passwordCheck
                    ) {
                        mPrefProvider.setUserLoggedIn(false)
                        mPrefProvider.setAdminLoggedIn(true)
                        moveToAdminHomeScreen()
                    } else {
                        Toast.makeText(
                            this@AdminLoginActivity,
                            "Username or Password is wrong!" + etUsername.text
                                .toString() + etPassword.text.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun moveToAdminHomeScreen() {
        val switchActivityIntent =
            Intent(this@AdminLoginActivity, AdminHomeActivity::class.java)
        switchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(switchActivityIntent)
        finish()
    }

    private fun validateFields(): Boolean {
        return mFieldsValidator.hasText(etUsername) &&
                mFieldsValidator.hasText(etPassword)
    }
}