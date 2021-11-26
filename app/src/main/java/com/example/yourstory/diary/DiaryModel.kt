package com.example.yourstory.diary

class DiaryModel {
    var date : String
    var entries : Int
    var joyAverage: Float
    var angerAverage: Float
    var supriseAverage: Float
    var sadnessAverage: Float
    var disgustAverage: Float
    var fearAverage: Float

    constructor(
        date: String,
        entries: Int,
        joyAverage: Float,
        angerAverage: Float,
        supriseAverage: Float,
        sadnessAverage: Float,
        disgustAverage: Float,
        fearAverage: Float
    ) {
        this.date = date
        this.entries = entries
        this.joyAverage = joyAverage
        this.angerAverage = angerAverage
        this.supriseAverage = supriseAverage
        this.sadnessAverage = sadnessAverage
        this.disgustAverage = disgustAverage
        this.fearAverage = fearAverage
    }


}