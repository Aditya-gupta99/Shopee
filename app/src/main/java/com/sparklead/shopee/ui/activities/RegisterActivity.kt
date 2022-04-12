package com.sparklead.shopee.ui.activities

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sparklead.shopee.R
import com.sparklead.shopee.firestore.FirestoreClass
import com.sparklead.shopee.models.User
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val typeface : Typeface = Typeface.createFromAsset(assets , "Sketch.ttf")
        btn_register.typeface = typeface

        setupActionBar()

        tv_register_login.setOnClickListener {
            onBackPressed()
//            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left)
        }

        btn_register.setOnClickListener {
            registerUser()
        }
    }

    private fun setupActionBar(){

        setSupportActionBar(toolbar_register_activity)

        val actionBar = supportActionBar
        if (actionBar !=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }

        toolbar_register_activity.setNavigationOnClickListener{onBackPressed()}
    }


    private fun validateRegisterDetails():Boolean {
        return when {
            TextUtils.isEmpty(et_first_name.text.toString().trim{it<= ' '}) ->
            {
                showErrorSnackBar(resources.getString(R.string.err_msg_first_name),true)
                false
            }
            TextUtils.isEmpty(et_last_name.text.toString().trim{it<= ' '}) ->
            {
                showErrorSnackBar(resources.getString(R.string.err_msg_last_name),true)
                false
            }
            TextUtils.isEmpty(et_email_reg.text.toString().trim{it<= ' '}) ->
            {
                showErrorSnackBar(resources.getString(R.string.err_msg_email),true)
                false
            }
            TextUtils.isEmpty(et_password_reg.text.toString().trim{it<= ' '}) ->
            {
                showErrorSnackBar(resources.getString(R.string.err_msg_password),true)
                false
            }
            TextUtils.isEmpty(et_confirm_password.text.toString().trim{it<= ' '}) ->
            {
                showErrorSnackBar(resources.getString(R.string.err_msg_confirm_password),true)
                false
            }
            et_password_reg.text.toString().trim{it <= ' '} != et_confirm_password.text.toString().trim{ it <= ' '} ->
            {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_confirm),true)
                false
            }
            !cb_terms_and_condition.isChecked ->
            {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_confirm),true)
                false
            }
            else ->
            {
//                showErrorSnackBar("Thanks for registering!",false)
                true
            }
        }
    }


    private fun registerUser()
    {

        if(validateRegisterDetails())
        {

            showProgressbar(resources.getString(R.string.please_wait))

            val email :String = et_email_reg.text.toString().trim{ it <= ' ' }
            val password : String = et_password_reg.text.toString().trim{ it <= ' '}


            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(
                OnCompleteListener<AuthResult>{ task ->

                    hideProgressDialog()

                    if(task.isSuccessful){

                        val firebaseUser : FirebaseUser = task.result!!.user!!

                        val user =User(
                            firebaseUser.uid,
                            et_first_name.text.toString().trim{ it<=' ' },
                            et_last_name.text.toString().trim{ it <=' ' },
                            et_email_reg.text.toString().trim{ it <= ' '},


                        )

                        FirestoreClass().registerUser(this@RegisterActivity , user)

//                        FirebaseAuth.getInstance().signOut()
//                        finish()
                    }
                    else
                    {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
            )
        }
    }

    fun userRegistrationSuccess(){
        hideProgressDialog()
        showErrorSnackBar("You are registered successfully.",false)


        Handler(Looper.getMainLooper()).postDelayed({
            FirebaseAuth.getInstance().signOut()
            finish()
        },1000)

    }

    override fun onBackPressed() {
        doubleBackToExit()
    }
}