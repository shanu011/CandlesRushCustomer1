package com.example.candlesrush.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.candlesrush.Constants
import com.example.candlesrush.R
import com.example.candlesrush.activities.MainActivity
import com.example.candlesrush.databinding.FragmentOrderDetailsBinding
import com.example.candlesrush.models.PlaceOrderModel
import com.example.candlesrush.models.SubCategory
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class OrderDetailsFragment : Fragment() {
    lateinit var binding:FragmentOrderDetailsBinding
    var orderId=""
    val db = Firebase.firestore
    var productimage=""
    var productPrice:Int?=0
    lateinit var mainActivity : MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity
        arguments?.let {
            orderId = it.getString(Constants.id,"") ?:""
        }
        Log.e("subcategoryId"," ${orderId}")

        db.collection(Constants.orderdetails).document(orderId)
            .addSnapshotListener{snapshots,e->
                if (e != null){
                    return@addSnapshotListener
                }
                var model = snapshots?.toObject(PlaceOrderModel::class.java)
                binding.tvproductName.setText(model?.productName)
                binding.tvproductDes.setText(model?.productDes)

                binding.tvColor.setText(model?.productColor)
                binding.tvfragrance.setText(model?.productFragrance)
                binding.tvshape.setText(model?.productShape)
                binding.tvSize.setText(model?.productSize)
                binding.tvproductPrice.setText("${model?.productPrice.toString()} INR")
                Log.e("description", "onCreate: ${model}", )
                productimage=model?.productImage.toString()
                productPrice=model?.productPrice
                Glide
                    .with(requireContext())
                    .load(model?.productImage)
                    .centerCrop()
                    .placeholder(R.drawable.candle)
                    .into(binding.imgProduct)
            }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentOrderDetailsBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return (binding.root)
    }
    override fun onResume() {
        super.onResume()
        mainActivity.binding.toolBar.title = resources.getString(R.string.order_details)
    }


}