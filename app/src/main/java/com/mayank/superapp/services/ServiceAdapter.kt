package com.mayank.superapp.services

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mayank.superapp.databinding.ItemServiceBinding

class ServiceAdapter(
    private val onClick: (Service) -> Unit
) : ListAdapter<Service, ServiceAdapter.ServiceViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ServiceViewHolder(private val binding: ItemServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Service) {
            binding.tvName.text = item.name
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Service>() {
        override fun areItemsTheSame(oldItem: Service, newItem: Service): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Service, newItem: Service): Boolean = oldItem == newItem
    }
}
