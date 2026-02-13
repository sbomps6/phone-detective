package com.scott.phonedetective

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Updated Data Class: Now includes a "description" and an "isExpanded" switch
data class ScanResult(
    val title: String, 
    val details: String, 
    val description: String, // New field for the Plain English reason
    val type: ResultType, 
    var isExpanded: Boolean = false
)

enum class ResultType { SAFE, WARNING, DANGER }

class ScanAdapter(private val results: List<ScanResult>) :
    RecyclerView.Adapter<ScanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
        val tvExplanation: TextView = view.findViewById(R.id.tvExplanation)
        val viewIndicator: View = view.findViewById(R.id.viewIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scan_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = results[position]
        
        holder.tvTitle.text = item.title
        holder.tvDetails.text = item.details
        
        // set the plain english text
        holder.tvExplanation.text = item.description

        // HANDLE VISIBILITY: If expanded, show the explanation. If not, hide it.
        holder.tvExplanation.visibility = if (item.isExpanded) View.VISIBLE else View.GONE

        // HANDLE CLICKS: Toggle the "isExpanded" switch when tapped
        holder.itemView.setOnClickListener {
            // Only allow expanding if there is actually a description to show
            if (item.description.isNotEmpty()) {
                item.isExpanded = !item.isExpanded
                notifyItemChanged(position) // Refresh just this one card
            }
        }

        // Color Coding
        when (item.type) {
            ResultType.DANGER -> {
                holder.viewIndicator.setBackgroundColor(Color.RED)
                holder.tvTitle.setTextColor(Color.RED)
            }
            ResultType.WARNING -> {
                holder.viewIndicator.setBackgroundColor(Color.rgb(255, 140, 0)) // Orange
                holder.tvTitle.setTextColor(Color.rgb(255, 140, 0))
            }
            ResultType.SAFE -> {
                holder.viewIndicator.setBackgroundColor(Color.parseColor("#4CAF50")) // Green
                holder.tvTitle.setTextColor(Color.parseColor("#388E3C"))
            }
        }
    }

    override fun getItemCount() = results.size
}
