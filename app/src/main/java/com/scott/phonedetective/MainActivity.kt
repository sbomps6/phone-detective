package com.scott.phonedetective

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnScan = findViewById<Button>(R.id.btnScan)
        val tvLog = findViewById<TextView>(R.id.tvLog)
        val scrollView = findViewById<ScrollView>(R.id.scrollView)

        btnScan.setOnClickListener {
            // Disable the button so they don't click it twice
            btnScan.isEnabled = false
            btnScan.text = "Scanning..."
            tvLog.text = "Starting Detective Scan...\n"

            // Run the heavy work in a background thread
            thread {
                val results = StringBuilder()

                // STEP 1: Scan Installed Apps
                results.append("\n--- CHECKING INSTALLED APPS ---\n")
                val pm = packageManager
                val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

                for (app in packages) {
                    // Look for apps that are NOT system apps (User installed)
                    if (app.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                        val appName = pm.getApplicationLabel(app).toString()
                        results.append("Found User App: $appName (${app.packageName})\n")
                    }
                }

                // STEP 2: Check System Logs (Requires the ADB Permission)
                results.append("\n--- CHECKING SYSTEM LOGS ---\n")
                try {
                    // This command grabs the last 100 lines of the system log
                    val process = Runtime.getRuntime().exec("logcat -d -t 100")
                    val reader = BufferedReader(InputStreamReader(process.inputStream))
                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        // Filter for "scary" keywords
                        if (line!!.contains("location") || line!!.contains("camera") || line!!.contains("mic")) {
                            results.append("SUSPICIOUS LOG: $line\n")
                        }
                    }
                } catch (e: Exception) {
                    results.append("Error reading logs: ${e.message}\n")
                    results.append("Did you run the ADB permission command?\n")
                }

                // Update the UI back on the Main Thread
                runOnUiThread {
                    tvLog.append(results.toString())
                    tvLog.append("\n--- SCAN COMPLETE ---\n")
                    
                    // Auto-scroll to the bottom
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    
                    // Re-enable the button
                    btnScan.text = "Run Scan"
                    btnScan.isEnabled = true
                }
            }
        }
    }
}
