package com.example.rotiku

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.rotiku.data.AppDatabase
import com.example.rotiku.data.BakeryItem
import com.example.rotiku.data.User
import com.example.rotiku.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set FragmentContainerView invisible initially
        binding.navHostFragment.isVisible = false

        // Insert dummy data
        lifecycleScope.launch {
            val database = AppDatabase.getDatabase(this@MainActivity)
            database.bakeryDao().insertUser(User(email = "admin@rotiku.com", password = "admin123", userType = "Admin"))
            database.bakeryDao().insertUser(User(email = "buyer@rotiku.com", password = "buyer123", userType = "Buyer"))
            database.bakeryDao().insertBakeryItem(BakeryItem(name = "Roti Tawar", price = 15000.0, description = "Roti tawar lembut"))
            database.bakeryDao().insertBakeryItem(BakeryItem(name = "Kue Coklat", price = 25000.0, description = "Kue coklat manis"))
            database.bakeryDao().insertBakeryItem(BakeryItem(name = "Donat", price = 10000.0, description = "Donat gula empuk"))
        }

        // Handle Start button click
        binding.btnStart.setOnClickListener {
            binding.navHostFragment.isVisible = true
            binding.tvAppName.isVisible = false
            binding.tvTagline.isVisible = false
            binding.ivLogo.isVisible = false
            binding.btnStart.isVisible = false
            navController.navigate(R.id.loginFragment)
        }
    }
}