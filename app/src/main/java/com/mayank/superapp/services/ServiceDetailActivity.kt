package com.mayank.superapp.services

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mayank.superapp.databinding.ActivityServiceDetailBinding

class ServiceDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServiceDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra(EXTRA_ID)
        binding.tvDetail.text = id ?: "Unknown"
    }

    companion object {
        const val EXTRA_ID = "service_id"
    }
}
