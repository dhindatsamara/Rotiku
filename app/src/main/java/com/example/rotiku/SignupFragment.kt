package com.example.rotiku

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rotiku.data.AppDatabase
import com.example.rotiku.data.User
import com.example.rotiku.databinding.FragmentSignupBinding
import kotlinx.coroutines.launch

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userTypes = resources.getStringArray(R.array.user_types)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, userTypes)
        binding.userTypeInput.setAdapter(adapter)
        binding.userTypeInput.setOnClickListener {
            binding.userTypeInput.showDropDown()
        }

        val database = AppDatabase.getDatabase(requireContext())

        binding.signupButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            val confirmPassword = binding.confirmPasswordInput.text.toString().trim()
            val userType = binding.userTypeInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || userType.isEmpty()) {
                Toast.makeText(context, "Silakan isi semua kolom", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userType !in userTypes) {
                Toast.makeText(context, "Pilih tipe pengguna yang valid (Buyer atau Admin)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(context, "Kata sandi tidak cocok", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(context, "Format email tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val existingUser = database.bakeryDao().getUserByEmail(email)
                if (existingUser != null) {
                    Toast.makeText(context, "Email sudah terdaftar", Toast.LENGTH_SHORT).show()
                } else {
                    val user = User(email = email, password = password, userType = userType)
                    database.bakeryDao().insertUser(user)
                    Toast.makeText(context, "Pendaftaran berhasil", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
                }
            }
        }

        binding.loginLink.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}