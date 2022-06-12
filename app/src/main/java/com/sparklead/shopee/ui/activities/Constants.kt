package com.sparklead.shopee.ui.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.sparklead.shopee.models.CartItem
import com.sparklead.shopee.models.Product

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

    const val EXTRA_PRODUCT_ID:String = "extra_product_id"

    const val EXTRA_PRODUCT_OWNER_ID :String= "extra_product_owner_id"

    const val DEFAULT_CART_QUANTITY :String= "1"

    const val CART_ITEMS:String = "cart_items"

    const val PRODUCT_ID = "product_id"

    const val PRODUCT_IMAGE :String = "product_image"

    const val CART_QUANTITY : String = "cart_quantity"

    const val HOME : String = "home"
    const val OFFICE :String = "office"
    const val OTHER : String = "other"

    const val ADDRESSES : String = "addresses"

    const val EXTRA_ADDRESS_DETAILS : String ="AddressDetails"

    const val EXTRA_SELECT_ADDRESS : String ="extra_selected_address"

    const val ADD_ADDRESS_REQUEST_CODE : Int = 121

    const val EXTRA_SELECTED_ADDRESS:String = "extra_selected_address"

    const val ORDERS : String = "orders"

    const val STOCK_QUANTITY :String = "stock_quantity"

    const val EXTRA_ORDER_DETAILS :String = "extra_order_details"

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