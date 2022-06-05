package com.sparklead.shopee.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.sparklead.shopee.R
//import com.sparklead.shopee.Activies.databinding.FragmentDashboardBinding
import com.sparklead.shopee.databinding.FragmentDashboardBinding
import com.sparklead.shopee.firestore.FirestoreClass
import com.sparklead.shopee.models.Product
import com.sparklead.shopee.ui.activities.CartListActivity
import com.sparklead.shopee.ui.activities.DashboardActivity
import com.sparklead.shopee.ui.activities.SettingActivity
import com.sparklead.shopee.ui.adapters.DashboardItemsListAdapter
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()

        getDashboardItemsList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(id){

            R.id.action_settings ->{
                startActivity(Intent(activity,SettingActivity::class.java))
                return true
            }
            R.id.action_cart->
            {
                startActivity(Intent(activity,CartListActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun successDashboardItemsList(dashboardItemsList : ArrayList<Product>){
        hideProgressDialog()

        if(dashboardItemsList.size>0)
        {
            rv_dashboard_items.visibility = View.VISIBLE
            tv_no_dashboard_items_found.visibility = View.GONE

            rv_dashboard_items.layoutManager =GridLayoutManager(activity,2)
            rv_dashboard_items.setHasFixedSize(true)

            val adapter = DashboardItemsListAdapter(requireActivity(),dashboardItemsList)
            rv_dashboard_items.adapter = adapter

        }
        else
        {
            rv_dashboard_items.visibility = View.GONE
            tv_no_dashboard_items_found.visibility = View.VISIBLE
        }
    }

    private fun getDashboardItemsList(){
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getDashboardItemsList(this)
    }
}