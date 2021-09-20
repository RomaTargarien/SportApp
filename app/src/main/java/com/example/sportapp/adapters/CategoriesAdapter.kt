package com.example.sportapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.sportapp.R
import com.example.sportapp.databinding.ItemCategoryBinding
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.models.ui.Category
import com.example.sportapp.other.states.Resource
import com.google.firebase.auth.FirebaseAuth

class CategoriesAdapter(): RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {


    inner class CategoryViewHolder(val binding: ViewBinding): RecyclerView.ViewHolder(binding.root) {

        val user = FirebaseAuth.getInstance().currentUser
        fun bind(category: Category) {
            binding as ItemCategoryBinding
            binding.tvCategoryTittle.text = category.tittle
            binding.ivCategoryIcon.setImageResource(category.id)
            binding.categoryCard.setOnClickListener {
                onItemClickListener?.let { it(category.tittle) }
            }

            binding.ibLike.setImageResource(
                if (!category.isLiked)
                    R.drawable.ic_like_border
                else
                    R.drawable.ic_like)

            binding.ibLike.setOnClickListener {
                if (user!!.isEmailVerified) {
                    binding.ibLike.setImageResource(
                        if (category.isLiked)
                            R.drawable.ic_like_border
                        else
                            R.drawable.ic_like)
                    onHeartClickListener?.let { it(Resource.Success(category.tittle)) }
                } else {
                    onHeartClickListener?.let { it(Resource.Error("Подтвердите почту")) }
                }
            }
        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CategoryViewHolder(binding)
    }
    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    private val differCallback = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    private var onItemClickListener: ((String)-> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    private var onHeartClickListener: ((Resource<String>) -> Unit)? = null

    fun setOnHeartClickListener(listener: (Resource<String>) -> Unit) {
        onHeartClickListener = listener
    }
}