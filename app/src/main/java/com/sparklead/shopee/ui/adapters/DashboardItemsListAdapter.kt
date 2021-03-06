package com.sparklead.shopee.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sparklead.shopee.R
import com.sparklead.shopee.models.Product
import com.sparklead.shopee.ui.activities.Constants
import com.sparklead.shopee.ui.activities.ProductDetailsActivity
import com.sparklead.shopee.utils.GliderLoader
import kotlinx.android.synthetic.main.item_dashboard_list.view.*
import kotlinx.android.synthetic.main.item_list_layout.view.*

open class DashboardItemsListAdapter (private val context: Context,private val list: ArrayList<Product>
):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return MyViewHolder(
                        LayoutInflater.from(context).inflate(R.layout.item_dashboard_list,parent,false)
                )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val model = list[position]

                if(holder is MyViewHolder)
                {
                        GliderLoader(context).loadProductPicture(model.image,holder.itemView.iv_dashboard_item_image)
                        holder.itemView.tv_dashboard_item_title.text = model.title
                        holder.itemView.tv_dashboard_item_price.text = "₹ ${model.price}"

                        holder.itemView.setOnClickListener {
                                val intent = Intent(context,ProductDetailsActivity::class.java)
                                intent.putExtra(Constants.EXTRA_PRODUCT_ID,model.product_id)
                                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID,model.user_id)
                                context.startActivity(intent)
                        }
                }
        }

        override fun getItemCount(): Int {
                return list.size
        }

        class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){

        }
}