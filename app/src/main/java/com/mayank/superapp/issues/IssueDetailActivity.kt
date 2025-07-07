package com.mayank.superapp.issues

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mayank.superapp.databinding.ActivityIssueDetailBinding

class IssueDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIssueDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIssueDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra(EXTRA_ID)
        binding.tvDetail.text = id ?: "Unknown"
    }

    companion object {
        const val EXTRA_ID = "issue_id"
    }
}
