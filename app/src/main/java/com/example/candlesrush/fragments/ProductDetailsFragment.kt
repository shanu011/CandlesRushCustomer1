package com.example.candlesrush.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.example.candlesrush.Constants
import com.example.candlesrush.activities.MainActivity
import com.example.candlesrush.R
import com.example.candlesrush.databinding.FragmentProductDetailsBinding
import com.example.candlesrush.models.PlaceOrderModel
import com.example.candlesrush.models.SubCategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ProductDetailsFragment : Fragment() {
    lateinit var binding: FragmentProductDetailsBinding
    lateinit var mainActivity: MainActivity
    //    lateinit var layoutManager: RecyclerView.LayoutManager
//    lateinit var categoriesAdapter: SubCategoriesAdapter
    var subcategoriesList= arrayListOf<SubCategory>()
    val db = Firebase.firestore
    var collectionName = Constants.subCategories
    var subcategoryId = ""
    var colors=""
    var fragrances=""
    var shape=""
    var sizes=""
    var productimage=""
    var productPrice:Int?=0
    var mAuth = Firebase.auth
    var progressBar:ProgressBar?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity=activity as MainActivity
        progressBar = ProgressBar(requireContext(), null, android.R.attr.progressBarStyleSmall)
        arguments?.let {
            subcategoryId = it.getString(Constants.id,"") ?:""
        }
        Log.e("subcategoryId"," ${subcategoryId}")

        db.collection(collectionName).document(subcategoryId)
            .addSnapshotListener{snapshots,e->
                if (e != null){
                    return@addSnapshotListener
                }
                var model = snapshots?.toObject(SubCategory::class.java)
                binding.tvproductName.setText(model?.subCatName)
                binding.tvproductDes.setText(model?.subcatDescription)
                binding.tvproductPrice.setText("${model?.subcatPrice.toString()} INR")
                Log.e("description", "onCreate: ${model}", )
                productimage=model?.subCatImage.toString()
                productPrice=model?.subcatPrice
                Glide
                    .with(requireContext())
                    .load(model?.subCatImage)
                    .centerCrop()
                    .placeholder(R.drawable.candle)
                    .into(binding.imgProduct)
            }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentProductDetailsBinding.inflate(layoutInflater)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setColorspinner()
        setFragrancespinner()
        setShapespinner()
        setSizespinner()
        progressBar=binding.pbar

        mAuth = FirebaseAuth.getInstance()

        // Check if a user is currently logged in
        val currentUser = mAuth.currentUser

        binding.customizechkbox.setOnCheckedChangeListener { buttonView, isChecked ->

        if(binding.customizechkbox.isChecked==false){
            binding.llcustomize.visibility=View.GONE

        }else{
            binding.llcustomize.visibility=View.VISIBLE
        }
        }
        binding.btnAddOrder.setOnClickListener {
            binding.pbar.visibility=View.VISIBLE
            var placeOrderModel=PlaceOrderModel(
                userid = currentUser?.uid,
            productImage = productimage,
                productName = binding.tvproductName.text.toString(),
                productDes = binding.tvproductDes.text.toString(),
                productPrice = productPrice,
                productColor = colors,
                productFragrance = fragrances,
                productShape = shape,
                productSize = sizes

            )
            db.collection(Constants.orderdetails).add(placeOrderModel).addOnSuccessListener {

                binding.pbar.visibility=View.GONE

                AlertDialog.Builder(mainActivity).apply {
                    setTitle(resources.getString(R.string.alert))
                    setMessage(resources.getString(R.string.order_added_successfully))
                    setNegativeButton(resources.getString(R.string.back)) { _, _ ->
                        mainActivity.navController.popBackStack()
                    }
                    setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                        mainActivity.navController.navigate(R.id.myOrdersListFragment, bundleOf(Constants.id to it.id))
                    }
                    show()
                }
            }.addOnFailureListener {
                binding.pbar.visibility=View.GONE

                AlertDialog.Builder(mainActivity).apply {
                    setTitle(resources.getString(R.string.alert))
                    setMessage(resources.getString(R.string.sorry_cannot_add))
                    setPositiveButton(resources.getString(R.string.ok)) { _, _ -> }
                    show()
                }

            }
        }
    }
    override fun onResume() {
        super.onResume()
        mainActivity.binding.toolBar.title = resources.getString(R.string.product_details)
    }

    fun setColorspinner() {
        val items = arrayOf("Default Color","Red", "Blue", "Gold", "Brown", "Green")


        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerColor.adapter = adapter
        binding.spinnerColor.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    colors = parent?.getItemAtPosition(position).toString()

                }
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }


            }

    }

    fun setFragrancespinner() {
        val items = arrayOf("Default Fragrance" ,"Citrus fruits", "Aromatics", "Floral", "Fruity", "Spices","Balsamic")


        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerFragrance.adapter = adapter
        binding.spinnerFragrance.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    fragrances = parent?.getItemAtPosition(position).toString()

                }
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }


            }

    }
    fun setShapespinner() {
        val items = arrayOf("Default Shape","Round candle jars", "Square Candle Jars", "Mason Candle Jars"
            , "Straight Side Jars", "Apothecary Jars","Jelly Jars"
            ,"Luxury Candle Jars","Lotus candle jars")


        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerShape.adapter = adapter
        binding.spinnerShape.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    shape = parent?.getItemAtPosition(position).toString()

                }
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }


            }

    }
    fun setSizespinner() {
        val items = arrayOf("Default Size","Xs", "S", "M", "L", "Xl")


        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerSize.adapter = adapter
        binding.spinnerSize.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    sizes = parent?.getItemAtPosition(position).toString()

                }
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }


            }

    }


}