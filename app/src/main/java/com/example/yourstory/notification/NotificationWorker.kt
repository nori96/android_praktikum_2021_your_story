package com.example.yourstory.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings.Global.getString
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.yourstory.MainActivity
import com.example.yourstory.R
import com.example.yourstory.utils.Constants

class NotificationWorker(var appContext: Context, var workerParams: WorkerParameters) : Worker(appContext,workerParams){

    override fun doWork(): Result {

        var hoursSinceLast = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .getInt("interval_notification", 12);

        //Intent tapping the notification
        val intent = Intent(appContext,MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TASK}
        intent.putExtra("notification_intent",0)
        val tapIntent: PendingIntent = PendingIntent.getActivity(appContext,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)

        //Intent tapping the diary entry button
        val intentDiary = Intent(appContext,MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TASK }
        intentDiary.putExtra("notification_intent",Constants.NOTIFICATION_ENTRY_CLICKED_CODE)
        val diaryIntent: PendingIntent = PendingIntent.getActivity(appContext,Constants.NOTIFICATION_ENTRY_CLICKED_CODE,intentDiary,PendingIntent.FLAG_UPDATE_CURRENT)

        //Intent tapping the likert button
        val intentLikert = Intent(appContext,MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TASK }
        intentLikert.putExtra("notification_intent",Constants.NOTIFICATION_LIKERT_CLICKED_CODE)
        val likertIntent: PendingIntent = PendingIntent.getActivity(appContext,Constants.NOTIFICATION_LIKERT_CLICKED_CODE,intentLikert,PendingIntent.FLAG_UPDATE_CURRENT)



        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = appContext.getString(R.string.channel_name)
            val descriptionText = appContext.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        var builder = NotificationCompat.Builder(appContext,Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.diary_logo)
            .setContentTitle("Your Story")
            .setStyle(NotificationCompat.BigTextStyle().bigText("When you are in the mood you can use the buttons to capture either your mood or a thought."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.likert,appContext.getString(R.string.mood),likertIntent)
            .addAction(R.drawable.thought,appContext.getString(R.string.thought),diaryIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(appContext)){
            notify(Constants.NOTIFICATION_ID,builder.build())
        }
        return Result.success()
    }
}