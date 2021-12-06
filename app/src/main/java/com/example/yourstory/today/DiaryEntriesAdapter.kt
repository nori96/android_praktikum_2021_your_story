package com.example.yourstory.today

import android.graphics.BitmapFactory
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
import com.example.yourstory.database.data.DiaryEntry
import com.example.yourstory.utils.DateEpochConverter
import java.util.*

class DiaryEntriesAdapter() : RecyclerView.Adapter<DiaryEntriesAdapter.ViewHolder>() {

    private var todayModelData: List<DiaryEntry> = listOf()
    private lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryEntriesAdapter.ViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.text_entry_diary_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryEntriesAdapter.ViewHolder, position: Int) {
        var imageByteArray = todayModelData[position].image
        holder.diaryText.text = todayModelData[position].text
        if(!imageByteArray.isEmpty()) {
            holder.diaryImage.setImageBitmap(
               BitmapFactory.decodeByteArray(imageByteArray,0,imageByteArray.size)
            )
        }
        holder.date.text = DateEpochConverter.convertEpochToDateTime(todayModelData[position].date).toString().split("T")[1].subSequence(0,5)
        holder.diaryImage.clipToOutline = true
        holder.diaryLocation.clipToOutline = true
        holder.diaryAudio.clipToOutline = true
        if (todayModelData[position].image.isEmpty() && holder.diaryImage.parent != null) {
            (holder.diaryImage.parent as ViewGroup).removeView(holder.diaryImage)
        }
        if (todayModelData[position].location.equals("") && holder.diaryLocation.parent != null) {
            (holder.diaryLocation.parent as ViewGroup).removeView(holder.diaryLocation)
        }
        if(todayModelData[position].audio.equals("") && holder.diaryAudio.parent != null) {
            (holder.diaryAudio.parent as ViewGroup).removeView(holder.diaryAudio)
        }
        else {

            if (view.context.assets.list("")!!.contains(todayModelData[position].audio)) {
                var assetFileDescriptor =
                    view.context.resources.assets.openFd(todayModelData[position].audio)
                val mediaPlayer: MediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(
                    assetFileDescriptor.fileDescriptor,
                    assetFileDescriptor.startOffset,
                    assetFileDescriptor.length
                )
                holder.seekBar.progress = 0
                mediaPlayer.prepare()
                holder.seekBar.max = mediaPlayer.duration

                holder.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {


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
                lateinit var runnable: Runnable
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
    }

    override fun getItemCount(): Int {
        return todayModelData.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var diaryText: TextView
        var diaryImage: ImageView
        var diaryAudio: View
        var diaryLocation: ImageView
        var playButton: ImageView
        var seekBar: SeekBar
        var date: TextView

        init {
            date = itemView.findViewById((R.id.entry_date))
            diaryText = itemView.findViewById(R.id.main_today_text)
            diaryImage = itemView.findViewById(R.id.main_today_image)
            diaryAudio = itemView.findViewById(R.id.main_today_audio_source)
            diaryLocation = itemView.findViewById(R.id.main_today_location)
            playButton = itemView.findViewById(R.id.entry_diary_play_button)
            seekBar = itemView.findViewById(R.id.entry_diary_seek_bar)
        }
    }

    fun setData(diaries: List<DiaryEntry>){
        this.todayModelData = diaries
        notifyDataSetChanged()
    }
}

