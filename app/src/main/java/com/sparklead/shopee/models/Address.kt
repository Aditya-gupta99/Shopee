package com.sparklead.shopee.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address (
    val user_id :String =" ",
    val name:String = " ",
    val mobileNumber:String =" ",
    val address:String =" ",
    val zipcode: String =" ",
    val additionalNote:String = " ",
    val type :String =" ",
    var id :String =" ",
):Parcelable