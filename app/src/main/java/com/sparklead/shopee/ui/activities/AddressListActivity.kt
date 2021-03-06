package com.sparklead.shopee.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sparklead.shopee.R
import com.sparklead.shopee.firestore.FirestoreClass
import com.sparklead.shopee.models.Address
import com.sparklead.shopee.ui.adapters.AddressListAdapter
import com.sparklead.shopee.utils.SwipeToDeleteCallback
import com.sparklead.shopee.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_address_list.*
import kotlin.collections.ArrayList

class AddressListActivity : BaseActivity() {

    private var mSelectedAddress : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)

        setupActionBar()

        tv_add_address.setOnClickListener {
            val intent = Intent(this, AddEditAddressActivity::class.java)
            startActivityForResult(intent,Constants.ADD_ADDRESS_REQUEST_CODE)
        }

        if(intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)){
            mSelectedAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS,false)
        }

        if (mSelectedAddress){
            tv_title_address.text = "Select Address"
        }

        getAddressList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            hideProgressDialog()
            getAddressList()
        }
    }

    private fun getAddressList(){
        showProgressbar(resources.getString(R.string.please_wait))
        FirestoreClass().getAddressList(this)
    }

    fun successAddressListFromFirestore(addressList : ArrayList<Address> ){
        hideProgressDialog()

        if (addressList.size >0){
            rv_address_list.visibility = View.VISIBLE
            tv_no_address_found.visibility = View.GONE
            rv_address_list.layoutManager = LinearLayoutManager(this)
            rv_address_list.setHasFixedSize(true)

            val addressAdapter = AddressListAdapter(this ,addressList,mSelectedAddress)
            rv_address_list.adapter = addressAdapter

            if(!mSelectedAddress){
                val editSwipeHandler = object : SwipeToEditCallback(this){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter = rv_address_list.adapter as AddressListAdapter
                        adapter.notifyEditItem(
                            this@AddressListActivity ,
                            viewHolder.adapterPosition
                        )
                    }
                }

                val editItemTouchHelper =ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(rv_address_list)


                val deleteSwipeHandler = object :SwipeToDeleteCallback(this){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        showProgressbar(resources.getString(R.string.please_wait))

                        FirestoreClass().deleteAddress(this@AddressListActivity,addressList[viewHolder.adapterPosition].id)
                    }
                }

                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(rv_address_list)
            }





        }
        else{
            rv_address_list.visibility = View.GONE
            tv_no_address_found.visibility = View.VISIBLE
        }
    }

    fun deleteAddressSuccess(){
        hideProgressDialog()

        Toast.makeText(
            this,
            "Deleted Successfully",
            Toast.LENGTH_SHORT
        ).show()

        getAddressList()
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_address_list_activity)

        val actionBar = supportActionBar
        if (actionBar !=null ){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
        }
        toolbar_address_list_activity.setNavigationOnClickListener{ onBackPressed() }
    }


}