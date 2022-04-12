package com.sparklead.shopee.ui.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.sparklead.shopee.R
import com.sparklead.shopee.firestore.FirestoreClass
import com.sparklead.shopee.models.User
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() , View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val typeface : Typeface = Typeface.createFromAsset(assets , "Sketch.ttf")
        btn_Submit.typeface = typeface


        tv_register.setOnClickListener(this)
        btn_Submit.setOnClickListener(this)
        tv_forgot.setOnClickListener(this)
    }


    override fun onClick(view: View?)
    {
        if(view!=null)
        {
            when(view.id)
            {
                R.id.tv_register ->
                {
                    startActivity(Intent(this, RegisterActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                R.id.tv_forgot ->
                {
                    startActivity(Intent(this, ForgetPasswordActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                R.id.btn_Submit ->
                {
                    logInRegisteredUser()
                }
            }
        }
    }

    private fun validateLoginDetails():Boolean {

        return when {
            TextUtils.isEmpty(et_email.text.toString().trim{it<= ' '}) ->
            {
                showErrorSnackBar(resources.getString(R.string.err_msg_email),true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim{it<= ' '}) ->
            {
                showErrorSnackBar(resources.getString(R.string.err_msg_password),true)
                false
            }
            else ->
            {
//                showErrorSnackBar("Your details are valid.",false)
                true
            }
        }
    }

    private fun logInRegisteredUser() {

        if(validateLoginDetails()){

            showProgressbar(resources.getString(R.string.please_wait))

            val email =et_email.text.toString().trim{ it <= ' '}
            val password = et_password.text.toString().trim{ it <= ' '}

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener{ task ->


                if(task.isSuccessful)
                {
                    //Todo - send user to Main Activity
                        FirestoreClass().getUserDetails(this)
                        showErrorSnackBar("You are logged in successfully.",false)
//                    startActivity(Intent(this,MainActivity::class.java))
//                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                }
                else
                {
                    hideProgressDialog()
                    showErrorSnackBar(task.exception!!.message.toString(),true)
                }
            }
        }
    }

    fun userLoggedInSuccess(user: User){

        hideProgressDialog()

        Log.i("First Name : ",user.firstName)
        Log.i("Last Name : ",user.lastName)
        Log.i("Email : ",user.email)

        if(user.profileCompleted==0) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, Profile::class.java)
                intent.putExtra(Constants.EXTRA_USER_DETAILS,user)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
            }, 200)
        }
        else
        {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent =Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
            }, 200)
        }


    }
    override fun onBackPressed() {
        doubleBackToExit()
    }
}