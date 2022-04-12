package com.sparklead.shopee.ui.activities

import android.graphics.Typeface
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.sparklead.shopee.R
import kotlinx.android.synthetic.main.activity_forget_password.*

class ForgetPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        val typeface : Typeface = Typeface.createFromAsset(assets , "Sketch.ttf")
        btn_Submit.typeface = typeface

        setupActionBar()

    }


    private fun setupActionBar(){

        setSupportActionBar(toolbar_forgot_activity)

        val actionBar = supportActionBar
        if (actionBar !=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }

        toolbar_forgot_activity.setNavigationOnClickListener{onBackPressed()}


        btn_Submit.setOnClickListener {
            val email :String =et_email_forgot.text.toString().trim{ it<= ' '}

            if (email.isEmpty())
            {
                showErrorSnackBar(resources.getString(R.string.err_msg_email),true)
            }
            else
            {
                showProgressbar(resources.getString(R.string.please_wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { task->
                    hideProgressDialog()

                    if(task.isSuccessful)
                    {
                        Toast.makeText(this,"Email sent successfully to reset your password !",Toast.LENGTH_LONG).show()

                        finish()
                    }
                    else
                    {
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }
                }
            }
        }
    }
}