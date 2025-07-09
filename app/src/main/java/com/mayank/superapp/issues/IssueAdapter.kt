package com.mayank.superapp.issues

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mayank.superapp.databinding.ItemIssueBinding

/** Adapter for issues list */
class IssueAdapter(
    private val onClick: (Issue) -> Unit
) : ListAdapter<Issue, IssueAdapter.IssueViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueViewHolder {
        val binding = ItemIssueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IssueViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IssueViewHolder, position: Int) {
        val issue = getItem(position)
        holder.bind(issue)
    }

    inner class IssueViewHolder(private val binding: ItemIssueBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Issue) {
            binding.tvTitle.text = item.title
            binding.tvStatus.text = item.status.name
            binding.tvReporter.text = item.reporterName
            binding.tvHandler.text = "Handled by ${item.handler}"
            binding.tvUpvotes.text = item.upvotes.toString()
            binding.tvDownvotes.text = item.downvotes.toString()
            binding.root.setOnClickListener { onClick(item) }

            binding.btnUpvote.setOnClickListener {
                val green = ContextCompat.getColor(binding.root.context, android.R.color.holo_green_dark)
                binding.btnUpvote.setColorFilter(green)
            }

            binding.btnDownvote.setOnClickListener {
                val red = ContextCompat.getColor(binding.root.context, android.R.color.holo_red_dark)
                binding.btnDownvote.setColorFilter(red)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Issue>() {
        override fun areItemsTheSame(oldItem: Issue, newItem: Issue): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Issue, newItem: Issue): Boolean = oldItem == newItem
    }
}
