package com.example.rotiku

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rotiku.data.AppDatabase
import com.example.rotiku.data.BakeryItem
import com.example.rotiku.data.Order
import com.example.rotiku.databinding.FragmentOrdersBinding
import com.example.rotiku.databinding.ItemOrderBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Periksa sesi pengguna
        val sharedPref = requireContext().getSharedPreferences("RotikuPrefs", Context.MODE_PRIVATE)
        val userType = sharedPref.getString("userType", "") ?: ""
        if (userType != "Admin") {
            Toast.makeText(context, "Akses hanya untuk Admin", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_ordersFragment_to_loginFragment)
            return
        }

        val database = AppDatabase.getDatabase(requireContext())
        val adapter = OrderAdapter()

        binding.rvOrders.layoutManager = LinearLayoutManager(context)
        binding.rvOrders.adapter = adapter

        // Load orders and bakery items
        lifecycleScope.launch {
            val orders = database.bakeryDao().getAllOrders()
            val items = database.bakeryDao().getAllBakeryItems().associateBy { it.id }
            adapter.submitList(orders.map { order ->
                OrderWithItem(
                    order = order,
                    itemName = items[order.bakeryItemId]?.name ?: "Unknown Item"
                )
            })
        }

        // Back button (logout)
        binding.ivBack.setOnClickListener {
            sharedPref.edit().clear().apply()
            findNavController().navigate(R.id.action_ordersFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class OrderWithItem(
    val order: Order,
    val itemName: String
)

class OrderAdapter : ListAdapter<OrderWithItem, OrderAdapter.OrderViewHolder>(
    object : DiffUtil.ItemCallback<OrderWithItem>() {
        override fun areItemsTheSame(oldItem: OrderWithItem, newItem: OrderWithItem): Boolean =
            oldItem.order.id == newItem.order.id
        override fun areContentsTheSame(oldItem: OrderWithItem, newItem: OrderWithItem): Boolean =
            oldItem == newItem
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class OrderViewHolder(private val binding: ItemOrderBinding) : ViewHolder(binding.root) {
        fun bind(item: OrderWithItem) {
            val order = item.order
            binding.tvOrderEmail.text = "Email: ${order.userEmail}"
            binding.tvOrderItemName.text = "Item: ${item.itemName}"
            binding.tvOrderLocation.text = "Lokasi: Lat ${order.latitude}, Long ${order.longitude}"
            binding.tvOrderDate.text = "Tanggal: ${
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(order.orderDate)
            }"
        }
    }
}