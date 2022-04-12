package com.sparklead.shopee.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import java.util.jar.Attributes

class ShopeeTextView(context: Context , attrs :AttributeSet) : AppCompatTextView(context,attrs) {

    init{
        //call the function to apply font to the components
        applyfont()
    }

    private fun applyfont() {

        val typeface : Typeface = Typeface.createFromAsset(context.assets , "Sketch.ttf")
        setTypeface(typeface)

    }

}