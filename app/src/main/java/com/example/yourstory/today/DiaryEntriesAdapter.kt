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
import androidx.cardview.widget.CardView
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
    private lateinit var mRecyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryEntriesAdapter.ViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.text_entry_diary_layout, parent, false)
        context = parent.context
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: DiaryEntriesAdapter.ViewHolder, position: Int) {
        // TODO pretty bad because its against the recycling in recyclerview, but its not allowed to delete nodes when it should stay recyclable
        holder.setIsRecyclable(false)

        holder.date.text =
            DateEpochConverter.convertEpochToDateTime(todayModelData[position].date).toString()
                .split("T")[1].subSequence(0, 5)

        if (todayModelData[position] is DiaryEntry) {

            if (holder.emotionalStateRoot.parent is ViewGroup) {
               (holder.emotionalStateRoot.parent as ViewGroup).removeView(holder.emotionalStateRoot)
            }

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
            if (entry.locationLat == 0.0 && entry.locationLong == 0.0 && holder.diaryLocation.parent != null) {
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
            if (holder.diaryText.parent is ViewGroup) {
                (holder.diaryText.parent as ViewGroup).removeView(holder.diaryText)
            }
            holder.joyLikert.text = state.joy.toString()
            holder.surpriseLikert.text = state.surprise.toString()
            holder.angerLikert.text = state.anger.toString()
            holder.sadnessLikert.text = state.sadness.toString()
            holder.fearLikert.text = state.fear.toString()
            holder.disgustLikert.text = state.disgust.toString()
            holder.joyEmoji.alpha = (0.2 + ((state.joy.toFloat()/5) * 0.8)).toFloat()
            holder.surpriseEmoji.alpha = (0.2 + ((state.surprise.toFloat()/5) * 0.8)).toFloat()
            holder.angerEmoji.alpha = (0.2 + ((state.anger.toFloat()/5) * 0.8)).toFloat()
            holder.sadnessEmoji.alpha = (0.2 + ((state.sadness.toFloat()/5) * 0.8)).toFloat()
            holder.fearEmoji.alpha = (0.2 + ((state.fear.toFloat()/5) * 0.8)).toFloat()
            holder.disgustEmoji.alpha = (0.2 + ((state.disgust.toFloat()/5) * 0.8)).toFloat()
        }
    }

    override fun getItemCount(): Int {
        return todayModelData.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        // entry nodes
        val diaryText: TextView = itemView.findViewById(R.id.main_today_text)
        val diaryImage: ImageView = itemView.findViewById(R.id.main_today_image)
        val diaryAudio: View = itemView.findViewById(R.id.main_today_audio_source)
        val diaryLocation: ImageView = itemView.findViewById(R.id.main_today_location)
        val playButton: ImageView = itemView.findViewById(R.id.entry_diary_play_button)
        val seekBar: SeekBar = itemView.findViewById(R.id.entry_diary_seek_bar)
        val date: TextView = itemView.findViewById((R.id.entry_date))

        // emotional state nodes
        val joyLikert: TextView = itemView.findViewById(R.id.joy_today_likert)
        val surpriseLikert: TextView = itemView.findViewById(R.id.surprise_today_likert)
        val angerLikert: TextView = itemView.findViewById(R.id.anger_today_likert)
        val sadnessLikert: TextView = itemView.findViewById(R.id.sadness_today_likert)
        val fearLikert: TextView = itemView.findViewById(R.id.fear_today_likert)
        val disgustLikert: TextView = itemView.findViewById(R.id.disgust_today_likert)
        val emotionalStateRoot: CardView = itemView.findViewById(R.id.emotional_state_root)
        val joyEmoji: ImageView = itemView.findViewById(R.id.emoji_today_joy)
        val surpriseEmoji: ImageView = itemView.findViewById(R.id.emoji_today_surprise)
        val angerEmoji: ImageView = itemView.findViewById(R.id.emoji_today_anger)
        val sadnessEmoji: ImageView = itemView.findViewById(R.id.emoji_today_sadness)
        val fearEmoji: ImageView = itemView.findViewById(R.id.emoji_today_fear)
        val disgustEmoji: ImageView = itemView.findViewById(R.id.emoji_today_disgust)
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
            this.todayModelData = (emotionalOnly + diaries).sortedBy { it.date }.reversed()
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
            this.todayModelData = ((diaryEntriesOnly + diaries).sortedBy { it.date }).reversed()
        }
        notifyDataSetChanged()
    }
}

