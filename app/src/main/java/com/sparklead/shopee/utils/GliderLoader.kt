package com.sparklead.shopee.utils

import android.content.Context
import android.net.Uri
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sparklead.shopee.R
import java.io.IOException

class GliderLoader(val context: Context)
{
    fun loadUserPicture(image:Any, imageView: ImageView)
    {
        try
        {
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.profile_img)
                .into(imageView)
        }
        catch (e:IOException)
        {
            e.printStackTrace()
        }
    }
    fun loadProductPicture(image :Any, imageView: ImageView)
    {
        try
        {
            Glide
                .with(context)
                .load(image)
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