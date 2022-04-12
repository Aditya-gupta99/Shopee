package com.sparklead.shopee.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class ShopeeRadioButton(context: Context, attributeSet : AttributeSet):AppCompatRadioButton(context,attributeSet) {

    init {
        applyfont()
    }

    private fun applyfont() {

        val typeface : Typeface = Typeface.createFromAsset(context.assets , "Sketch.ttf")
        setTypeface(typeface)

    }
}