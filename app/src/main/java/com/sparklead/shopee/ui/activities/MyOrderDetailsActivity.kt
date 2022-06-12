package com.sparklead.shopee.ui.activities

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sparklead.shopee.R
import com.sparklead.shopee.models.Order
import com.sparklead.shopee.ui.adapters.CartItemListAdapter
import kotlinx.android.synthetic.main.activity_my_order_details.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.SimpleFormatter

class MyOrderDetailsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_order_details)

        setupActionBar()

        var myOrderDetails : Order = Order()
        if(intent.hasExtra(Constants.EXTRA_ORDER_DETAILS)){
            myOrderDetails = intent.getParcelableExtra(Constants.EXTRA_ORDER_DETAILS)!!
        }

        setupUI(myOrderDetails)
    }

    private fun setupUI(orderDetails: Order){
        tv_order_details_id.text = orderDetails.title

        val dateFormat = "dd MMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calender : Calendar = Calendar.getInstance()
        calender.timeInMillis = orderDetails.order_datetime
        val orderDateTime = formatter.format(calender.time)
        tv_order_details_date.text = orderDateTime

        val diffInMillsSeconds :Long = System.currentTimeMillis() - orderDetails.order_datetime
        val diffInHours : Long = TimeUnit.MILLISECONDS.toHours(diffInMillsSeconds)
        Log.e("Difference in Hours","$diffInHours")

        when{
            diffInHours < 24 ->{
                tv_order_status.text = "Pending"
                tv_order_status.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorSnackBarError
                    )
                )
            }
            diffInHours < 48 ->{
                tv_order_status.text = "In Process"
                tv_order_status.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorSnackBarError
                    )
                )
            }
            else ->{
                tv_order_status.text = "Delivered"
                tv_order_status.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorSnackBarSuccess
                    )
                )
            }
        }

        rv_my_order_items_list.layoutManager = LinearLayoutManager(this)
        rv_my_order_items_list.setHasFixedSize(true)

        val cartListAdapter = CartItemListAdapter(this ,orderDetails.items,false)
        rv_my_order_items_list.adapter = cartListAdapter

        tv_order_address_type.text = orderDetails.address.type
        tv_order_full_name.text = orderDetails.address.name
        tv_order_address.text = "${orderDetails.address.address}, ${orderDetails.address.zipcode}"
        tv_order_additional_note.text = orderDetails.address.additionalNote
        tv_order_mobile_number.text = orderDetails.address.mobileNumber

        tv_order_details_sub_total.text = "₹${orderDetails.sub_total_amount}"
        tv_order_details_shipping_charge.text = "₹${orderDetails.shipping_charge}"
        tv_order_details_total_amount.text = "₹${orderDetails.sub_total_amount}"

    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_my_order_details_activity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)

        }
        toolbar_my_order_details_activity.setNavigationOnClickListener { onBackPressed() }

    }
}