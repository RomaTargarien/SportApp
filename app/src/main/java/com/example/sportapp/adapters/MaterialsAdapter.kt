package com.example.sportapp.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportapp.R
import com.example.sportapp.models.rss.materials.Image
import com.example.sportapp.models.rss.materials.Item
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class MaterialsAdapter() : RecyclerView.Adapter<MaterialsAdapter.MaterialsViewHolder>() {


    class MaterialsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ivMaterial = view.findViewById<ImageView>(R.id.ivMaterial)
        private val tvMaterialTitle = view.findViewById<TextView>(R.id.tvMaterialTitle)
        private val tvMaterialPublishedDate = view.findViewById<TextView>(R.id.tvMaterialPublishedDate)
        private val formatter = SimpleDateFormat("HH:mm")

        fun bind(item: Item) {
            Glide.with(itemView.context)
                .load(item.enclosure.url)
                .into(ivMaterial)
            tvMaterialTitle.text = item.title
            tvMaterialPublishedDate.text = hoursAndMin(item.published)
        }

        private fun hoursAndMin(date:Date): String {
           return formatter.format(date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_material,parent,false)
        return MaterialsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MaterialsViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
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

}