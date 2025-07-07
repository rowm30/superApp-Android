package com.mayank.superapp.services

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mayank.superapp.databinding.ActivityServicesBinding
import kotlinx.coroutines.flow.collectLatest

class ServicesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServicesBinding
    private val viewModel: ServicesViewModel by viewModels()
    private lateinit var adapter: ServiceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ServiceAdapter { service ->
            val intent = Intent(this, ServiceDetailActivity::class.java)
            intent.putExtra(ServiceDetailActivity.EXTRA_ID, service.id)
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadServices() }

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest { state ->
                adapter.submitList(state.services)
                binding.swipeRefresh.isRefreshing = state.loading
            }
        }
    }
}
