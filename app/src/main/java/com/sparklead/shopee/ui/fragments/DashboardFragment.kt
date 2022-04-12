package com.sparklead.shopee.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sparklead.shopee.R
//import com.sparklead.shopee.Activies.databinding.FragmentDashboardBinding
import com.sparklead.shopee.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

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
}