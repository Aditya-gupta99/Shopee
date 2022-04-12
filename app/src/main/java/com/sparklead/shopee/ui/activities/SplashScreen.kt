package com.sparklead.shopee.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.sparklead.shopee.R

class SplashScreen : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, DashboardActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        },2000)


//        val typeface : Typeface = Typeface.createFromAsset(assets , "Sketch.ttf")
//        tv_app_name.typeface = typeface


    }

    override fun onBackPressed() {
        doubleBackToExit()
    }
}