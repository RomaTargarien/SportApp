package com.example.sportapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.sportapp.R
import com.example.sportapp.data.ui.Intro

class ViewPagerIntroAdapter(val items: List<Intro>) : RecyclerView.Adapter<ViewPagerIntroAdapter.ViewPagerViewHolder>(){

    class ViewPagerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var lottie: LottieAnimationView = view.findViewById(R.id.lottieIntro)
        private var tittle: TextView = view.findViewById(R.id.tvTittleIntro)
        private var subTittle: TextView = view.findViewById(R.id.tvSubTittleIntro)

        fun bind(item: Intro) {
            lottie.setAnimation(item.lottieId)
            tittle.text = item.title
            subTittle.text = item.subTittle
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.item_intro,parent,false)
        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}