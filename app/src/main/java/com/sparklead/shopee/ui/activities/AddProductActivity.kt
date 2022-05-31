package com.sparklead.shopee.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sparklead.shopee.R
import com.sparklead.shopee.firestore.FirestoreClass
import com.sparklead.shopee.models.Product
import com.sparklead.shopee.utils.GliderLoader
import kotlinx.android.synthetic.main.activity_add_product.*
import java.io.IOException

class AddProductActivity : BaseActivity(), View.OnClickListener{

    private var mSelectedImageFileUrl : Uri? = null
    private var mProductImageURL:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        setupActionBar()

        iv_add_update_product.setOnClickListener(this)
        btn_post.setOnClickListener(this)
    }

    private fun setupActionBar(){

        setSupportActionBar(toolbar_add_product_activity)

        val actionBar = supportActionBar
        if (actionBar !=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }

        toolbar_add_product_activity.setNavigationOnClickListener{onBackPressed()}
    }

    override fun onClick(v: View?) {
        if(v!=null)
        {
            when(v.id) {
                R.id.iv_add_update_product ->
                {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                    {
                        Constants.showImageChooser(this)
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
                    }
                }
                R.id.btn_post ->
                {
                    if(validateUserProfileDetails())
                    {
                        uploadProductImage()
                    }

                }
            }
        }
    }

    private fun uploadProductImage(){
        showProgressbar(resources.getString(R.string.please_wait))
        FirestoreClass().uploadImageToCloudStorage(this,  mSelectedImageFileUrl,Constants.PRODUCT_IMAGE)
    }

    fun productUploadSuccess(){
        hideProgressDialog()
        Toast.makeText(this,"Product Uploaded Successfully !",Toast.LENGTH_SHORT).show()
        finish()
    }

    fun imageUploadSuccess(imageUrl:String){

//        hideProgressDialog()

//        showErrorSnackBar("Product image is uploaded successful. image URL : $imageUrl ",false)

        mProductImageURL = imageUrl

        uploadProductDetails()
    }

    private fun uploadProductDetails()
    {
        val username = this.getSharedPreferences(Constants.SHOPEE_PREFERENCES,Context.MODE_PRIVATE)
            .getString(Constants.LOGGED_IN_USERNAME," ")!!

        val product = Product(
            FirestoreClass().getCurrentUserId(),
            username,
            et_product_title.text.toString().trim{ it<= ' '},
            et_product_price.text.toString().trim{ it<= ' '},
            et_product_description.text.toString().trim{ it <= ' '},
            et_product_quantity.text.toString().trim{ it <= ' '},
            mProductImageURL
            )
        FirestoreClass().uploadProductDetails(this, product)
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

        if(resultCode== Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        iv_add_update_product.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.edit_icon))

                        //the uri of selected image from phone storage.
                        mSelectedImageFileUrl = data.data!!
//                        user_image.setImageURI(selectedImageFileUri)
                        GliderLoader(this).loadUserPicture(mSelectedImageFileUrl!!, iv_product_image)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Image selection Failed !", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        else if (resultCode== Activity.RESULT_CANCELED)
        {
            Log.e("request cancelled", "image selection  cancelled")
        }
    }

    private fun validateUserProfileDetails():Boolean {
        return when{
           mSelectedImageFileUrl == null ->
           {
               showErrorSnackBar("Please select product image",true)
               false
           }

            TextUtils.isEmpty(et_product_title.text.toString().trim{ it <= ' '}) ->
            {
                showErrorSnackBar("Please enter product title",true)
                false
            }
            TextUtils.isEmpty(et_product_price.text.toString().trim{ it <= ' '}) ->
            {
                showErrorSnackBar("Please enter product price",true)
                false
            }
            TextUtils.isEmpty(et_product_description.text.toString().trim{ it <= ' '}) ->
            {
                showErrorSnackBar("Please enter product description",true)
                false
            }
            TextUtils.isEmpty(et_product_quantity.text.toString().trim{ it <= ' '}) ->
            {
                showErrorSnackBar("Please enter product Quantity",true)
                false
            }

            else -> {
                true
            }
        }
    }
}