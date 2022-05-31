package com.example.android_communicator

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.android_communicator.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStreamReader
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
    @RequiresApi(Build.VERSION_CODES.M)
    private fun isDeviceOnline(context: Context):Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        if(network == null)
            return false

        val activeNetwork = connectivityManager.getNetworkCapabilities(network)
        if(activeNetwork == null)
            return false

        return (activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))

    }
    private fun constructRequest(): MyRequest {
        var myRequest = MyRequest()
        myRequest.url = binding.URLEditText.text.toString()
        myRequest.type = when (binding.requestTypeGroup.checkedRadioButtonId)
        {
            R.id.option_get ->  "GET"
            R.id.option_post -> "POST"
            else -> "GET"
        }
        myRequest.timeOut = 10000 //= 10 seconds
        myRequest.body = binding.requestBodyInput.text.toString()
        myRequest.headers = binding.headersEditText.text.toString().split(";") as MutableList<String>
        return myRequest
    }

    private fun sendRequest(myRequest: MyRequest){
        Toast.makeText(applicationContext,"this is NOT a toast message",Toast.LENGTH_SHORT).show()
        val url = URL(myRequest.url)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = myRequest.type
        connection.connectTimeout = myRequest.timeOut

        val data: ByteArray = myRequest.body.toByteArray(StandardCharsets.UTF_8)
        connection.setRequestProperty("charset", "utf-8")
        connection.setRequestProperty("Content-length", data.size.toString())
        connection.setRequestProperty("Content-Type", "application/json")

        //Write body
        try{
            val outputStream = DataOutputStream(connection.outputStream)
            outputStream.write(data)
        }
        catch(exception: Exception){
            Toast.makeText(applicationContext,"Connection error...",Toast.LENGTH_SHORT).show()
        }

        val inputStream:DataInputStream
        if(connection.responseCode/100 == 2){
            inputStream = DataInputStream(connection.inputStream)
        }
        else{
            inputStream = DataInputStream(connection.errorStream)
        }

        val reader = BufferedReader(InputStreamReader(inputStream))
        val output: String = reader.readLine()
        binding.responseBody.text = output

        binding.responseCode.text = connection.responseCode.toString()
        connection.disconnect();

    }
}

