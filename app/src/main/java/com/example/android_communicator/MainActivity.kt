package com.example.android_communicator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.android_communicator.databinding.ActivityMainBinding
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sendButton = binding.sendButton
        sendButton.setOnClickListener{
            val request:MyRequest = constructRequest()
            sendRequest(request)
        }
    }
    private fun constructRequest(): MyRequest {
        var myRequest = MyRequest()
        myRequest.url = binding.URLEditText.text.toString()
        myRequest.type = "POST"
        myRequest.timeOut = 10000 //= 10 seconds
        myRequest.body = binding.requestBodyInput.text.toString()
        myRequest.headers = binding.headersEditText.text.toString().split(";") as MutableList<String>
        return myRequest
    }

    private fun sendRequest(myRequest: MyRequest){
        val url = URL(myRequest.url)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = myRequest.type
        connection.connectTimeout = myRequest.timeOut

        val data: ByteArray = myRequest.body.toByteArray(StandardCharsets.UTF_8)
        connection.setRequestProperty("charset", "utf-8")
        connection.setRequestProperty("Content-length", data.size.toString())
        connection.setRequestProperty("Content-Type", "application/json")

        TODO("send the request")
    }
}

