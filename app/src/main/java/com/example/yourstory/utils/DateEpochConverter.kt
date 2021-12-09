package com.example.yourstory.utils

import android.content.Context
import android.content.res.Resources
import com.example.yourstory.R
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.MonthDay
import java.text.SimpleDateFormat
import java.util.*

class DateEpochConverter {


    companion object {
        fun convertDateTimeToEpoch(isoString: String): Long {
            var dateTime = DateTime(isoString, DateTimeZone.UTC)
            return dateTime.millis / 1000
        }

        fun convertEpochToDateTime(epochString: Long): DateTime {
            return DateTime(epochString * 1000, DateTimeZone.UTC)
        }

        fun generateIsoDate(): String{
            return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.GERMANY).format(Date()).toString()
        }

        fun generateIsoDateWithoutTime():String{
            return SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY).format(Date()).toString()
        }

        fun generateEpochDate(): Long{
            return convertDateTimeToEpoch(generateIsoDate())
        }

        fun monthIntToString(context: Context,month: Int): String{
            when(month){
                1 -> return context.getString(R.string.month_january)
                2 -> return context.getString(R.string.month_february)
                3 -> return context.getString(R.string.month_march)
                4 -> return context.getString(R.string.month_april)
                5 -> return context.getString(R.string.month_may)
                6 -> return context.getString(R.string.month_june)
                7 -> return context.getString(R.string.month_july)
                8 -> return context.getString(R.string.month_august)
                9 -> return context.getString(R.string.month_september)
                10 -> return context.getString(R.string.month_october)
                11 -> return context.getString(R.string.month_november)
                12 -> return context.getString(R.string.month_december)
            }
            return "ERROR"
        }
    }
}