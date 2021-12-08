package com.example.yourstory.today

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.R
import com.example.yourstory.database.data.DiaryEntry
import com.example.yourstory.database.data.EmotionalState
import com.example.yourstory.database.data.Entry
import com.example.yourstory.utils.DateEpochConverter
import java.io.File
import java.util.*

class DiaryEntriesAdapter() : RecyclerView.Adapter<DiaryEntriesAdapter.ViewHolder>() {

    private var todayModelData: List<Entry> = listOf()
    private lateinit var view: View
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryEntriesAdapter.ViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.text_entry_diary_layout, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryEntriesAdapter.ViewHolder, position: Int) {

        holder.date.text =
            DateEpochConverter.convertEpochToDateTime(todayModelData[position].date).toString()
                .split("T")[1].subSequence(0, 5)

        if (todayModelData[position] is DiaryEntry) {
            val entry = todayModelData[position] as DiaryEntry
            var imageUUID = entry.image
            holder.diaryText.text = entry.text
            if (!imageUUID.isEmpty()) {
                holder.diaryImage.setImageURI(File(context.filesDir, imageUUID + ".png").toUri())
            }

            holder.diaryImage.clipToOutline = true
            holder.diaryLocation.clipToOutline = true
            holder.diaryAudio.clipToOutline = true
            if (entry.image.isEmpty() && holder.diaryImage.parent != null) {
                (holder.diaryImage.parent as ViewGroup).removeView(holder.diaryImage)
            }
            if (entry.location.equals("") && holder.diaryLocation.parent != null) {
                (holder.diaryLocation.parent as ViewGroup).removeView(holder.diaryLocation)
            }
            if (entry.audio.equals("") && holder.diaryAudio.parent != null) {
                (holder.diaryAudio.parent as ViewGroup).removeView(holder.diaryAudio)
            } else {

                if (view.context.assets.list("")!!.contains(entry.audio)) {
                    var assetFileDescriptor =
                        view.context.resources.assets.openFd(entry.audio)
                    val mediaPlayer: MediaPlayer = MediaPlayer()
                    mediaPlayer.setDataSource(
                        assetFileDescriptor.fileDescriptor,
                        assetFileDescriptor.startOffset,
                        assetFileDescriptor.length
                    )
                    holder.seekBar.progress = 0
                    mediaPlayer.prepare()
                    holder.seekBar.max = mediaPlayer.duration

                    holder.seekBar.setOnSeekBarChangeListener(object :
                        SeekBar.OnSeekBarChangeListener {


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
        if (todayModelData[position] is EmotionalState) {
             val state = todayModelData[position] as EmotionalState
            if (holder.diaryLocation.parent is ViewGroup) {
                (holder.diaryLocation.parent as ViewGroup).removeView(holder.diaryLocation)
            }
            if (holder.diaryImage.parent is ViewGroup) {
                (holder.diaryImage.parent as ViewGroup).removeView(holder.diaryImage)
            }
            if (holder.diaryAudio.parent is ViewGroup) {
                (holder.diaryAudio.parent as ViewGroup).removeView(holder.diaryAudio)
            }
            holder.diaryText.text = "anger: "+state.anger+" surprise: "+ state.surprise
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

    @Synchronized
    fun setData(diaries: List<Entry>){
        if (diaries.isEmpty()) {
            return
        }
        if (diaries[0] is DiaryEntry) {
            val emotionalOnly = this.todayModelData.toMutableList()
            val iterator = emotionalOnly.listIterator()
            while (iterator.hasNext()) {
                val e = iterator.next()
                if (e is DiaryEntry) {
                    iterator.remove()
                }
            }
            this.todayModelData = (emotionalOnly + diaries).sortedBy { it.date }
        }
        else {
            val diaryEntriesOnly = this.todayModelData.toMutableList()
            val iterator = diaryEntriesOnly.listIterator()
            while (iterator.hasNext()) {
                val e = iterator.next()
                if (e is EmotionalState) {
                    iterator.remove()
                }
            }
            this.todayModelData = (diaryEntriesOnly + diaries).sortedBy { it.date }
        }
        notifyDataSetChanged()
    }
}

