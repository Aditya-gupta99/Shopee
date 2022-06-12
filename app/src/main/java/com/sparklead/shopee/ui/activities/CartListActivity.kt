package com.sparklead.shopee.ui.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.sparklead.shopee.R
import com.sparklead.shopee.firestore.FirestoreClass
import com.sparklead.shopee.models.CartItem
import com.sparklead.shopee.models.Product
import com.sparklead.shopee.ui.adapters.CartItemListAdapter
import kotlinx.android.synthetic.main.activity_cart_list.*

class CartListActivity : BaseActivity() {

    private lateinit var mProductList:ArrayList<Product>
    private lateinit var mCartListItem :ArrayList<CartItem>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)

        //for font
        val typeface : Typeface = Typeface.createFromAsset(assets , "Sketch.ttf")
        btn_checkout.typeface = typeface

        setUpActionBar()

        btn_checkout.setOnClickListener {
            val intent = Intent(this,AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS , true)
            startActivity(intent)
        }

    }

    fun successCartItemList(cartList: ArrayList<CartItem>){
        hideProgressDialog()

        for (product in mProductList){
            for (cartItem in cartList){
                if (product.product_id == cartItem.product_id){

                    cartItem.stock_quantity = product.stock_quantity

                    if (product.stock_quantity.toInt() == 0){
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCartListItem = cartList

        if(mCartListItem.size>0)
        {
            rv_cart_item_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            tv_no_cart_item_found.visibility = View.GONE

            rv_cart_item_list.layoutManager = LinearLayoutManager(this)
            rv_cart_item_list.setHasFixedSize(true)

            val cartListAdapter = CartItemListAdapter(this,cartList,true)
            rv_cart_item_list.adapter = cartListAdapter
            var subTotal:Double =0.0

            for(item in mCartListItem) {

                val availableQuantity = item.stock_quantity.toInt()

                if (availableQuantity >0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()

                    subTotal += (price * quantity)

                }
            }

                tv_sub_total.text = "₹$subTotal"
                tv_shipping_charge.text = "₹150.00"//todo change shipping charge

                if (subTotal>0)
                {
                    ll_checkout.visibility = View.VISIBLE

                    val total =subTotal + 150
                    tv_Total_amount.text = "₹$total"
                }
                else{
                    ll_checkout.visibility = View.GONE
                }
        }
        else
        {
            rv_cart_item_list.visibility = View.GONE
            ll_checkout.visibility = View.GONE
            tv_no_cart_item_found.visibility = View.VISIBLE
        }

    }

    fun successProductsListFromFirestore(productsList : ArrayList<Product>) {
        hideProgressDialog()

        mProductList = productsList
        getCartItemsList()
    }

    private fun getProductList(){
        showProgressbar(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductsList(this)
    }

    private fun getCartItemsList(){
//        showProgressbar(resources.getString(R.string.please_wait))
        FirestoreClass().getCartList(this)
    }

    fun itemUpdateSuccess(){
        hideProgressDialog()
        getCartItemsList()
    }

    override fun onResume() {
        super.onResume()
//        getCartItemsList()
        getProductList()
    }

    fun itemRemovedSuccess(){
        hideProgressDialog()

        Toast.makeText(this,
        "Item removed",Toast.LENGTH_SHORT).show()

        getCartItemsList()
    }



    private fun setUpActionBar(){
        setSupportActionBar(toolbar_cart_activity)

        val actionBar = supportActionBar
        if(actionBar !=null )
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }
        toolbar_cart_activity.setNavigationOnClickListener{ onBackPressed() }
    }
}