package com.sparklead.shopee.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sparklead.shopee.R
import com.sparklead.shopee.firestore.FirestoreClass
import com.sparklead.shopee.models.Product
import com.sparklead.shopee.ui.activities.AddProductActivity
import com.sparklead.shopee.ui.activities.SettingActivity
import com.sparklead.shopee.ui.adapters.MyProductListAdapter
import kotlinx.android.synthetic.main.fragment_products.*


class ProductsFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    fun deleteProduct(productId:String){

        showAlertDialogToDeleteProduct(productId)

    }

    private fun showAlertDialogToDeleteProduct(productId: String)
    {
        val builder = AlertDialog.Builder(requireActivity())

        builder.setTitle("Delete")
        builder.setMessage("Are you ure you want to delete the product?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") { dialogInterface, _ ->

            showProgressDialog(resources.getString(R.string.please_wait))

            FirestoreClass().deleteProduct(this,productId)

            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No"){dialogInterface,_->
            dialogInterface.dismiss()
        }

        val alertDialog :AlertDialog= builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    fun productDeleteSuccess() {
        hideProgressDialog()

        Toast.makeText(requireActivity(),"Successfully deleted",Toast.LENGTH_SHORT).show()

        getProductListFromFireStore()
    }

    fun successProductListFromFireStore(productsList : ArrayList<Product>)
    {
        hideProgressDialog()

        if(productsList.size>0)
        {
            rv_my_product_items.visibility = View.VISIBLE
            tv_no_products_found.visibility = View.GONE

            rv_my_product_items.layoutManager = LinearLayoutManager(activity)
            rv_my_product_items.setHasFixedSize(true)
            val adapterProduct = MyProductListAdapter(requireActivity(),productsList,this)
            rv_my_product_items.adapter = adapterProduct
        }
        else
        {
            rv_my_product_items.visibility =View.GONE
            tv_no_products_found.visibility = View.VISIBLE
        }
    }

    private fun getProductListFromFireStore(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductList(this)
    }

    override fun onResume() {
        super.onResume()
        getProductListFromFireStore()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_products, container, false)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(id){

            R.id.action_add_products ->{
                startActivity(Intent(activity, AddProductActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}