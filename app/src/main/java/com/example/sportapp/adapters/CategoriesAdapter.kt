package com.example.sportapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.sportapp.databinding.ItemCategoryBinding
import com.example.sportapp.models.ui.Category

class CategoriesAdapter(val list: List<Category>): RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {


    class CategoryViewHolder(val binding: ViewBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding as ItemCategoryBinding
            binding.tvCategoryTittle.text = category.tittle
            binding.ivCategoryIcon.setImageResource(category.id)
        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CategoryViewHolder(binding)
    }
    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(list[position])
    }

}