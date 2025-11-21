//package com.codewithram.medvault.ui.dashboard
//
//import android.app.Activity
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.provider.OpenableColumns
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import com.bumptech.glide.Glide
//import com.codewithram.medvault.R
//import com.codewithram.medvault.network.ApiClient
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import java.io.InputStream
//
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.RequestBody.Companion.asRequestBody
//import java.io.File
//
//suspend fun uploadMultipleFiles(files: List<File>) {
//    val parts = files.map { file ->
//        val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//        MultipartBody.Part.createFormData("files", file.name, requestBody)
//    }
//
//    val response = ApiClient.apiService.summarizeAll(parts)
//    if (response.isSuccessful) {
//        println("Response: ${response.body()}")
//    } else {
//        println("Error: ${response.errorBody()?.string()}")
//    }
//}
//
//class DashboardFragment : Fragment() {
//
//    private val PICK_IMAGE = 1201
//    private var selectedUri: Uri? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
//
//        val btnPick = root.findViewById<Button>(R.id.btn_pick_record)
//        val ivPreview = root.findViewById<ImageView>(R.id.iv_preview)
//        val btnSummarize = root.findViewById<Button>(R.id.btn_summarize)
//        val tvSummary = root.findViewById<TextView>(R.id.tv_summary)
//
//        btnPick.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
//                type = "image/*"
//                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/png", "image/jpeg"))
//            }
//            startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE)
//        }
//
//        btnSummarize.setOnClickListener {
//            val uri = selectedUri
//            if (uri == null) {
//                Toast.makeText(requireContext(), "Pick an image first", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            tvSummary.text = "Summarizing..."
//            lifecycleScope.launch {
//                try {
//                    val part = createMultipartFromUri(uri, "file")
//                    val resp = ApiClient.apiService.summarize(part)
//                    if (resp.isSuccessful && resp.body() != null) {
//                        tvSummary.text = resp.body()!!.summary
//                    } else {
//                        tvSummary.text = "Error: ${resp.message()}"
//                    }
//                } catch (e: Exception) {
//                    tvSummary.text = "Summary failed: ${e.message}"
//                }
//            }
//        }
//
//        return root
//    }
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
//            data?.data?.let { uri ->
//                selectedUri = uri
//                val iv = view?.findViewById<ImageView>(R.id.iv_preview)
//                Glide.with(this).load(uri).into(iv!!)
//            }
//        }
//    }
//
//    private suspend fun createMultipartFromUri(uri: Uri, partName: String): MultipartBody.Part {
//        return withContext(Dispatchers.IO) {
//            val resolver = requireContext().contentResolver
//            val inputStream: InputStream = resolver.openInputStream(uri)!!
//            val fileBytes = inputStream.readBytes()
//            val mime = resolver.getType(uri) ?: "image/jpeg"
//            val requestFile = RequestBody.create(mime.toMediaTypeOrNull(), fileBytes)
//            val filename = queryName(uri)
//            MultipartBody.Part.createFormData(partName, filename, requestFile)
//        }
//    }
//
//    private fun queryName(uri: Uri): String {
//        var name = "image"
//        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
//        cursor?.use {
//            if (it.moveToFirst()) {
//                val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//                if (idx >= 0) name = it.getString(idx)
//            }
//        }
//        return name
//    }
//}
//
//package com.codewithram.medvault.ui.dashboard
//
//import android.app.Activity
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.provider.OpenableColumns
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import com.bumptech.glide.Glide
//import com.codewithram.medvault.R
//import com.codewithram.medvault.network.ApiClient
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import okhttp3.RequestBody.Companion.toRequestBody
//
//
//class DashboardFragment : Fragment() {
//
//    private val PICK_FILES = 1201
//    private var selectedUris: List<Uri> = emptyList()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
//
//        val btnPick = root.findViewById<Button>(R.id.btn_pick_record)
//        val ivPreview = root.findViewById<ImageView>(R.id.iv_preview)
//        val btnSummarize = root.findViewById<Button>(R.id.btn_summarize)
//        val tvSummary = root.findViewById<TextView>(R.id.tv_summary)
//
//        btnPick.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
//                type = "*/*"
//                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//            }
//            startActivityForResult(Intent.createChooser(intent, "Select files"), PICK_FILES)
//        }
//
//        btnSummarize.setOnClickListener {
//            if (selectedUris.isEmpty()) {
//                Toast.makeText(requireContext(), "Select at least one file", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            tvSummary.text = "Summarizing..."
//            lifecycleScope.launch {
//                try {
//                    val parts = selectedUris.map { createMultipartFromUri(it, "files") }
//                    val response = ApiClient.apiService.summarizeAll(parts)
//                    if (response.isSuccessful && response.body() != null) {
//                        val result = response.body()!!
//                        tvSummary.text = buildString {
//                            append("📋 Summary:\n${result.summary}\n\n")
//                            append("🧾 All Summaries:\n")
//                            result.all_summaries.forEach { append("- $it\n") }
//                        }
//                    } else {
//                        tvSummary.text = "Error: ${response.message()}"
//                    }
//
//                } catch (e: Exception) {
//                    tvSummary.text = "Summary failed: ${e.message}"
//                }
//            }
//        }
//
//        return root
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_FILES && resultCode == Activity.RESULT_OK) {
//            selectedUris = mutableListOf<Uri>().apply {
//                data?.let {
//                    it.clipData?.let { clip ->
//                        for (i in 0 until clip.itemCount) add(clip.getItemAt(i).uri)
//                    } ?: it.data?.let { singleUri -> add(singleUri) }
//                }
//            }
//            val iv = view?.findViewById<ImageView>(R.id.iv_preview)
//            if (selectedUris.isNotEmpty()) {
//                Glide.with(this).load(selectedUris.first()).into(iv!!)
//            }
//        }
//    }
//
//    private suspend fun createMultipartFromUri(uri: Uri, partName: String): MultipartBody.Part {
//        return withContext(Dispatchers.IO) {
//            val resolver = requireContext().contentResolver
//            val inputStream = resolver.openInputStream(uri)!!
//            val bytes = inputStream.readBytes()
//            val mimeType = resolver.getType(uri) ?: "application/octet-stream"
//            val requestFile: RequestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
//            val fileName = queryName(uri)
//            MultipartBody.Part.createFormData(partName, fileName, requestFile)
//        }
//    }
//
//    private fun queryName(uri: Uri): String {
//        var name = "file"
//        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
//        cursor?.use {
//            if (it.moveToFirst()) {
//                val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//                if (idx >= 0) name = it.getString(idx)
//            }
//        }
//        return name
//    }
//}

package com.codewithram.medvault.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import okhttp3.RequestBody.Companion.toRequestBody

class DashboardFragment : Fragment() {

    private val PICK_FILES = 1201
    private var selectedUris: List<Uri> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val btnPick = root.findViewById<Button>(R.id.btn_pick_record)
        val btnSummarize = root.findViewById<Button>(R.id.btn_summarize)
        val tvSummary = root.findViewById<TextView>(R.id.tv_summary)
        val progress = root.findViewById<ProgressBar>(R.id.progress_loading)
        val fileLayout = root.findViewById<LinearLayout>(R.id.layout_selected_files)

        btnPick.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            startActivityForResult(Intent.createChooser(intent, "Select files"), PICK_FILES)
        }

        btnSummarize.setOnClickListener {
            if (selectedUris.isEmpty()) {
                Toast.makeText(requireContext(), "Please select files first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progress.visibility = View.VISIBLE
            tvSummary.visibility = View.GONE
            tvSummary.text = ""

            lifecycleScope.launch {
                try {
                    val parts = selectedUris.map { createMultipartFromUri(it, "files") }
                    val response = ApiClient.apiService.summarizeAll(parts)

                    progress.visibility = View.GONE
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()!!
                        tvSummary.visibility = View.VISIBLE
                        tvSummary.text = buildString {
                            append("📋 Summary:\n${result.summary}\n\n")
                            append("🧾 Detailed Summaries:\n")
                            result.all_summaries.forEach { append("• $it\n") }
                        }
                    } else {
                        tvSummary.visibility = View.VISIBLE
                        tvSummary.text = "⚠️ Error: ${response.message()}"
                    }
                } catch (e: Exception) {
                    progress.visibility = View.GONE
                    tvSummary.visibility = View.VISIBLE
                    tvSummary.text = "❌ Failed: ${e.message}"
                }
            }
        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILES && resultCode == Activity.RESULT_OK) {
            selectedUris = mutableListOf<Uri>().apply {
                data?.let {
                    it.clipData?.let { clip ->
                        for (i in 0 until clip.itemCount) add(clip.getItemAt(i).uri)
                    } ?: it.data?.let { singleUri -> add(singleUri) }
                }
            }

            val layout = view?.findViewById<LinearLayout>(R.id.layout_selected_files)
            layout?.removeAllViews()
            layout?.visibility = View.VISIBLE

            val inflater = LayoutInflater.from(requireContext())
            selectedUris.forEach { uri ->
                val card = inflater.inflate(R.layout.item_file_card, layout, false)
                val img = card.findViewById<ImageView>(R.id.iv_file_thumb)
                val name = card.findViewById<TextView>(R.id.tv_file_name)

                name.text = queryName(uri)
                Glide.with(this).load(uri).placeholder(R.drawable.ic_file).into(img)
                layout?.addView(card)
            }
        }
    }

    private suspend fun createMultipartFromUri(uri: Uri, partName: String): MultipartBody.Part {
        return withContext(Dispatchers.IO) {
            val resolver = requireContext().contentResolver
            val bytes = resolver.openInputStream(uri)?.readBytes() ?: ByteArray(0)
            val mimeType = resolver.getType(uri) ?: "application/octet-stream"
            val requestFile = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val fileName = queryName(uri)
            MultipartBody.Part.createFormData(partName, fileName, requestFile)
        }
    }

    private fun queryName(uri: Uri): String {
        var name = "file"
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

