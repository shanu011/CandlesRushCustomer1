package com.example.candlesrush.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.candlesrush.Constants
import com.example.candlesrush.activities.MainActivity
import com.example.candlesrush.R
import com.example.candlesrush.adapters.OrdersAdapter
import com.example.candlesrush.databinding.FragmentMyOrdersListBinding
import com.example.candlesrush.interfaces.ClickInterface
import com.example.candlesrush.interfaces.ClickType
import com.example.candlesrush.models.PlaceOrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MyOrdersListFragment : Fragment() {
    lateinit var binding: FragmentMyOrdersListBinding
    val db = Firebase.firestore
    lateinit var mainActivity: MainActivity
    var collectionName = Constants.orderdetails
        lateinit var layoutManager: RecyclerView.LayoutManager
    var OrderList= arrayListOf<PlaceOrderModel>()
    lateinit var ordersAdapter: OrdersAdapter
    var mAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        // Check if a user is currently logged in
        val currentUser = mAuth.currentUser
        mainActivity=activity as MainActivity
        db.collection(collectionName).whereEqualTo("userid",currentUser?.uid)
            .addSnapshotListener{snapshots,e->
            if (e != null){
                return@addSnapshotListener
            }
            for (snapshot in snapshots!!.documentChanges) {
                val userModel = convertObject( snapshot.document)

                when (snapshot.type) {
                    DocumentChange.Type.ADDED -> {
                        userModel?.let { OrderList.add(it) }
                        Log.e("", "userModelList ${OrderList.size}")
                    }
                    DocumentChange.Type.MODIFIED -> {
                        userModel?.let {
                            var index = getIndex(userModel)
                            if (index > -1)
                                OrderList.set(index, it)
                        }
                    }
                    DocumentChange.Type.REMOVED -> {
                        userModel?.let {
                            var index = getIndex(userModel)
                            if (index > -1)
                                OrderList.removeAt(index)
                        }
                    }
                }
            }
            ordersAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMyOrdersListBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return (binding.root)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ordersAdapter= OrdersAdapter(requireContext(),OrderList,object :
            ClickInterface {
            override fun onClick(position: Int, clickType: ClickType?): Boolean {
                when (clickType) {
                    ClickType.AddSub->{

                    }
                    ClickType.OnViewClick->{
                        mainActivity.navController.navigate(R.id.orderDetailsFragment, bundleOf(Constants.id to OrderList[position].orderid ))
                    }
                    ClickType.Delete -> {

                    }
                    ClickType.img->{

                    }
                    else -> {}
                }
                return true
            }

            override fun view(position: Int, imageView: ImageView) {
//                imgCandle=imageView
                imageView?.let { it1 ->
                    Glide
                        .with(requireContext())
                        .load(Uri.parse(OrderList[position].productImage))
                        .centerCrop()
                        .placeholder(R.drawable.candle)
                        .into(it1)
                }
//                imageView.setImageURI(Uri.parse(categoriesList[position].categoryImgUri))
            }
        })
        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerOrdersList.layoutManager = layoutManager
        binding.recyclerOrdersList.adapter = ordersAdapter
    }
    override fun onResume() {
        super.onResume()
        mainActivity.binding.toolBar.title = resources.getString(R.string.my_orders)
    }

    fun convertObject(snapshot: QueryDocumentSnapshot) : PlaceOrderModel?{
        val placeOrderModel:PlaceOrderModel? =
            snapshot.toObject(PlaceOrderModel::class.java)
        placeOrderModel?.orderid = snapshot.id ?: ""
        return placeOrderModel
    }

    fun getIndex(placeOrderModel: PlaceOrderModel) : Int{
        var index = -1
        index = OrderList.indexOfFirst { element ->
            element.orderid?.equals(placeOrderModel.orderid) == true
        }
        return index
    }
}