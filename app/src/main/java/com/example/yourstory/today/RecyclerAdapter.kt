package com.example.yourstory.today

import android.media.Image
import android.media.MediaPlayer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.R

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var todayViewModel = TodayViewModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.text_entry_diary_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        holder.diaryText.text = todayViewModel.entries.value?.get(position)!!.diaryText
        holder.diaryImage.setImageResource( todayViewModel.entries.value?.get(position)!!.diaryImage)
        holder.diaryImage.clipToOutline = true
        holder.diaryLocation.clipToOutline = true
        holder.diaryAudio.clipToOutline = true
        if (todayViewModel.entries.value?.get(position)!!.diaryImage == 0) {
            (holder.diaryImage.parent as ViewGroup).removeView(holder.diaryImage)
        }
        if (!todayViewModel.entries.value?.get(position)!!.diaryLocation) {
            (holder.diaryLocation.parent as ViewGroup).removeView(holder.diaryLocation)
        }
        if(todayViewModel.entries.value?.get(position)!!.diaryAudio == 0) {
            (holder.diaryAudio.parent as ViewGroup).removeView(holder.diaryAudio)
        }
        else {
            val mediaPlayer: MediaPlayer = MediaPlayer.create(holder.diaryAudio.context, todayViewModel.entries.value?.get(position)!!.diaryAudio)
            holder.seekBar.progress = 0
            holder.seekBar.max = mediaPlayer.duration

            holder.seekBar.setOnSeekBarChangeListener (object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        mediaPlayer.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            })

            holder.playButton.setOnClickListener {
                if (!mediaPlayer.isPlaying) {
                    mediaPlayer.start()
                    holder.playButton.setImageResource(R.drawable.pause_icon_media_player)
                } else {
                    mediaPlayer.pause()
                    holder.playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                }
            }
            lateinit var runnable : Runnable
            var handler = Handler()
            runnable = Runnable {
                holder.seekBar.progress = mediaPlayer.currentPosition
                handler.postDelayed(runnable, 1000)
            }
            handler.postDelayed(runnable, 1000)
            mediaPlayer.setOnCompletionListener {
                holder.playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                holder.seekBar.progress = 0
            }
        }
    }

    override fun getItemCount(): Int {
        return todayViewModel.entries.value!!.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var diaryText: TextView
        var diaryImage: ImageView
        var diaryAudio: View
        var diaryLocation: ImageView
        var playButton: ImageView
        var seekBar: SeekBar

        init {
            diaryText = itemView.findViewById(R.id.main_today_text)
            diaryImage = itemView.findViewById(R.id.main_today_image)
            diaryAudio = itemView.findViewById(R.id.main_today_audio_source)
            diaryLocation = itemView.findViewById(R.id.main_today_location)
            playButton = itemView.findViewById(R.id.entry_diary_play_button)
            seekBar = itemView.findViewById(R.id.entry_diary_seek_bar)
        }
    }
}

