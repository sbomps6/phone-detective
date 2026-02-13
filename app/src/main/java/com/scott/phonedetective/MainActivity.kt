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
                // --- PHASE 1: PERMISSION & APP SCAN ---
                val pm = packageManager
                val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                var userAppCount = 0
                var suspiciousAppCount = 0

                scanFindings.add(ScanResult("App Scan Started", "Analyzing app permissions and behaviors...", "", ResultType.SAFE))

                for (app in packages) {
                    if (app.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                        userAppCount++
                        val appName = pm.getApplicationLabel(app).toString()
                        val pkgName = app.packageName
                        
                        // 1. Get the Permissions for this app
                        // We use a try-catch because sometimes apps hide their info
                        try {
                            val packageInfo = pm.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS)
                            val requestedPermissions = packageInfo.requestedPermissions
                            
                            var hasLocation = false
                            var hasMic = false
                            var hasCamera = false
                            var hasContacts = false
                            var riskyList = ArrayList<String>()

                            if (requestedPermissions != null) {
                                for (p in requestedPermissions) {
                                    if (p.contains("ACCESS_FINE_LOCATION")) { hasLocation = true; riskyList.add("GPS Location") }
                                    if (p.contains("RECORD_AUDIO")) { hasMic = true; riskyList.add("Microphone") }
                                    if (p.contains("CAMERA")) { hasCamera = true; riskyList.add("Camera") }
                                    if (p.contains("READ_CONTACTS")) { hasContacts = true; riskyList.add("Read Contacts") }
                                }
                            }

                            // 2. RUN THE LOGIC TRAPS
                            var reason = ""
                            var isSuspicious = false

                            // TRAP A: The "Over-Reaching" Flashlight
                            if (appName.contains("Flashlight", ignoreCase = true) || appName.contains("Torch", ignoreCase = true)) {
                                if (hasLocation) {
                                    reason = "Why is this suspicious?\nThis is a flashlight app, but it has permission to track your GPS Location. Flashlights do not need to know where you are."
                                    isSuspicious = true
                                } else if (hasMic) {
                                    reason = "Why is this suspicious?\nThis flashlight app has permission to record audio. This is highly abnormal."
                                    isSuspicious = true
                                }
                            }
                            
                            // TRAP B: The "Spying" Calculator/Game
                            else if (appName.contains("Calculator", ignoreCase = true) || appName.contains("Solitaire", ignoreCase = true)) {
                                if (hasMic || hasContacts) {
                                    reason = "Why is this suspicious?\nA simple tool like a Calculator should not need access to your Microphone or Contacts list."
                                    isSuspicious = true
                                }
                            }

                            // TRAP C: The "Suspicious Name" (Our old logic, still good!)
                            else if (appName.contains("Tracker", ignoreCase = true) || appName.contains("Spy", ignoreCase = true)) {
                                reason = "Why is this suspicious?\nThe name itself suggests surveillance capabilities."
                                isSuspicious = true
                            }

                            // If we caught them in a trap, Add to list
                            if (isSuspicious) {
                                suspiciousAppCount++
                                scanFindings.add(ScanResult("Suspicious Behavior: $appName", "Found risky permissions.", reason, ResultType.WARNING))
                            } 
                            // If it's just a normal app but has LOTS of power (e.g. Facebook/TikTok), maybe just warn the user?
                            // (Optional: You can uncomment this to see ALL apps with mic access)
                            
                            else if (hasMic || hasCamera) {
                                scanFindings.add(ScanResult("High Privilege App: $appName", "Has Camera/Mic access.", 
                                    "This app isn't necessarily malicious, but it HAS permission to use: ${riskyList.joinToString(", ")}. Verify you trust it.", ResultType.WARNING))
                            }
                            

                        } catch (e: Exception) {
                            // Sometime older Android versions fail to fetch permissions for specific system apps
                        }
                    }
                }

                if (suspiciousAppCount == 0) {
                    scanFindings.add(ScanResult("App Analysis Clean", "Checked permissions for $userAppCount apps. No behavior mismatches found.", "", ResultType.SAFE))
                }

                // --- PHASE 2: LOG SCAN (Same as before) ---
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
