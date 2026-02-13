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
            
            val scanFindings = ArrayList<ScanResult>()

            thread {
                // --- PHASE 1: APP SCAN ---
                val pm = packageManager
                val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                var userAppCount = 0
                var suspiciousAppCount = 0

                scanFindings.add(ScanResult("App Scan Started", "Analyzing installed applications...", "", ResultType.SAFE))

                for (app in packages) {
                    if (app.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                        userAppCount++
                        val appName = pm.getApplicationLabel(app).toString()
                        val pkgName = app.packageName

                        // SMART DETECTIVE LOGIC
                        var reason = ""
                        
                        if (appName.contains("Tracker", ignoreCase = true)) {
                            reason = "Why is this flagged?\nThe name 'Tracker' suggests this app monitors location or items. While often used for finding keys (like AirTags), it can be used to track people. Verify this is yours."
                        } else if (appName.contains("Spy", ignoreCase = true)) {
                            reason = "Why is this flagged?\n'Spy' apps are designed for surveillance. Unless you installed this for a game, this is a major privacy risk."
                        } else if (appName.contains("Wifi", ignoreCase = true)) {
                            reason = "Why is this flagged?\nWi-Fi tools can analyze your network traffic. If this is a flashlight or calculator app requesting Wi-Fi access, that is highly suspicious."
                        } else if (appName.contains("Tool", ignoreCase = true)) {
                            reason = "Why is this flagged?\nGeneric names like 'Tool' or 'Utility' are sometimes used to hide malicious software. Check if you recognize this specific tool."
                        }

                        // If we found a reason, add it to the list
                        if (reason.isNotEmpty()) {
                            suspiciousAppCount++
                            scanFindings.add(ScanResult("Suspicious App Found", "$appName ($pkgName)", reason, ResultType.WARNING))
                        }
                    }
                }

                if (suspiciousAppCount == 0) {
                    scanFindings.add(ScanResult("App Scan Complete", "Scanned $userAppCount user apps. No suspicious keywords found.", "", ResultType.SAFE))
                } else {
                    scanFindings.add(ScanResult("App Scan Warning", "Found $suspiciousAppCount potential threats. Tap cards for details.", "", ResultType.WARNING))
                }

                // --- PHASE 2: LOG SCAN ---
                try {
                    val process = Runtime.getRuntime().exec("logcat -d -t 300")
                    val reader = BufferedReader(InputStreamReader(process.inputStream))
                    var line: String?
                    var logThreatsFound = 0

                    while (reader.readLine().also { line = it } != null) {
                        val logText = line ?: ""
                        if (logText.contains("TaskInfo")) continue
                        if (logText.contains("InputMethodManager")) continue

                        if (logText.contains("location", ignoreCase = true)) {
                             logThreatsFound++
                             scanFindings.add(ScanResult("Location Accessed", "System log indicates GPS usage.", 
                                 "Plain English:\nAn app on your phone recently asked the GPS for your exact location. The raw log data is:\n\n$logText", ResultType.DANGER))
                        } else if (logText.contains("camera", ignoreCase = true)) {
                             logThreatsFound++
                             scanFindings.add(ScanResult("Camera Accessed", "System log indicates Camera usage.", 
                                 "Plain English:\nYour camera sensor was triggered recently. If you weren't taking a photo, an app might be looking at you.\n\nRaw Data: $logText", ResultType.DANGER))
                        } else if (logText.contains("mic", ignoreCase = true) || logText.contains("record", ignoreCase = true)) {
                             logThreatsFound++
                             scanFindings.add(ScanResult("Microphone Activity", "System log indicates Mic usage.", 
                                 "Plain English:\nYour microphone was active. This happens during calls or voice commands, but shouldn't happen silently.\n\nRaw Data: $logText", ResultType.DANGER))
                        }
                    }
                    
                    if (logThreatsFound == 0) {
                         scanFindings.add(ScanResult("System Logs Clear", "No active camera, mic, or location usage detected in recent logs.", "", ResultType.SAFE))
                    }

                } catch (e: Exception) {
                    scanFindings.add(ScanResult("Error", "Could not read system logs.", "Did you run the ADB permission command?", ResultType.DANGER))
                }

                runOnUiThread {
                    recyclerView.adapter = ScanAdapter(scanFindings)
                    btnScan.text = "Scan Again"
                    btnScan.isEnabled = true
                }
            }
        }
    }
}
