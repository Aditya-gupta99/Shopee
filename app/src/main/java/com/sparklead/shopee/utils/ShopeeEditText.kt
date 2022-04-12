package com.sparklead.shopee.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class ShopeeEditText(context: Context,attr : AttributeSet):AppCompatEditText(context,attr) {

    init {
        applyFont()
    }

    private fun applyFont() {
        val typeface :Typeface = Typeface.createFromAsset(context.assets,"Sketch.ttf")
        setTypeface(typeface)
    }
}