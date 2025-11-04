package com.codewithram.medvault.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.codewithram.medvault.MainActivity
import com.codewithram.medvault.R
import com.codewithram.medvault.network.ApiClient
import com.codewithram.medvault.network.LoginRequest
import com.codewithram.medvault.network.SessionManager
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val etEmail = view.findViewById<EditText>(R.id.et_email)
        val etPassword = view.findViewById<EditText>(R.id.et_password)
        val btnLogin = view.findViewById<Button>(R.id.btn_login)

        SessionManager.init(requireContext())

        btnLogin.setOnClickListener {
            val username = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.login(LoginRequest(username, password))

                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null && body.username != null) {
                            SessionManager.saveUser(body.username, body.role)

                            // Extract cookie if available
                            response.headers().values("Set-Cookie").forEach { header ->
                                if (header.contains("JSESSIONID", true)) {
                                    SessionManager.saveSessionCookie(header)
                                }
                            }

                            Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()

                            // ✅ Start MainActivity and close LoginActivity
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            requireActivity().finish()
                        } else {
                            Toast.makeText(requireContext(), "Invalid login response", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Login failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }
}
