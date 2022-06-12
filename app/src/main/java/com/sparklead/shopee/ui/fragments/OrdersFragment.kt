package com.sparklead.shopee.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sparklead.shopee.R
import com.sparklead.shopee.firestore.FirestoreClass
import com.sparklead.shopee.models.Order
import com.sparklead.shopee.ui.adapters.MyOrdersListAdapter
import kotlinx.android.synthetic.main.fragment_orders.*

class OrdersFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_orders, container, false)

        return root
    }

    override fun onResume() {
        super.onResume()

        getMyOrdersList()
    }

    fun populateOrdersListUiI(orderList : ArrayList<Order>){
        hideProgressDialog()

        if (orderList.size>0){

            rv_my_order_items.visibility = View.VISIBLE
            no_orders_found.visibility= View.GONE

            rv_my_order_items.layoutManager = LinearLayoutManager(activity)
            rv_my_order_items.setHasFixedSize(true)

            val myOrdersListAdapter = MyOrdersListAdapter(requireActivity(),orderList)
            rv_my_order_items.adapter = myOrdersListAdapter
        }
        else{
            rv_my_order_items.visibility = View.GONE
            no_orders_found.visibility= View.VISIBLE
        }
    }

    private fun getMyOrdersList(){

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getMyOrderList(this)
    }

}