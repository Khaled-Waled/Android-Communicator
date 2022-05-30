package com.example.android_communicator

class MyRequest (){
    var type:String = "GET"
    var url:String  = "127.0.0.1:8080"
    var body:String = ""
    var timeOut:Int = 10000
    var headers: MutableList<String> = mutableListOf()

}