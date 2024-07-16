package com.penguinstudios.tradeguardian.ui.createwallet.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.penguinstudios.tradeguardian.R

class AvailableWordsAdapter(
    private val wordList: List<String>,
    private val selectedWordsMap: Map<Int, String>,
    private val callback: Callback
) : RecyclerView.Adapter<AvailableWordsAdapter.ViewHolder>() {

    interface Callback {
        fun onAvailableWordsClick(adapterPosition: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_mnemonic_item_three, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvWord.text = wordList[position]

        if (selectedWordsMap.containsValue(wordList[position])) {
            holder.tvWord.setBackgroundResource(R.drawable.bgr_available_word_already_selected)
            holder.tvWord.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.default_text_color_50_opacity
                )
            )
        } else {
            holder.tvWord.setBackgroundResource(R.drawable.bgr_view_mnemonic)
            holder.tvWord.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.white
                )
            )
        }
    }

    override fun getItemCount(): Int = wordList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val tvWord: TextView = itemView.findViewById(R.id.tv_word)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            callback.onAvailableWordsClick(adapterPosition)
        }
    }
}
