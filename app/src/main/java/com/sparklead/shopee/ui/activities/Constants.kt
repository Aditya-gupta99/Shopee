package com.sparklead.shopee.ui.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val USERS : String = "users"
    const val PRODUCTS :String = "products"

    const val SHOPEE_PREFERENCES : String = "ShopeePrefs"
    const val LOGGED_IN_USERNAME : String = "logged_in_user"
    const val EXTRA_USER_DETAILS : String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1
    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"
    const val MALE : String = "Male"
    const val FEMALE : String = "Female"
    const val MOBILE : String = "mobile"
    const val GENDER : String = "gender"
    const val IMAGE :String ="image"
    const val PROFILE_COMPLETED :String ="profileCompleted"

    const val USER_PROFILE_IMAGE :String = "user_profile_image"

    const val USER_ID:String = "user_id"

    const val EXTRA_PRODUCT_ID = "extra_product_id"


    const val PRODUCT_IMAGE :String = "product_image"


    fun showImageChooser(activity :Activity)
    {
        val galleryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity,uri: Uri?):String?
    {

        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))

    }

}