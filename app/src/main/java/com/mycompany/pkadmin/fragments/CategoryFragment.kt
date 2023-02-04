package com.mycompany.pkadmin.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.mycompany.pkadmin.R
import com.mycompany.pkadmin.adapter.CategoryAdapter
import com.mycompany.pkadmin.databinding.FragmentCategoryBinding
import com.mycompany.pkadmin.model.CategoryModel
import java.util.*

class CategoryFragment : Fragment() {

    private lateinit var binding: FragmentCategoryBinding
    private var imageUrl : Uri? = null
    private lateinit var dialog: Dialog
    //private val adapter: CategoryAdapter? = null
    //private val recyclerView: RecyclerView? = null
    private lateinit var customAdapter: CategoryAdapter

//     private val list = ArrayList<CategoryModel>()
    // private lateinit var productsAdapter: CategoryAdapter
    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == Activity.RESULT_OK ){
            imageUrl = it.data!!.data
            binding.imageView.setImageURI(imageUrl)
        }
    }
        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            // Inflate the layout for this fragment
            binding = FragmentCategoryBinding.inflate(layoutInflater)
            dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.progress_layout)
            dialog.setCancelable(false)

            val list = ArrayList<CategoryModel>()
            Firebase.firestore.collection("categories")
                .get().addOnSuccessListener {
                    list.clear()
                    for (doc in it.documents){
                        val data = doc.toObject(CategoryModel::class.java)
                        list.add(data!!)
                    }
                    customAdapter = CategoryAdapter(requireContext(),list)
                    binding.categoryRecycler.layoutManager = LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL ,false)
                    binding.categoryRecycler.adapter = customAdapter
                }
                .addOnFailureListener{
                    dialog.dismiss()
                    Toast.makeText(requireContext(),"Something Went Wrong with storage.", Toast.LENGTH_SHORT).show()
                }
            binding.apply {
                imageView.setOnClickListener{
                    val intent = Intent("android.intent.action.GET_CONTENT")
                    intent.type = "image/*"
                    launchGalleryActivity.launch(intent)
                }
                button9.setOnClickListener {
                    validateData(binding.categoryName.text.toString())
                }
            }
            getData()
            return binding.root
    }

    private fun getData() {
        val list = ArrayList<CategoryModel>()
        Firebase.firestore.collection("categories")
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents){
                    val data = doc.toObject(CategoryModel::class.java)
                    list.add(data!!)
                }
                customAdapter = CategoryAdapter(requireContext(),list)
                binding.categoryRecycler.layoutManager = LinearLayoutManager(requireContext(),
                    LinearLayoutManager.HORIZONTAL ,false)
                binding.categoryRecycler.adapter = customAdapter
                //binding.categoryRecycler.isNestedScrollingEnabled = true
                //binding.categoryRecycler.setHasFixedSize(true)
                //binding.categoryRecycler.adapter = CategoryAdapter(requireContext(),list)
            }
    }

    private fun validateData(categoryName: String) {
        if(categoryName.isEmpty()){
            Toast.makeText(requireContext(),"Please Provide category name", Toast.LENGTH_SHORT).show()
        } else if(imageUrl == null){
            Toast.makeText(requireContext(),"Please select image", Toast.LENGTH_SHORT).show()
        }else{
            uploadImage(categoryName)
        }
    }

    private fun uploadImage(categoryName: String) {
        dialog.show()
        val fileName = UUID.randomUUID().toString()+".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("category/$fileName")
        Toast.makeText(requireContext(),"refStorage", Toast.LENGTH_SHORT).show()

        refStorage.putFile(imageUrl!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    storeData(categoryName,image.toString())
                }
            }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(),"Something Went Wrong with storage.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeData(categoryName: String, url: String) {
        val db = Firebase.firestore
        val data = hashMapOf<String, Any>(
            "cat" to categoryName,
            "img" to url
        )
        db.collection("categories").add(data)
            .addOnSuccessListener {
                dialog.dismiss()
                binding.imageView.setImageDrawable(resources.getDrawable(R.drawable.preview))
                binding.categoryName.text = null
                getData()
                Toast.makeText(requireContext(), "Category Added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(),"Something Went wrong", Toast.LENGTH_SHORT).show()
            }
        println("categories =-----------")


    }


}