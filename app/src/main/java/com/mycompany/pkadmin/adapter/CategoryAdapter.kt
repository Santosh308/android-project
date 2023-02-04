package com.mycompany.pkadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mycompany.pkadmin.R
import com.mycompany.pkadmin.databinding.ItemCategoryLayoutBinding
import com.mycompany.pkadmin.model.CategoryModel

class CategoryAdapter(var context: Context, private val list : ArrayList<CategoryModel>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    /*inner class CategoryViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var binding = ItemCategoryLayoutBinding.bind(view)
    }*/
    /*override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category_layout, parent, false))
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_layout, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.textView.text = list[position].cat
        Glide.with(context).load(list[position].img).into(holder.imageView)
        //println("list size print adapter=----------- ${holder.binding.textView2.text}")
        //println("list size print adapter=----------- ${Glide.with(context).load(list[position].img).into(holder.binding.imageView2)}")
    }

    override fun getItemCount(): Int {
        println("list size print adapter=----------- ${list.size}")
        return list.size
    }

    class CategoryViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView2)
        val textView: TextView = itemView.findViewById(R.id.textView2)
    }
}