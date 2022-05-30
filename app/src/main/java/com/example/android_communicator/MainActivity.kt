package com.example.android_communicator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.android_communicator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sendButton = binding.sendButton
        sendButton.setOnClickListener{sendRequest()}
    }
    private fun sendRequest()
    {

    }
}

