package com.codewithram.medvault.ui.notifications

import android.app.*
import android.content.*
import android.net.*
import android.os.*
import android.provider.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.codewithram.medvault.R
import com.codewithram.medvault.broadcasts.AlarmReceiver
import com.codewithram.medvault.network.ApiClient
import com.codewithram.medvault.network.models.Medication
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.InputStream
import java.util.concurrent.TimeUnit

class NotificationsFragment : Fragment() {

    private val TAG = "MedVault"
    private val PICK_PRESC = 1301
    private var selectedUri: Uri? = null
    private var extractedMeds: List<Medication> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        Log.d(TAG, "NotificationsFragment initialized")

        val btnPick = root.findViewById<Button>(R.id.btn_pick_prescription)
        val ivPreview = root.findViewById<ImageView>(R.id.iv_prescription_preview)
        val btnExtract = root.findViewById<Button>(R.id.btn_extract)
        val tvExtraction = root.findViewById<TextView>(R.id.tv_extraction)
        val btnSetAlarms = root.findViewById<Button>(R.id.btn_set_alarms)

        btnPick.setOnClickListener {
            Log.d(TAG, "Opening image picker")
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/png", "image/jpeg"))
            }
            startActivityForResult(Intent.createChooser(intent, "Select prescription"), PICK_PRESC)
        }

        btnExtract.setOnClickListener {
            val uri = selectedUri ?: run {
                Toast.makeText(requireContext(), "Pick an image first", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "No image selected")
                return@setOnClickListener
            }

            tvExtraction.text = "Extracting..."
            Log.d(TAG, "Starting extraction for URI: $uri")

            lifecycleScope.launch {
                try {
                    val part = createMultipartFromUri(uri, "file")
                    Log.d(TAG, "Multipart created, calling API...")

                    val resp = ApiClient.apiService.extractPrescription(part)
                    Log.d(TAG, "Response received: success=${resp.isSuccessful}")

                    if (resp.isSuccessful && resp.body() != null) {
                        val body = resp.body()!!
                        val summaryText = body.summary.toString() ?: ""
                        Log.d(TAG, "Summary received:\n$summaryText")

                        extractedMeds = parseMedications(summaryText)
                        Log.d(TAG, "Extracted meds: $extractedMeds")

                        if (extractedMeds.isEmpty()) {
                            tvExtraction.text = "No medicines found."
                        } else {
                            tvExtraction.text = extractedMeds.joinToString("\n") { med ->
                                buildString {
                                    append(med.name)
                                    med.dosage?.let { append(" — $it") }
                                    med.frequency?.let { append(", $it") }
                                    med.duration?.let { append(", $it") }
                                }
                            }
                        }
                    } else {
                        tvExtraction.text = "Error: ${resp.message()}"
                        Log.e(TAG, "Extraction failed: ${resp.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    tvExtraction.text = "Extraction failed: ${e.message}"
                    Log.e(TAG, "Exception during extraction", e)
                }
            }
        }

        btnSetAlarms.setOnClickListener {
            if (extractedMeds.isEmpty()) {
                Toast.makeText(requireContext(), "No medications extracted", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "No medications available to set alarms")
                return@setOnClickListener
            }
            Log.d(TAG, "Scheduling alarms for ${extractedMeds.size} medications")
            scheduleMedicationAlarms(extractedMeds)
            Toast.makeText(requireContext(), "Medication alarms scheduled!", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PRESC && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedUri = uri
                Log.d(TAG, "Prescription image selected: $uri")
                view?.findViewById<ImageView>(R.id.iv_prescription_preview)?.let {
                    Glide.with(this).load(uri).into(it)
                }
            }
        }
    }

    private suspend fun createMultipartFromUri(uri: Uri, partName: String): MultipartBody.Part {
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "Creating multipart for URI: $uri")
            val resolver = requireContext().contentResolver
            val inputStream: InputStream = resolver.openInputStream(uri)!!
            val fileBytes = inputStream.readBytes()
            val mime = resolver.getType(uri) ?: "image/jpeg"
            val filename = queryName(uri)
            Log.d(TAG, "File: $filename, mime=$mime, size=${fileBytes.size}")
            val requestFile = RequestBody.create(mime.toMediaTypeOrNull(), fileBytes)
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
        Log.d(TAG, "Resolved file name: $name")
        return name
    }

    private fun frequencyToHours(freq: String?): Long {
        Log.d(TAG, "Converting frequency to hours: $freq")
        if (freq == null) return 24L
        return when {
            freq.contains("once", true) -> 24L
            freq.contains("twice", true) -> 12L
            freq.contains("thrice", true) -> 8L
            freq.contains("every 6", true) -> 6L
            freq.contains("every 8", true) -> 8L
            else -> 24L
        }
    }

    private fun durationToDays(duration: String?): Int {
        Log.d(TAG, "Converting duration to days: $duration")
        if (duration == null) return 1
        return when {
            duration.contains("week", true) -> {
                val num = duration.filter { it.isDigit() }.toIntOrNull() ?: 1
                num * 7
            }
            duration.contains("day", true) -> duration.filter { it.isDigit() }.toIntOrNull() ?: 1
            else -> 1
        }
    }

    private fun scheduleMedicationAlarms(meds: List<Medication>) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = System.currentTimeMillis()
        Log.d(TAG, "Scheduling alarms... currentTime=$now")

        meds.forEachIndexed { idx, med ->
            val hoursBetween = frequencyToHours(med.frequency)
            val totalDays = durationToDays(med.duration)
            val totalAlarms = (24 / hoursBetween) * totalDays
            Log.d(TAG, "Scheduling for ${med.name}: every $hoursBetween hrs, $totalDays days, total=$totalAlarms alarms")

            for (i in 0 until totalAlarms) {
                val triggerAt = now + TimeUnit.HOURS.toMillis(hoursBetween * i)
                Log.d(TAG, "Alarm #$i for ${med.name} -> triggerAt=$triggerAt (${hoursBetween * i} hours from now)")

                val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
                    putExtra("title", "Time to take ${med.name}")
                    putExtra("text", "${med.dosage ?: ""} ${med.frequency ?: ""}")
                }

                val pending = PendingIntent.getBroadcast(
                    requireContext(),
                    (1000 + idx * 100 + i).toInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                                PendingIntent.FLAG_MUTABLE else 0
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pending)
                }
            }
        }
        Log.d(TAG, "All alarms scheduled successfully.")
    }

    private fun parseMedications(summary: String): List<Medication> {
        val meds = mutableListOf<Medication>()
        val pattern = Regex(
            """\* Medicine Name:\s*(.+?)\n\s*Dosage:\s*(.*?)\n\s*Frequency:\s*(.*?)\n\s*Route of Administration:\s*(.*?)\n\s*Duration:\s*(.*?)($|\n)""",
            RegexOption.IGNORE_CASE
        )

        pattern.findAll(summary).forEach { match ->
            val name = match.groupValues[1].trim()
            val dosage = match.groupValues[2].ifBlank { null }
            val frequency = match.groupValues[3].ifBlank { null }
            val route = match.groupValues[4].ifBlank { null }
            val duration = match.groupValues[5].ifBlank { null }

            meds.add(Medication(name, dosage, frequency, route, duration))
        }
        return meds
    }
}
