package com.sparklead.shopee.ui.activities

import android.content.Context
import android.os.Bundle
import com.sparklead.shopee.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val sharedPreferences = getSharedPreferences(Constants.SHOPEE_PREFERENCES,Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME,"")!!
        tv_main.text = "Hello $username"
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }
}