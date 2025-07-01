package com.example.rotiku

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rotiku.data.BakeryItem
import com.example.rotiku.databinding.ItemMenuBinding

class MenuAdapter(private val onItemClick: (BakeryItem) -> Unit) :
    ListAdapter<BakeryItem, MenuAdapter.MenuViewHolder>(MenuDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("MenuAdapter", "Binding item: ${item.name}, ID: ${item.id}")
        holder.bind(item)
    }

    inner class MenuViewHolder(private val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BakeryItem) {
            binding.tvItemName.text = item.name
            binding.tvItemDescription.text = item.description
            binding.tvItemPrice.text = "Rp ${item.price}"
            binding.root.setOnClickListener {
                Log.d("MenuAdapter", "Item clicked: ${item.name}, ID: ${item.id}")
                onItemClick(item)
            }
        }
    }

    class MenuDiffCallback : DiffUtil.ItemCallback<BakeryItem>() {
        override fun areItemsTheSame(oldItem: BakeryItem, newItem: BakeryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BakeryItem, newItem: BakeryItem): Boolean {
            return oldItem == newItem
        }
    }
}