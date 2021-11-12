package com.example.yourstory.reports

class ReportModel {

    var date : String
    var interval : String
    var average : Double


    constructor(date: String, interval: String, average: Double) {
        this.date = date
        this.interval = interval
        this.average = average
    }
}