package com.habeshastudio.mahder.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.Switch
import com.habeshastudio.mahder.R

class StartActivity : AppCompatActivity() {

    private val mToolbar: Toolbar? = null

    private var mSwitch: Switch? = null
    private var mRegBtn: Button? = null
    private var mSignInBtn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        mSwitch = findViewById<View>(R.id.phone_switch) as Switch
        mRegBtn = findViewById<View>(R.id.startRegButton) as Button
        mSignInBtn = findViewById<View>(R.id.startLoginButton) as Button
        mRegBtn!!.setOnClickListener {
            val reg_intent = Intent(this@StartActivity, RegisterActivity::class.java)
            startActivity(reg_intent)
        }
        mSignInBtn!!.setOnClickListener {
            if (mSwitch!!.isChecked) {
                val phoneIntent = Intent(this@StartActivity, PhoneAuth::class.java)
                startActivity(phoneIntent)
            } else {
                val logIN_intent = Intent(this@StartActivity, LoginActivity::class.java)
                startActivity(logIN_intent)
            }
        }
    }
}
