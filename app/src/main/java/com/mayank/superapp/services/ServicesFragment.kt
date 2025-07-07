package com.mayank.superapp.services

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mayank.superapp.R
import com.mayank.superapp.databinding.FragmentServicesBinding
import kotlinx.coroutines.flow.collectLatest

class ServicesFragment : Fragment(R.layout.fragment_services) {

    private var _binding: FragmentServicesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ServicesViewModel by viewModels()
    private lateinit var adapter: ServiceAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentServicesBinding.bind(view)

        adapter = ServiceAdapter { service ->
            val intent = Intent(requireContext(), ServiceDetailActivity::class.java)
            intent.putExtra(ServiceDetailActivity.EXTRA_ID, service.id)
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadServices() }

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest { state ->
                adapter.submitList(state.services)
                binding.swipeRefresh.isRefreshing = state.loading
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
