package com.example.sportapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.sportapp.databinding.ItemCategoryHomeBinding
import com.example.sportapp.models.ui.Category
import com.example.sportapp.other.ext.returnIconId

class HomeCategoriesAdapter : RecyclerView.Adapter<HomeCategoriesAdapter.HomeCategoriesViewHolder>() {

    inner class HomeCategoriesViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tittle: String) {
            binding as ItemCategoryHomeBinding
            binding.tvCategoryTittle.text = tittle
            binding.categoryCard.setOnClickListener {
                onItemClickListener?.let { it(tittle) }
            }
            binding.ivCategoryIcon.setImageResource(tittle.returnIconId())
            binding.ivDeleteCategory.setOnClickListener {
                onDeleteButtonClickListener?.let { it(tittle) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCategoriesViewHolder {
        val binding = ItemCategoryHomeBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HomeCategoriesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeCategoriesViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount() = differ.currentList.size

    private val differCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    private var onItemClickListener: ((String)-> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    private var onDeleteButtonClickListener: ((String) -> Unit)? = null

    fun setOnDeleteButtonClickListener(listener: (String) -> Unit) {
        onDeleteButtonClickListener = listener
    }
}