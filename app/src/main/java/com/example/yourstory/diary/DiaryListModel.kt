package com.example.yourstory.diary

import com.example.yourstory.diary.detail.Date

class DiaryListModel(
    var date: Date,
    var entries: Int,
    var joyAverage: Float,
    var angerAverage: Float,
    var surpriseAverage: Float,
    var sadnessAverage: Float,
    var disgustAverage: Float,
    var fearAverage: Float
) {
}