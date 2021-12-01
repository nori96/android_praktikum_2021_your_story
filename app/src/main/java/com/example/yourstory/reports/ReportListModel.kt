package com.example.yourstory.reports

class ReportListModel {

    var date : String
    var interval : String
    var average : Double
    var joyAverage: Float
    var angerAverage: Float
    var supriseAverage: Float
    var sadnessAverage: Float
    var disgustAverage: Float
    var fearAverage: Float


    constructor(
        date: String,
        interval: String,
        average: Double,
        joyAverage: Float,
        angerAverage: Float,
        supriseAverage: Float,
        sadnessAverage: Float,
        disgustAverage: Float,
        fearAverage: Float
    ) {
        this.date = date
        this.interval = interval
        this.average = average
        this.joyAverage = joyAverage
        this.angerAverage = angerAverage
        this.supriseAverage = supriseAverage
        this.sadnessAverage = sadnessAverage
        this.disgustAverage = disgustAverage
        this.fearAverage = fearAverage
    }
}