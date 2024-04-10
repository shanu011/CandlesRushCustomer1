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
import com.example.candlesrush.adapters.CategoriesAdapter
import com.example.candlesrush.databinding.FragmentCategoriesBinding
import com.example.candlesrush.interfaces.ClickInterface
import com.example.candlesrush.interfaces.ClickType
import com.example.candlesrush.models.CategoriesModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CategoriesFragment : Fragment() {
    lateinit var binding:FragmentCategoriesBinding
    val db = Firebase.firestore
    lateinit var mainActivity: MainActivity
    var collectionName = Constants.categories
//    lateinit var layoutManager: RecyclerView.LayoutManager
    var categoriesList= arrayListOf<CategoriesModel>()
    lateinit var categoriesAdapter: CategoriesAdapter




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity=activity as MainActivity
        db.collection(collectionName).addSnapshotListener{snapshots,e->
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
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentCategoriesBinding.inflate(layoutInflater)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoriesAdapter= CategoriesAdapter(requireContext(),categoriesList,object : ClickInterface {
            override fun onClick(position: Int, clickType: ClickType?): Boolean {
                when (clickType) {
                   ClickType.img->{
                       mainActivity.navController.navigate(R.id.subCategoriesFragment, bundleOf(Constants.id to categoriesList[position].categoryId ))

                   }
                    ClickType.OnViewClick->{
                        mainActivity.navController.navigate(R.id.subCategoriesFragment, bundleOf(Constants.id to categoriesList[position].categoryId ))

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
                        .load(Uri.parse(categoriesList[position].categoryImgUri))
                        .centerCrop()
                        .placeholder(R.drawable.candle)
                        .into(it1)
                }
//                imageView.setImageURI(Uri.parse(categoriesList[position].categoryImgUri))
            }

        })
//        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCategory.layoutManager = GridLayoutManager(context,2)
        binding.recyclerCategory.adapter = categoriesAdapter
    }

    fun convertObject(snapshot: QueryDocumentSnapshot) : CategoriesModel?{
        val categoriesModel:CategoriesModel? =
            snapshot.toObject(CategoriesModel::class.java)
        categoriesModel?.categoryId = snapshot.id ?: ""
        return categoriesModel
    }

    override fun onResume() {
        super.onResume()
        mainActivity.binding.toolBar.title = resources.getString(R.string.categories)
    }

    fun getIndex(categoriesModel:CategoriesModel) : Int{
        var index = -1
        index = categoriesList.indexOfFirst { element ->
            element.categoryId?.equals(categoriesModel.categoryId) == true
        }
        return index
    }
}