package com.example.yourstory.today

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.R

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    //private var entryTexts = arrayOf("asjs","dfdf","asdf","ffasdf", "asdf", "asdf", "asdf", "asdf")
    //private var entryImages = intArrayOf(R.drawable.beach,R.drawable.beach,R.drawable.beach,R.drawable.beach,R.drawable.beach,R.drawable.beach,R.drawable.beach,R.drawable.beach)
    private var todayViewModel = TodayViewModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.text_entry_diary_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        holder.diaryText.text = todayViewModel.entries.value?.get(position)!!.diaryText//entryTexts[position]
        holder.diaryImage.setImageResource( todayViewModel.entries.value?.get(position)!!.diaryImage)
        // TODO following line does not work
        holder.diaryImage.clipToOutline = true
        if (todayViewModel.entries.value?.get(position)!!.diaryImage == 0) {
            (holder.diaryImage.parent as ViewGroup).removeView(holder.diaryImage)
        }
        if (!todayViewModel.entries.value?.get(position)!!.diaryLocation)
        {
            (holder.diaryLocation.parent as ViewGroup).removeView(holder.diaryLocation)
        }
        if(!todayViewModel.entries.value?.get(position)!!.diaryAudio)
        {
            (holder.diaryAudio.parent as ViewGroup).removeView(holder.diaryAudio)
        }

        //var imageView = v.findViewById<ImageView>(R.id.main_today_image)
        //
    }

    override fun getItemCount(): Int {
        //return entryTexts.size
        return todayViewModel.entries.value!!.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var diaryText: TextView
        var diaryImage: ImageView
        var diaryAudio: View
        var diaryLocation: ImageView

        init {
            diaryText = itemView.findViewById(R.id.main_today_text)
            diaryImage = itemView.findViewById(R.id.main_today_image)
            diaryAudio = itemView.findViewById(R.id.main_today_audio_source)
            diaryLocation = itemView.findViewById(R.id.main_today_location)
        }
    }
}

