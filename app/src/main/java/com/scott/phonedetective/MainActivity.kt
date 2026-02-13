package com.scott.phonedetective

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnScan = findViewById<Button>(R.id.btnScan)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        // Set up the list manager
        recyclerView.layoutManager = LinearLayoutManager(this)

        btnScan.setOnClickListener {
            btnScan.isEnabled = false
            btnScan.text = "Scanning..."

            // Use a list to hold our findings
            val scanFindings = ArrayList<ScanResult>()

            thread {
                // 1. Check Apps
                val pm = packageManager
                val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                
                // Add a header for apps
                scanFindings.add(ScanResult("App Scan Started", "Checking installed applications...", ResultType.SAFE))

                for (app in packages) {
                    if (app.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                        val appName = pm.getApplicationLabel(app).toString()
                        // Flag suspicious names
                        if (appName.contains("Wifi", ignoreCase = true) || appName.contains("Tool", ignoreCase = true)) {
                            scanFindings.add(ScanResult("Suspicious App Name", "$appName (${app.packageName})", ResultType.WARNING))
                        }
                    }
                }

                // 2. Check Logs
                try {
                    val process = Runtime.getRuntime().exec("logcat -d -t 200") // Increased to 200 lines
                    val reader = BufferedReader(InputStreamReader(process.inputStream))
                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        val logText = line ?: ""
                        
                        // Filter out the noise (AppCompat)
                        if (logText.contains("TaskInfo")) continue

                        if (logText.contains("location", ignoreCase = true)) {
                             scanFindings.add(ScanResult("Location Accessed", logText.take(100) + "...", ResultType.DANGER))
                        } else if (logText.contains("camera", ignoreCase = true) || logText.contains("mic", ignoreCase = true)) {
                             scanFindings.add(ScanResult("Camera/Mic Activity", logText.take(100) + "...", ResultType.DANGER))
                        }
                    }
                } catch (e: Exception) {
                    scanFindings.add(ScanResult("Error", "Could not read system logs.", ResultType.DANGER))
                }

                // 3. Update UI
                runOnUiThread {
                    // Plug the findings into the adapter
                    recyclerView.adapter = ScanAdapter(scanFindings)
                    
                    btnScan.text = "Scan Again"
                    btnScan.isEnabled = true
                }
            }
        }
    }
}
