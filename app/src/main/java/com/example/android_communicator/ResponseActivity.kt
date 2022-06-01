package com.example.android_communicator

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android_communicator.databinding.ActivityMainBinding
import com.example.android_communicator.databinding.ActivityResponseBinding
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class ResponseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResponseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_response)
        binding = ActivityResponseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MainActivity.mySingletonRequest?.let { sendRequest(it) }
    }


    private fun sendRequest(myRequest: MyRequest) {
        if (myRequest == null) {
            return
        }
        val url = URL(myRequest.url)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = myRequest.type
        connection.connectTimeout = myRequest.timeOut

        val data: ByteArray = myRequest.body.toByteArray(StandardCharsets.UTF_8)
        connection.setRequestProperty("charset", "utf-8")
        connection.setRequestProperty("Content-length", data.size.toString())
        connection.setRequestProperty("Content-Type", "application/json")

        //set Headers
        for (headerPair in myRequest.headers) {
            val entry = headerPair.split("=")
            connection.setRequestProperty(entry[0], entry[1])
        }

        //Write body
        try {
            val outputStream = DataOutputStream(connection.outputStream)
            outputStream.write(data)
        } catch (exception: Exception) {
            Toast.makeText(applicationContext, "Connection error...", Toast.LENGTH_SHORT).show()
        }

        //Get response
        val inputStream: DataInputStream

        if (connection.responseCode / 100 == 2) {
            inputStream = DataInputStream(connection.inputStream)
        } else {
            inputStream = DataInputStream(connection.errorStream)
        }

        val reader = BufferedReader(InputStreamReader(inputStream))
        val output: String = reader.readLine()
        binding.responseBody.text = output

        binding.responseCode.text = connection.responseCode.toString()
        connection.disconnect()

    }
}