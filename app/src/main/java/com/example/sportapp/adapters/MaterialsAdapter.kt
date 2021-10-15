package com.example.sportapp.adapters

import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.sportapp.R
import com.example.sportapp.databinding.ItemMaterialBinding
import com.example.sportapp.databinding.ItemMaterialHeaderBinding
import com.example.sportapp.models.rss.materials.Image
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.other.ext.display
import com.example.sportapp.other.toDp
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class MaterialsAdapter() : RecyclerView.Adapter<MaterialsAdapter.MaterialsViewHolder>() {

    private lateinit var binding: ViewBinding

    inner class MaterialsViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        private val formatter = SimpleDateFormat("HH:mm")
        fun bind(item: Item,itemViewType: Int,position: Int) {
            when(itemViewType) {
                HEADER -> {
                    binding as ItemMaterialHeaderBinding
                    binding.display(item, itemView, formatter)
                    binding.ivHeader.setOnClickListener {
                        onItemClickListener?.let { it(item) }
                    }
                }
                CARD -> {
                    binding as ItemMaterialBinding
                    binding.display(item, itemView, position, formatter)
                    binding.cardView.setOnClickListener {
                        onItemClickListener?.let { it(item) }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialsViewHolder {
        return when (viewType) {
            CARD -> {
                binding = ItemMaterialBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                MaterialsViewHolder(binding)
            }
            HEADER -> {
                binding = ItemMaterialHeaderBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                MaterialsViewHolder(binding)
            }
            else -> {
                throw IllegalArgumentException("")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
       return when {
           position == 0 -> HEADER
           else -> CARD
       }
    }

    override fun onBindViewHolder(holder: MaterialsViewHolder, position: Int) {
        holder.bind(differ.currentList[position],holder.itemViewType,position)
    }

    override fun getItemCount() = differ.currentList.size

    private val differCallback = object : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.guid== newItem.guid
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    companion object {
        private const val HEADER = 0
        private const val CARD = 1
    }

    private var onItemClickListener: ((Item)-> Unit)? = null

    fun setOnItemClickListener(listener: (Item) -> Unit) {
        onItemClickListener = listener
    }
     
}    
     