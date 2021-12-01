package com.example.yourstory.diary.detail


import java.io.Serializable

class Date(var day: String, var month: String, var year: String) : Serializable{

    override fun toString(): String = "$day.$month.$year"

}