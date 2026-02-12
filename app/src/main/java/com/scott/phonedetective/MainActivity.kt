package com.scott.phonedetective
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
        }
        val btnScan = Button(this).apply { text = "Scan for Detective Clues" }
        val txtLogs = TextView(this).apply { textSize = 14f }
        layout.addView(btnScan); layout.addView(txtLogs)
        setContentView(layout)
        btnScan.setOnClickListener {
            txtLogs.text = "Investigating logs..."
            txtLogs.text = runLogcatScan()
        }
    }
    private fun runLogcatScan(): String {
        val result = StringBuilder()
        try {
            val process = Runtime.getRuntime().exec("logcat -d *:E")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line!!.contains("FATAL") || line!!.contains("ANR")) {
                    result.append("🔍 DETECTED: ").append(line).append("\n\n")
                }
            }
        } catch (e: Exception) { return "Error: ${e.message}" }
        return if (result.isEmpty()) "No suspicious activity found." else result.toString()
    }
}
