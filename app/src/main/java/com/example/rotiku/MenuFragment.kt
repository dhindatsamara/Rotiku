package com.example.rotiku

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rotiku.data.AppDatabase
import com.example.rotiku.data.BakeryItem
import com.example.rotiku.data.Order
import com.example.rotiku.databinding.FragmentMenuBinding
import com.example.rotiku.databinding.ItemBakeryBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.util.Date

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var adapter: BakeryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val database = AppDatabase.getDatabase(requireContext())

        val sharedPref = requireContext().getSharedPreferences("RotikuPrefs", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", "") ?: ""

        adapter = BakeryAdapter { item ->
            if (userEmail.isEmpty()) {
                Toast.makeText(context, "Sesi pengguna tidak ditemukan, silakan login ulang", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_menuFragment_to_loginFragment)
                return@BakeryAdapter
            }

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
                return@BakeryAdapter
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    lifecycleScope.launch {
                        val order = Order(
                            userEmail = userEmail,
                            bakeryItemId = item.id,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            orderDate = Date()
                        )
                        database.bakeryDao().insertOrder(order)
                        Toast.makeText(context, "Pesanan ${item.name} berhasil!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Gagal mendapatkan lokasi", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.rvBakeryItems.layoutManager = LinearLayoutManager(context)
        binding.rvBakeryItems.adapter = adapter

        lifecycleScope.launch {
            val items = database.bakeryDao().getAllBakeryItems()
            adapter.submitList(items)
        }

        binding.ivBack.setOnClickListener {
            sharedPref.edit().clear().apply()
            findNavController().navigate(R.id.action_menuFragment_to_loginFragment)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Izin lokasi diberikan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Izin lokasi diperlukan untuk memesan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 100
    }
}

class BakeryAdapter(
    private val onOrderClick: (BakeryItem) -> Unit
) : androidx.recyclerview.widget.ListAdapter<BakeryItem, BakeryAdapter.BakeryViewHolder>(
    object : androidx.recyclerview.widget.DiffUtil.ItemCallback<BakeryItem>() {
        override fun areItemsTheSame(oldItem: BakeryItem, newItem: BakeryItem): Boolean =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: BakeryItem, newItem: BakeryItem): Boolean =
            oldItem == newItem
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BakeryViewHolder {
        val binding = ItemBakeryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BakeryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BakeryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class BakeryViewHolder(private val binding: ItemBakeryBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BakeryItem) {
            binding.tvItemName.text = item.name
            binding.tvItemPrice.text = "Harga: Rp ${item.price}"
            binding.tvItemDescription.text = item.description
            binding.btnOrder.setOnClickListener { onOrderClick(item) }
        }
    }
}