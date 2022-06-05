package com.sparklead.shopee.ui.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.sparklead.shopee.R
import com.sparklead.shopee.firestore.FirestoreClass
import com.sparklead.shopee.models.CartItem
import com.sparklead.shopee.models.Product
import com.sparklead.shopee.utils.GliderLoader
import kotlinx.android.synthetic.main.activity_product_details.*

class ProductDetailsActivity : BaseActivity() ,View.OnClickListener{

    private var mProductId : String = " "
    private lateinit var mProductDetails :Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)){
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.e("Product Id",mProductId)

        }

        var productOwnerId :String = ""

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)){
            productOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }

        if(FirestoreClass().getCurrentUserId() == productOwnerId)
        {
            btn_add_to_cart.visibility = View.GONE
            btn_go_to_cart.visibility = View.GONE
        }
        else
        {
            btn_add_to_cart.visibility = View.VISIBLE
        }

        getProductDetails()

        btn_add_to_cart.setOnClickListener(this)
        btn_go_to_cart.setOnClickListener(this)
        setupActionBar()

        //for font
        val typeface : Typeface = Typeface.createFromAsset(assets , "Sketch.ttf")
        btn_add_to_cart.typeface = typeface
        val typeface2 : Typeface = Typeface.createFromAsset(assets , "Sketch.ttf")
        btn_go_to_cart.typeface = typeface2
    }

    private fun getProductDetails(){
        showProgressbar(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this,mProductId)
    }

    fun productExistingInCart()
    {
        hideProgressDialog()

        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }

    fun productDetailsSuccess(product: Product)
    {
        mProductDetails = product
//        hideProgressDialog()

        GliderLoader(this).loadProductPicture(product.image,iv_product_detail_image)

        tv_product_details_title.text = product.title
        tv_product_details_price.text = "₹${product.price}"
        tv_product_details_description.text = product.description
        tv_product_details_available_quantity.text = product.stock_quantity

        if(product.stock_quantity.toInt()==0){
            hideProgressDialog()

            btn_add_to_cart.visibility = View.GONE
            tv_product_details_available_quantity.text ="Out of stock"

            tv_product_details_available_quantity.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorSnackBarError
                )
            )
        }
        else{
            if(FirestoreClass().getCurrentUserId()==product.user_id)
            {
                hideProgressDialog()
            }
            else
            {
                FirestoreClass().checkIfItemExistInCart(this,mProductId)
            }
        }


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

    private fun addToCart(){
        val cartItem = CartItem(
            FirestoreClass().getCurrentUserId(),
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )

        showProgressbar(resources.getString(R.string.please_wait))
        FirestoreClass().addCartItems(this,cartItem)
    }

    fun addToCartSuccess()
    {
        hideProgressDialog()
        Toast.makeText(this,"Product was added to your cart..",Toast.LENGTH_SHORT).show()

        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE

    }

    override fun onClick(v: View?) {
        if(v!= null)
        {
            when(v.id){
                R.id.btn_add_to_cart->
                {
                    addToCart()
                }

                R.id.btn_go_to_cart->
                {
                    startActivity(Intent(this,CartListActivity::class.java))
                }
            }
        }
    }
}