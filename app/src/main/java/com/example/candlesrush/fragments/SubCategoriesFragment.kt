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
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.candlesrush.Constants
import com.example.candlesrush.activities.MainActivity
import com.example.candlesrush.R
import com.example.candlesrush.adapters.SubCategoriesAdapter
import com.example.candlesrush.databinding.FragmentSubCategoriesBinding
import com.example.candlesrush.interfaces.ClickInterface
import com.example.candlesrush.interfaces.ClickType
import com.example.candlesrush.models.SubCategory
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SubCategoriesFragment : Fragment() {
    lateinit var binding: FragmentSubCategoriesBinding
    lateinit var mainActivity: MainActivity
//    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var categoriesAdapter: SubCategoriesAdapter
    var categoriesList= arrayListOf<SubCategory>()
    val db = Firebase.firestore
    var collectionName = Constants.subCategories
    var categoryId = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity=activity as MainActivity
        arguments?.let {
            categoryId = it.getString(Constants.id,"") ?:""
        }
        Log.e("categoryId"," ${categoryId}")

        db.collection(collectionName).whereEqualTo("categoryId",categoryId)
            .addSnapshotListener{snapshots,e->
                if (e != null){
                    return@addSnapshotListener
                }
                for (snapshot in snapshots!!.documentChanges) {
                    val userModel = convertObject( snapshot.document)

                    when (snapshot.type) {
                        DocumentChange.Type.ADDED -> {
                            userModel?.let { categoriesList.add(it) }
                            Log.e("", "userModelList ${categoriesList.size}")
                        }
                        DocumentChange.Type.MODIFIED -> {
                            userModel?.let {
                                var index = getIndex(userModel)
                                if (index > -1)
                                    categoriesList.set(index, it)
                            }
                        }
                        DocumentChange.Type.REMOVED -> {
                            userModel?.let {
                                var index = getIndex(userModel)
                                if (index > -1)
                                    categoriesList.removeAt(index)
                            }
                        }
                    }
                }
                categoriesAdapter.notifyDataSetChanged()
            }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding= FragmentSubCategoriesBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoriesAdapter= SubCategoriesAdapter(requireContext(),categoriesList, object : ClickInterface {
            override fun onClick(position: Int, clickType: ClickType?): Boolean {
                when (clickType) {
                    ClickType.OnViewClick->{
                        mainActivity.navController.navigate(R.id.productDetailsFragment, bundleOf(Constants.id to categoriesList[position].subCatId ))                    }
                    ClickType.Delete -> {

                    }
                    ClickType.img->{
//                        showAddCategoryDialog(position)

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
                        .load(Uri.parse(categoriesList[position].subCatImage))
                        .centerCrop()
                        .placeholder(R.drawable.candle)
                        .into(it1)
                }
//                imageView.setImageURI(Uri.parse(categoriesList[position].categoryImgUri))
            }

        })
//        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSubCategory.layoutManager = GridLayoutManager(context,2)
        binding.recyclerSubCategory.adapter = categoriesAdapter


    }
    override fun onResume() {
        super.onResume()
        mainActivity.binding.toolBar.title = resources.getString(R.string.subcategories)
    }

    fun convertObject(snapshot: QueryDocumentSnapshot) : SubCategory?{
        val categoriesModel:SubCategory? =
            snapshot.toObject(SubCategory::class.java)
        categoriesModel?.subCatId = snapshot.id ?: ""
        return categoriesModel
    }

    fun getIndex(categoriesModel:SubCategory) : Int{
        var index = -1
        index = categoriesList.indexOfFirst { element ->
            element.subCatId?.equals(categoriesModel.subCatId) == true
        }
        return index
    }



}