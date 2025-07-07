package com.mayank.superapp.issues

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mayank.superapp.R
import com.mayank.superapp.databinding.FragmentIssuesBinding
import kotlinx.coroutines.flow.collectLatest

class IssuesFragment : Fragment(R.layout.fragment_issues) {

    private var _binding: FragmentIssuesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IssuesViewModel by viewModels()
    private lateinit var adapter: IssueAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentIssuesBinding.bind(view)

        adapter = IssueAdapter { issue ->
            val intent = Intent(requireContext(), IssueDetailActivity::class.java)
            intent.putExtra(IssueDetailActivity.EXTRA_ID, issue.id)
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadIssues() }

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest { state ->
                adapter.submitList(state.issues)
                binding.swipeRefresh.isRefreshing = state.loading
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
