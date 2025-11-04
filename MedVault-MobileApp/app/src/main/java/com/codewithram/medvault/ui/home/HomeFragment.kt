package com.codewithram.medvault.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.codewithram.medvault.R
import com.codewithram.medvault.network.ApiClient
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val etInput = root.findViewById<EditText>(R.id.et_input)
        val btnPredict = root.findViewById<Button>(R.id.btn_predict)
        val progress = root.findViewById<ProgressBar>(R.id.progress)
        val tvResult = root.findViewById<TextView>(R.id.tv_result)

        btnPredict.setOnClickListener {
            val text = etInput.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter symptoms", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progress.visibility = View.VISIBLE
            tvResult.text = ""

            lifecycleScope.launch {
                try {
                    val resp = ApiClient.apiService.predict(mapOf("text" to text))
                    if (resp.isSuccessful && resp.body() != null) {
                        tvResult.text = "Prediction: ${resp.body()!!.prediction}"
                    } else {
                        tvResult.text = "Error: ${resp.message()}"
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Prediction failed: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    progress.visibility = View.GONE
                }
            }
        }

        return root
    }
}
