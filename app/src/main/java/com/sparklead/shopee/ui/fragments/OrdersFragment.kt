package com.sparklead.shopee.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.sparklead.shopee.R

class OrdersFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_orders, container, false)

        val textView: TextView = root.findViewById(R.id.text_notifications)
        textView.text = "This is the Notification Fragment"

        return root
    }

}