package com.example.chrysoum_oblig2

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

class PartyAdapter(val partyList: MutableList<AlpacaParty>): RecyclerView.Adapter<PartyAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var colourView: View
        var itemTextView1: TextView
        var itemImageView:CircleImageView
        var itemTextView2: TextView
        var itemTextView3: TextView

        init {
            colourView = itemView.findViewById(R.id.view)
            itemTextView1 = itemView.findViewById(R.id.textView1)
            itemImageView = itemView.findViewById(R.id.imageView)
            itemTextView2 = itemView.findViewById(R.id.textView2)
            itemTextView3 = itemView.findViewById(R.id.textView3)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.element, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return partyList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = partyList[position]
        val currentUrl = current.img
        holder.colourView.setBackgroundColor(Color.parseColor(current.color))
        holder.itemTextView1.text = current.name
        GlideApp.with(holder.itemImageView.context)
            .load(currentUrl)
            .into(holder.itemImageView)

        holder.itemTextView2.text = "Leader: ".plus(current.leader)

        val result: String = current.getVotes().toString()
        val resultAverage: String = current.getAverage().toString()
        val resultText = "Votes: ".plus(result).plus(" - ").plus(resultAverage).plus(" %")
        holder.itemTextView3.text = resultText
    }

}

@GlideModule
class GlideAppName : AppGlideModule()


