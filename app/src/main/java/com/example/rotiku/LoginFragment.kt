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
import com.example.rotiku.data.AppDatabase
import com.example.rotiku.data.User
import com.example.rotiku.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Silakan isi semua kolom", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = database.bakeryDao().getUserByEmailAndPassword(email, password)
                if (user != null) {
                    // Simpan email dan userType ke SharedPreferences
                    val sharedPref = requireContext().getSharedPreferences("RotikuPrefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("userEmail", user.email)
                        putString("userType", user.userType)
                        apply()
                    }

                    if (user.userType == "Buyer") {
                        findNavController().navigate(R.id.action_loginFragment_to_menuFragment)
                    } else if (user.userType == "Admin") {
                        findNavController().navigate(R.id.action_loginFragment_to_ordersFragment)
                    }
                } else {
                    Toast.makeText(context, "Email atau kata sandi salah", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.signupLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}