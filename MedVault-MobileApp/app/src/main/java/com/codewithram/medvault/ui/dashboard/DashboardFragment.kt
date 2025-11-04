package com.codewithram.medvault.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.codewithram.medvault.R
import com.codewithram.medvault.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.InputStream

class DashboardFragment : Fragment() {

    private val PICK_IMAGE = 1201
    private var selectedUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val btnPick = root.findViewById<Button>(R.id.btn_pick_record)
        val ivPreview = root.findViewById<ImageView>(R.id.iv_preview)
        val btnSummarize = root.findViewById<Button>(R.id.btn_summarize)
        val tvSummary = root.findViewById<TextView>(R.id.tv_summary)

        btnPick.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/png", "image/jpeg"))
            }
            startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE)
        }

        btnSummarize.setOnClickListener {
            val uri = selectedUri
            if (uri == null) {
                Toast.makeText(requireContext(), "Pick an image first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            tvSummary.text = "Summarizing..."
            lifecycleScope.launch {
                try {
                    val part = createMultipartFromUri(uri, "file")
                    val resp = ApiClient.apiService.summarize(part)
                    if (resp.isSuccessful && resp.body() != null) {
                        tvSummary.text = resp.body()!!.summary
                    } else {
                        tvSummary.text = "Error: ${resp.message()}"
                    }
                } catch (e: Exception) {
                    tvSummary.text = "Summary failed: ${e.message}"
                }
            }
        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedUri = uri
                val iv = view?.findViewById<ImageView>(R.id.iv_preview)
                Glide.with(this).load(uri).into(iv!!)
            }
        }
    }

    private suspend fun createMultipartFromUri(uri: Uri, partName: String): MultipartBody.Part {
        return withContext(Dispatchers.IO) {
            val resolver = requireContext().contentResolver
            val inputStream: InputStream = resolver.openInputStream(uri)!!
            val fileBytes = inputStream.readBytes()
            val mime = resolver.getType(uri) ?: "image/jpeg"
            val requestFile = RequestBody.create(mime.toMediaTypeOrNull(), fileBytes)
            val filename = queryName(uri)
            MultipartBody.Part.createFormData(partName, filename, requestFile)
        }
    }

    private fun queryName(uri: Uri): String {
        var name = "image"
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx >= 0) name = it.getString(idx)
            }
        }
        return name
    }
}
