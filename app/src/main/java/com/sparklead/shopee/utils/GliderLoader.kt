package com.sparklead.shopee.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sparklead.shopee.R
import java.io.IOException

class GliderLoader (val context :Context)
{
    fun loadUserPicture(imageURI:Uri,imageView: ImageView)
    {
        try
        {
            Glide
                .with(context)
                .load(Uri.parse(imageURI.toString()))
                .centerCrop()
                .placeholder(R.drawable.profile_img)
                .into(imageView)
        }
        catch (e:IOException)
        {
            e.printStackTrace()
        }
    }
}