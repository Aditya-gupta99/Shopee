package com.sparklead.shopee.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.sparklead.shopee.R
import com.sparklead.shopee.firestore.FirestoreClass
import com.sparklead.shopee.models.Address
import com.sparklead.shopee.models.CartItem
import com.sparklead.shopee.models.Order
import com.sparklead.shopee.models.Product
import com.sparklead.shopee.ui.adapters.CartItemListAdapter
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.activity_login.*

class CheckoutActivity : BaseActivity() {

    private var mAddressDetails: Address?= null
    private lateinit var mProductList :ArrayList<Product>
    private lateinit var mCartItemsList:ArrayList<CartItem>
    private var mSubTotal :Double = 0.0
    private var mTotalAmount : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        setupActionBar()

        if(intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)){
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_SELECTED_ADDRESS)
        }

        if(mAddressDetails !=null){
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails?.address},${mAddressDetails?.zipcode}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote
            tv_checkout_mobile_number.text = mAddressDetails?.mobileNumber

        }

        getProductList()

        btn_place_order.setOnClickListener {
            placeAnOrder()
        }
    }


    fun successProductsListFromFirestore(productList : ArrayList<Product>){
        hideProgressDialog()

        mProductList = productList
        getCartItemList()
    }

    private fun getCartItemList(){
        FirestoreClass().getCartList(this)
    }

    private fun getProductList(){
        showProgressbar(resources.getString(R.string.please_wait))

        FirestoreClass().getAllProductsList(this)
    }

    private fun placeAnOrder(){
        showProgressbar(resources.getString(R.string.please_wait))

        val order = Order(
            FirestoreClass().getCurrentUserId(),
            mCartItemsList,
            mAddressDetails!!,
            "Order ${System.currentTimeMillis()}",
            mCartItemsList[0].image,
            mSubTotal.toString(),
            "150.00" ,
            mTotalAmount.toString(),
            System.currentTimeMillis()
        )

        FirestoreClass().placeOrder(this,order)
    }

    fun allDetailsUpdatedSuccessfully(){
        hideProgressDialog()
        Toast.makeText(this,"Your order was Placed Successfully.",Toast.LENGTH_SHORT).show()

        val intent = Intent(this,DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun orderPlacedSuccess(){
        FirestoreClass().updateAllDetails(this,mCartItemsList)
    }

    fun successCartItemsList(cartList:ArrayList<CartItem>){
        hideProgressDialog()

        for(product in mProductList){
            for(cartItem in cartList){
                if(product.product_id == cartItem.product_id){
                    cartItem.stock_quantity = product.stock_quantity
                }
            }
        }
        mCartItemsList = cartList

        rv_cart_list_items.layoutManager = LinearLayoutManager(this)
        rv_cart_list_items.setHasFixedSize(true)

        val cartListAdapter = CartItemListAdapter(this,mCartItemsList,false)
        rv_cart_list_items.adapter = cartListAdapter

        for(item in mCartItemsList){
            val availability = item.stock_quantity.toInt()
            if(availability >0){
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubTotal += (price * quantity)
            }
        }

        tv_checkout_sub_total.text = "₹${mSubTotal}"
        tv_checkout_shipping_charge.text = "₹150.00"

        if(mSubTotal>0){
            ll_checkout_place_order.visibility = View.VISIBLE

            mTotalAmount = mSubTotal +150.00
            tv_checkout_total_amount.text = "₹${mTotalAmount}"
        }
        else{
            ll_checkout_place_order.visibility = View.GONE
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_checkout_activity)
        val actionBar = supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }
        toolbar_checkout_activity.setNavigationOnClickListener{ onBackPressed()}
    }
}