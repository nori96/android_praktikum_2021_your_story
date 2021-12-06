package com.example.yourstory.utils

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
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
    }
}