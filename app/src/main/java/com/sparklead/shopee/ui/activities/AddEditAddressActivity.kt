package com.sparklead.shopee.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.sparklead.shopee.R
import com.sparklead.shopee.firestore.FirestoreClass
import com.sparklead.shopee.models.Address
import kotlinx.android.synthetic.main.activity_add_edit_address.*
import kotlinx.android.synthetic.main.activity_address_list.*

class AddEditAddressActivity : BaseActivity() {

    private var mAddressDetails:Address?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_address)

        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)){
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)
        }

        if (mAddressDetails!= null ){
            if (mAddressDetails!!.id.isNotEmpty()){

                tv_title.text = "Edit Address"
                btn_submit_address.text ="Update"

                et_full_name.setText(mAddressDetails?.name)
                et_phone_no.setText(mAddressDetails?.mobileNumber)
                et_address.setText(mAddressDetails?.address)
                et_zip_code.setText(mAddressDetails?.zipcode)
                et_additional_note.setText(mAddressDetails?.additionalNote)

                when (mAddressDetails?.type) {
                    Constants.HOME -> {
                        rb_home.isChecked = true
                    }
                    Constants.OFFICE -> {
                        rb_office.isChecked = true
                    }
                    else -> {
                        rb_other.isChecked = true
                    }
                }
            }
        }

        setupActionBar()
        btn_submit_address.setOnClickListener {
            saveAddressToFirestore()
        }

    }
    private fun setupActionBar(){
        setSupportActionBar(toolbar_add_edit_address_activity)

        val actionBar = supportActionBar
        if (actionBar !=null ){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }
        toolbar_add_edit_address_activity.setNavigationOnClickListener{ onBackPressed() }
    }

    private fun  saveAddressToFirestore(){
        val fullName:String =et_full_name.text.toString().trim{ it <= ' '}
        val phoneNumber:String = et_phone_no.text.toString().trim{ it <= ' '}
        val address:String = et_address.text.toString().trim{ it <= ' '}
        val zipcode:String = et_zip_code.text.toString().trim{ it <= ' '}
        val additionalNote = et_additional_note.text.toString().trim{ it <= ' '}

        if (validateData()){
            showProgressbar(resources.getString(R.string.please_wait))

            val addressType :String = when{
                rb_home.isChecked ->{
                    Constants.HOME
                }
                rb_office.isChecked->{
                    Constants.OFFICE
                }
                else->{
                    Constants.OTHER
                }
            }

            val addressModel = Address(
                FirestoreClass().getCurrentUserId(),
                fullName,
                phoneNumber,
                address,
                zipcode,
                additionalNote,
                addressType
            )

            if (mAddressDetails!=null && mAddressDetails!!.id.isNotEmpty()){
                FirestoreClass().updateAddress(this,addressModel,mAddressDetails!!.id)
            }else{
                FirestoreClass().addAddress(this ,addressModel)
            }

        }
    }

    fun addUpdateAddressSuccess(){
        hideProgressDialog()

        val notifySuccessMessage : String = if (mAddressDetails!=null && mAddressDetails!!.id.isNotEmpty()){
            "Your Address is updated successfully"
        }else{
            "Your Address is added successfully"
        }

        Toast.makeText(
            this,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()
        setResult(RESULT_OK)

        finish()
    }

    private fun validateData():Boolean {
        return when {
            TextUtils.isEmpty(et_full_name.text.toString().trim{ it <=' '})->{
                showErrorSnackBar("Please enter full name ",true)
                false
            }

            TextUtils.isEmpty(et_phone_no.text.toString().trim{ it<=' '})->{
                showErrorSnackBar("Please enter phone number",true)
                false
            }

            TextUtils.isEmpty(et_address.text.toString().trim { it <=' '})->{
                showErrorSnackBar("Please enter address ",true )
                false
            }
            TextUtils.isEmpty(et_zip_code.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar("Please enter zip code",true)
                false
            }

            rb_other.isChecked && TextUtils.isEmpty(et_zip_code.text.toString().trim{it <=' '})->{
                showErrorSnackBar("Please enter zip code",true)
                false
            }
            else->{
                true
            }
        }

    }
}