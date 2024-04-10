package com.example.candlesrush.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.candlesrush.R
import com.example.candlesrush.databinding.LayoutMyOrdersBinding
import com.example.candlesrush.interfaces.ClickInterface
import com.example.candlesrush.interfaces.ClickType
import com.example.candlesrush.models.PlaceOrderModel

class OrdersAdapter(var context: Context,var arrayList: ArrayList<PlaceOrderModel>, var clickListener: ClickInterface)  : RecyclerView.Adapter<OrdersAdapter.ViewHolder>(){
    class ViewHolder(var binding:LayoutMyOrdersBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindData(placeOrderModel: PlaceOrderModel, position: Int, clickListener: ClickInterface){
            binding.placeorders = placeOrderModel
            binding.position = position
            binding.clickListener = clickListener

            binding.imgproduct.setOnClickListener {
                clickListener.onClick(position,clickType = ClickType.OnViewClick)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutMyOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            bindData(arrayList[position], position, clickListener)
            binding.tvtotalAmount.setText("${arrayList[position].productPrice} INR")
            Glide
                .with(context)
                .load(arrayList[position].productImage)
                .centerCrop()
                .placeholder(R.drawable.candle)
                .into(binding.imgproduct)
        }



    }

    override fun getItemCount(): Int =arrayList.size
}