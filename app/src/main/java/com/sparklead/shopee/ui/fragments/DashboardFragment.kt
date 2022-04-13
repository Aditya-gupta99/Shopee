package com.sparklead.shopee.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sparklead.shopee.R
//import com.sparklead.shopee.Activies.databinding.FragmentDashboardBinding
import com.sparklead.shopee.databinding.FragmentDashboardBinding
import com.sparklead.shopee.ui.activities.SettingActivity

class DashboardFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val textView : TextView = root.findViewById(R.id.text_dashboard)

        textView.text = "This is the dashboard Fragment"

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
        }
        return super.onOptionsItemSelected(item)
    }
}