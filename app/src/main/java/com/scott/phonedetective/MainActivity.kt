package com.scott.phonedetective

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
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

                scanFindings.add(ScanResult("App Scan Started", "Analyzing ACTIVE permissions...", "", ResultType.SAFE))

                for (app in packages) {
                    if (app.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                        userAppCount++
                        val appName = pm.getApplicationLabel(app).toString()
                        val pkgName = app.packageName
                        
                        try {
                            // We need both Permissions AND the Flags (to see if they are granted)
                            val packageInfo = pm.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS)
                            val requestedPermissions = packageInfo.requestedPermissions
                            val requestedFlags = packageInfo.requestedPermissionsFlags // The "Receipts"

                            var hasLocation = false
                            var hasMic = false
                            var hasCamera = false
                            var hasContacts = false

                            if (requestedPermissions != null) {
                                for (i in requestedPermissions.indices) {
                                    // THE TRUTH CHECK:
                                    // Only count it if the system says it is currently GRANTED
                                    if ((requestedFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                                        val p = requestedPermissions[i]
                                        
                                        if (p.contains("ACCESS_FINE_LOCATION") || p.contains("ACCESS_COARSE_LOCATION")) hasLocation = true
                                        if (p.contains("RECORD_AUDIO")) hasMic = true
                                        if (p.contains("CAMERA")) hasCamera = true
                                        if (p.contains("READ_CONTACTS")) hasContacts = true
                                    }
                                }
                            }

                            // 2. RUN THE LOGIC TRAPS (Now only triggers on GRANTED permissions)
                            var reason = ""
                            var isSuspicious = false

                            // TRAP A: The "Over-Reaching" Flashlight
                            if (appName.contains("Flashlight", ignoreCase = true) || appName.contains("Torch", ignoreCase = true)) {
                                if (hasLocation) {
                                    reason = "Why is this suspicious?\nThis Flashlight app CURRENTLY has active access to your GPS Location. Go to Settings > Apps and turn this off immediately."
                                    isSuspicious = true
                                } else if (hasMic) {
                                    reason = "Why is this suspicious?\nThis Flashlight app CURRENTLY has active access to your Microphone."
                                    isSuspicious = true
                                }
                            }
                            
                            // TRAP B: The "Spying" Calculator/Game
                            else if (appName.contains("Calculator", ignoreCase = true) || appName.contains("Solitaire", ignoreCase = true)) {
                                if (hasMic || hasContacts) {
                                    reason = "Why is this suspicious?\nThis simple tool currently has permission to listen to you or read your contacts."
                                    isSuspicious = true
                                }
                            }

                            // TRAP C: The "Suspicious Name"
                            else if (appName.contains("Tracker", ignoreCase = true) || appName.contains("Spy", ignoreCase = true)) {
                                reason = "Why is this suspicious?\nThe name itself suggests surveillance capabilities."
                                isSuspicious = true
                            }

                            if (isSuspicious) {
                                suspiciousAppCount++
                                scanFindings.add(ScanResult("Suspicious Behavior: $appName", "Active risky permissions found.", reason, ResultType.WARNING))
                            }

                        } catch (e: Exception) {
                            // Skip if info unavailable
                        }
                    }
                }

                if (suspiciousAppCount == 0) {
                    scanFindings.add(ScanResult("App Analysis Clean", "Checked $userAppCount apps. No suspicious permissions are currently active.", "", ResultType.SAFE))
                }

                // --- PHASE 2: LOG SCAN (Unchanged) ---
                try {
                    val process = Runtime.getRuntime().exec("logcat -d -t 300")
                    val reader = BufferedReader(InputStreamReader(process.inputStream))
                    var line: String?
                    var logThreatsFound = 0

                    while (reader.readLine().also { line = it } != null) {
                        val logText = line ?: ""
                        if (logText.contains("TaskInfo") || logText.contains("InputMethod")) continue

                        if (logText.contains("location", ignoreCase = true)) {
                             logThreatsFound++
                             scanFindings.add(ScanResult("Active Location Use", "GPS accessed recently.", 
                                 "Plain English:\nSomething on your phone asked for your location just now.\n\nRaw Log: $logText", ResultType.DANGER))
                        } else if (logText.contains("camera", ignoreCase = true)) {
                             logThreatsFound++
                             scanFindings.add(ScanResult("Active Camera Use", "Camera sensor triggered.", 
                                 "Plain English:\nYour camera was active. If you aren't taking a photo, this is suspicious.\n\nRaw Log: $logText", ResultType.DANGER))
                        } else if (logText.contains("mic", ignoreCase = true) || logText.contains("record", ignoreCase = true)) {
                             logThreatsFound++
                             scanFindings.add(ScanResult("Active Mic Use", "Microphone triggered.", 
                                 "Plain English:\nYour microphone was active recently.\n\nRaw Log: $logText", ResultType.DANGER))
                        }
                    }
                    
                    if (logThreatsFound == 0) {
                         scanFindings.add(ScanResult("System Logs Clear", "No active spy activity detected in the last few minutes.", "", ResultType.SAFE))
                    }

                } catch (e: Exception) {
                    scanFindings.add(ScanResult("Error", "Could not read logs.", "", ResultType.DANGER))
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
