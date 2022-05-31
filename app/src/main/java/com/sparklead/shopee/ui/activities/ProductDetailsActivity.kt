package com.sparklead.shopee.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.sparklead.shopee.R
import com.sparklead.shopee.firestore.FirestoreClass
import com.sparklead.shopee.models.Product
import com.sparklead.shopee.utils.GliderLoader
import kotlinx.android.synthetic.main.activity_product_details.*

class ProductDetailsActivity : BaseActivity() {

    private var mProductId : String = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)){
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.e("Product Id",mProductId)

        }

        getProductDetails()
    }

    private fun getProductDetails(){
        showProgressbar(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this,mProductId)
    }

    fun productDetailsSuccess(product: Product)
    {
        hideProgressDialog()

        GliderLoader(this).loadProductPicture(product.image,iv_product_detail_image)

        tv_product_details_title.text = product.title
        tv_product_details_price.text = "₹${product.price}"
        tv_product_details_description.text = product.description
        tv_product_details_quantity.text = product.stock_quantity

    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_product_activity)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }
        toolbar_product_activity.setNavigationOnClickListener { onBackPressed() }
    }
}