package com.example.yourstory.today
import android.content.Context
import android.media.MediaPlayer
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.File
import java.io.IOException
import android.view.ViewGroup.MarginLayoutParams
import android.animation.ValueAnimator

class DiaryEntriesAdapter : RecyclerView.Adapter<DiaryEntriesAdapter.ViewHolder>() {

    private var todayModelData: List<Entry> = listOf()
    private lateinit var view: View
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryEntriesAdapter.ViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.text_entry_diary_layout, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryEntriesAdapter.ViewHolder, position: Int) {
        // views cant be recycled if nodes from the view are removed
        holder.setIsRecyclable(false)
        holder.date.text =
            DateEpochConverter.convertEpochToDateTime(todayModelData[position].date).toString()
                .split("T")[1].subSequence(0, 5)

        if (todayModelData[position] is DiaryEntry) {
            // nodes from emotional state arent needed
            if (holder.emotionalStateRoot.parent is ViewGroup) {
               (holder.emotionalStateRoot.parent as ViewGroup).removeView(holder.emotionalStateRoot)
            }
            val entry = todayModelData[position] as DiaryEntry

            // Following each component of the possible entry will be processed separately and in the end the layout will be reorganized
            // based on the remaining nodes
            var imageFlag = false
            var locationFlag = false
            var textFlag = false
            var audioFlag = false

            if (entry.text.isEmpty() && holder.diaryText.parent != null) {
                (holder.diaryText.parent as ViewGroup).removeView(holder.diaryText)
            } else {
                textFlag = true
                holder.diaryText.text = entry.text
            }

            if (entry.image.isEmpty() && holder.diaryImage.parent != null) {
                (holder.diaryImage.parent as ViewGroup).removeView(holder.diaryImage)
            } else {
                imageFlag = true
                holder.diaryImage.setImageURI(File(context.filesDir, entry.image + ".png").toUri())
                holder.diaryImage.clipToOutline = true
            }

            if (entry.locationLat == 0.0 && entry.locationLong == 0.0 && holder.diaryLocation.parent != null) {
                (holder.diaryLocation.parent as ViewGroup).removeView(holder.diaryLocation)
            } else {
                locationFlag = true
                holder.diaryLocation.clipToOutline = true
                holder.locationMapView.getMapAsync { map ->
                    val location = LatLng(entry.locationLat, entry.locationLong)
                    map.addMarker(
                        MarkerOptions()
                            .position(location)
                    )
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 11f))
                    map.uiSettings.setAllGesturesEnabled(false)
                    map.uiSettings.isMapToolbarEnabled = false
                    holder.locationMapView.onResume()
                }
            }

            if (entry.audio == "" && holder.diaryAudio.parent != null) {
                (holder.diaryAudio.parent as ViewGroup).removeView(holder.diaryAudio)
            } else {
                audioFlag = true
                holder.diaryAudio.clipToOutline = true
                holder.seekBar.progress = 0
                val mediaPlayer = MediaPlayer().apply {
                    try {
                        setDataSource(entry.audio)
                        prepare()
                    } catch (e: IOException) { }
                }
                holder.seekBar.max = mediaPlayer.duration

                val animSeekbar = ValueAnimator.ofInt(0, holder.seekBar.max)
                animSeekbar.duration = mediaPlayer.duration.toLong()
                animSeekbar.addUpdateListener { animation ->
                    val animProgress = animation.animatedValue as Int
                    holder.seekBar.progress = animProgress
                }
                holder.playButton.setOnClickListener {
                    if (!mediaPlayer.isPlaying) {
                        mediaPlayer.start()
                        animSeekbar.start()
                        holder.playButton.setImageResource(R.drawable.pause_icon_media_player)
                    } else {
                        mediaPlayer.pause()
                        animSeekbar.pause()
                        holder.playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                }
                mediaPlayer.setOnCompletionListener {
                    holder.playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    holder.seekBar.progress = 0
                }
            }

            if (imageFlag) {
                if (!locationFlag) {
                    if (textFlag) {
                        (holder.diaryText.parent as ViewGroup).removeView(holder.diaryText)
                        val layoutParams = holder.diaryText.layoutParams as MarginLayoutParams
                        layoutParams.bottomMargin = 0
                        layoutParams.topMargin = 0
                        holder.diaryText.layoutParams = layoutParams
                        holder.diaryText.movementMethod = ScrollingMovementMethod()
                        holder.firstRowLinearLayoutSecondItem.addView(holder.diaryText)
                        if (audioFlag) {
                            val layoutParamsImage = holder.diaryImage.layoutParams as MarginLayoutParams
                            layoutParamsImage.bottomMargin = holder.itemView.context.resources.getDimension(R.dimen.standard_margin).toInt()
                            holder.diaryImage.layoutParams = layoutParamsImage
                            val layoutParamsText = holder.diaryText.layoutParams as MarginLayoutParams
                            layoutParamsText.bottomMargin = holder.itemView.context.resources.getDimension(R.dimen.standard_margin).toInt()
                            holder.diaryText.layoutParams = layoutParamsText

                        }
                    } else {
                        (holder.firstRowLinearLayoutSecondItem.parent as ViewGroup).removeView(holder.firstRowLinearLayoutSecondItem)
                        (holder.diaryImage.layoutParams as LinearLayout.LayoutParams).weight = 0f
                        holder.diaryImage.layoutParams.width = holder.itemView.context.resources.getDimension(R.dimen.new_image_width).toInt()
                        (holder.diaryImage.layoutParams as LinearLayout.LayoutParams).gravity =  Gravity.CENTER
                        holder.firstRowLinearLayout.orientation = LinearLayout.VERTICAL
                        val layoutParams = holder.diaryImage.layoutParams as MarginLayoutParams
                        layoutParams.bottomMargin = holder.itemView.context.resources.getDimension(R.dimen.standard_margin).toInt()
                        holder.diaryImage.layoutParams = layoutParams
                    }
                }
            }
            if (!textFlag && audioFlag) {
                val layoutParams = holder.diaryImage.layoutParams as MarginLayoutParams
                layoutParams.bottomMargin = holder.itemView.context.resources.getDimension(R.dimen.standard_margin).toInt()
                holder.diaryImage.layoutParams = layoutParams

                val layoutParamsLocation = holder.diaryLocation.layoutParams as MarginLayoutParams
                layoutParamsLocation.bottomMargin = holder.itemView.context.resources.getDimension(R.dimen.standard_margin).toInt()
                holder.diaryLocation.layoutParams = layoutParamsLocation
            }
            if (!imageFlag && locationFlag) {
                val layoutParamsLocation = holder.firstRowLinearLayoutSecondItem.layoutParams as MarginLayoutParams
                layoutParamsLocation.leftMargin = 0
                holder.firstRowLinearLayoutSecondItem.layoutParams = layoutParamsLocation
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
        val diaryLocation: CardView = itemView.findViewById(R.id.main_today_location)
        val locationMapView: MapView = itemView.findViewById(R.id.main_today_map_view)
        val playButton: ImageView = itemView.findViewById(R.id.entry_diary_play_button)
        val seekBar: SeekBar = itemView.findViewById(R.id.entry_diary_seek_bar)
        val date: TextView = itemView.findViewById((R.id.entry_date))
        val firstRowLinearLayout: LinearLayout = itemView.findViewById(R.id.text_diary_entry_first_row)
        val firstRowLinearLayoutSecondItem: LinearLayout = itemView.findViewById(R.id.text_entry_diary_layout_second_item)
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
        // special handling for maps, it seems to be necessary
        init {
            locationMapView.onCreate(null)
        }
    }
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

