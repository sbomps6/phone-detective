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

        recyclerView.layoutManager = LinearLayoutManager(this)

        btnScan.setOnClickListener {
            btnScan.isEnabled = false
            btnScan.text = "Scanning..."
            
            // Clear previous results visually if needed, but here we just rebuild the list
            val scanFindings = ArrayList<ScanResult>()

            thread {
                // --- PHASE 1: APP SCAN ---
                val pm = packageManager
                val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                var userAppCount = 0
                var suspiciousAppCount = 0

                // Add Header
                scanFindings.add(ScanResult("App Scan Started", "Analyzing installed applications...", ResultType.SAFE))

                for (app in packages) {
                    // Filter for User Apps (ignore system android apps)
                    if (app.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                        userAppCount++
                        val appName = pm.getApplicationLabel(app).toString()
                        val pkgName = app.packageName

                        // THE "BAD GUY" LIST:
                        // You can add more keywords here later!
                        if (appName.contains("Wifi", ignoreCase = true) || 
                            appName.contains("Tool", ignoreCase = true) ||
                            appName.contains("Tracker", ignoreCase = true) ||
                            appName.contains("Spy", ignoreCase = true)) {
                            
                            suspiciousAppCount++
                            scanFindings.add(ScanResult("Suspicious App Found", "$appName ($pkgName)", ResultType.WARNING))
                        }
                    }
                }

                // Explicitly report the result
                if (suspiciousAppCount == 0) {
                    scanFindings.add(ScanResult("App Scan Complete", "Scanned $userAppCount user apps. No suspicious keywords found.", ResultType.SAFE))
                } else {
                    scanFindings.add(ScanResult("App Scan Warning", "Found $suspiciousAppCount potential threats out of $userAppCount apps.", ResultType.WARNING))
                }

                // --- PHASE 2: LOG SCAN ---
                try {
                    // Header for Log Scan
                    // scanFindings.add(ScanResult("Log Scan Started", "Reading system logs...", ResultType.SAFE))
                    
                    val process = Runtime.getRuntime().exec("logcat -d -t 300")
                    val reader = BufferedReader(InputStreamReader(process.inputStream))
                    var line: String?
                    var logThreatsFound = 0

                    while (reader.readLine().also { line = it } != null) {
                        val logText = line ?: ""
                        
                        // Ignore harmless system noise
                        if (logText.contains("TaskInfo")) continue
                        if (logText.contains("InputMethodManager")) continue

                        // The "Red Flag" Keywords
                        if (logText.contains("location", ignoreCase = true)) {
                             logThreatsFound++
                             scanFindings.add(ScanResult("Location Accessed", logText.take(120) + "...", ResultType.DANGER))
                        } else if (logText.contains("camera", ignoreCase = true)) {
                             logThreatsFound++
                             scanFindings.add(ScanResult("Camera Accessed", logText.take(120) + "...", ResultType.DANGER))
                        } else if (logText.contains("mic", ignoreCase = true) || logText.contains("record", ignoreCase = true)) {
                             logThreatsFound++
                             scanFindings.add(ScanResult("Microphone Activity", logText.take(120) + "...", ResultType.DANGER))
                        }
                    }
                    
                    if (logThreatsFound == 0) {
                         scanFindings.add(ScanResult("System Logs Clear", "No active camera, mic, or location usage detected in recent logs.", ResultType.SAFE))
                    }

                } catch (e: Exception) {
                    scanFindings.add(ScanResult("Error", "Could not read system logs. Did you run the ADB command?", ResultType.DANGER))
                }

                // Update UI
                runOnUiThread {
                    recyclerView.adapter = ScanAdapter(scanFindings)
                    btnScan.text = "Scan Again"
                    btnScan.isEnabled = true
                }
            }
        }
    }
}
