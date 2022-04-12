package com.sparklead.shopee.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sparklead.shopee.R
import com.sparklead.shopee.firestore.FirestoreClass
import com.sparklead.shopee.models.User
import com.sparklead.shopee.utils.GliderLoader
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.IOException

class Profile : BaseActivity() , View.OnClickListener {

    private lateinit var mUserDetails : User
    private var mSelectedImageFileUrl : Uri? =null
    private var mUserProfileImageURl :String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS))
        {
            //get the user details from intent as parcelableExtra
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        et_first_name_profile.isEnabled = false
        et_first_name_profile.setText(mUserDetails.firstName)

        et_last_name_profile.isEnabled =false
        et_last_name_profile.setText(mUserDetails.lastName)

        et_email_profile.isEnabled = false
        et_email_profile.setText(mUserDetails.email)

        user_image.setOnClickListener(this)
        btn_profile.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(v!=null)
            when(v.id){

                R.id.user_image -> {

                    // Here we will check if the permission is already allowed or we need to request for it.
                    // First of all we will check the READ_EXTERNAL_STORAGE permission and if it is not allowed we will request for the same.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this)
//                        showErrorSnackBar("You already have the storage permission.", false)
                    } else {

                        /*Requests permissions to be granted to this application. These permissions
                         must be requested in your manifest, they should not be granted to your app,
                         and they should have protection level*/

                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.btn_profile ->
                {
                    if(validateUserProfileDetails())
                    {

                        showProgressbar(resources.getString(R.string.please_wait))

                        if(mSelectedImageFileUrl!=null)
                        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUrl)
                        else
                        {
                            updateUserProfileDetails()
                        }


                    }
                }
            }

    }

    private fun updateUserProfileDetails()
    {
        val userHashMap =HashMap<String,Any>()

        val mobileNo = et_mobile_no.text.toString().trim{ it <= ' '}

        val gender = if(rb_male.isChecked)
        {
            Constants.MALE
        }
        else
        {
            Constants.FEMALE
        }

        if(mUserProfileImageURl.isNotEmpty()){
            userHashMap[Constants.IMAGE] = mUserProfileImageURl
        }

        if(mobileNo.isNotEmpty())
        {
            userHashMap[Constants.MOBILE] = mobileNo.toLong()
        }

        userHashMap[Constants.GENDER] = gender

        userHashMap[Constants.PROFILE_COMPLETED] = 1

//        showProgressbar(resources.getString(R.string.please_wait))

        FirestoreClass().updateUserProfileData(this,userHashMap)

//      showErrorSnackBar("your details are valid .",false)

    }

    fun userProfileUpdateSuccess(){
        hideProgressDialog()

        showErrorSnackBar("profile updated successfully",false)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent =Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }, 400)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
//                showErrorSnackBar("The storage permission is granted.", false)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,"Oops , you just denied the permission for storage .you can also allow in app permission",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode==Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        //the uri of selected image from phone storage.
                        mSelectedImageFileUrl = data.data!!
//                        user_image.setImageURI(selectedImageFileUri)
                        GliderLoader(this).loadUserPicture(mSelectedImageFileUrl!!, user_image)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Image selection Failed !", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        else if (resultCode==Activity.RESULT_CANCELED)
        {
            Log.e("request cancelled", "image selection  cancelled")
        }
    }

    private fun validateUserProfileDetails():Boolean {
        return when{
            TextUtils.isEmpty(et_mobile_no.text.toString().trim{it <=' '})->
            {
                showErrorSnackBar("Enter your mobile number",true)
                false
            }
            else->
            {
                true
            }
        }
    }

    fun imageUploadSuccess(imageUrl:String){

        hideProgressDialog()

        mUserProfileImageURl = imageUrl
        updateUserProfileDetails()
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }
}