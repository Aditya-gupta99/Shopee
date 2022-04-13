package com.sparklead.shopee.models

import android.media.AudioProfile
import android.media.Image
import android.os.Parcelable
import android.provider.ContactsContract
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (
    val id:String = "",
    val firstName : String = "",
    val lastName : String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val gender :String = "",
    val profileCompleted :Int = 0
):Parcelable