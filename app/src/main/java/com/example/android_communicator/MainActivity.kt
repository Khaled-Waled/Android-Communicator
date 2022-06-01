package com.example.android_communicator

import android.content.Context
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.android_communicator.MainActivity.singleton.mySingletonRequest
import com.example.android_communicator.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.Collections.singleton

class MainActivity : AppCompatActivity() {

    companion object singleton {
        var mySingletonRequest: MyRequest? = null
    }

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sendButton = binding.sendButton
        sendButton.setOnClickListener {
            sendClicked()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun sendClicked() {
        if (!isDeviceOnline(this)) {
            Toast.makeText(applicationContext, "Device is offline", Toast.LENGTH_SHORT).show()
            return
        }
        val request: MyRequest? = constructRequest()
        if (request != null) {
            mySingletonRequest = request
            val intent = Intent(this, ResponseActivity::class.java)
            this.startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isDeviceOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        if (network == null)
            return false

        val activeNetwork = connectivityManager.getNetworkCapabilities(network)
        if (activeNetwork == null)
            return false

        return (activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun constructRequest(): MyRequest? {
        val myRequest = MyRequest()

        //invalidate input if a field is empty
        if (binding.URLEditText.text.toString() == "" ||
            binding.requestBodyInput.text.toString() == "" ||
            binding.headersEditText.text.toString() == ""
        ) {
            Toast.makeText(applicationContext, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            return null
        }

        //take input
        myRequest.url = binding.URLEditText.text.toString()
        myRequest.type = when (binding.requestTypeGroup.checkedRadioButtonId) {
            R.id.option_get -> "GET"
            R.id.option_post -> "POST"
            else -> "GET"
        }
        myRequest.timeOut = 10000 //= 10 seconds
        myRequest.body = binding.requestBodyInput.text.toString()
        myRequest.headers =
            binding.headersEditText.text.toString().split(";") as MutableList<String>
        return myRequest
    }
}

