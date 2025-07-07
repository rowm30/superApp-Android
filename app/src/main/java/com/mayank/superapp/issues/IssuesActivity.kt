package com.mayank.superapp.issues

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mayank.superapp.databinding.ActivityIssuesBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest

class IssuesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIssuesBinding
    private val viewModel: IssuesViewModel by viewModels()
    private lateinit var adapter: IssueAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIssuesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = IssueAdapter { issue ->
            val intent = Intent(this, IssueDetailActivity::class.java)
            intent.putExtra(IssueDetailActivity.EXTRA_ID, issue.id)
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadIssues() }

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest { state ->
                adapter.submitList(state.issues)
                binding.swipeRefresh.isRefreshing = state.loading
            }
        }
    }
}
