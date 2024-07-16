package com.penguinstudios.tradeguardian.ui.createwallet.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.penguinstudios.tradeguardian.R

class ViewMnemonicAdapter(
    private val wordList: List<String>
) : RecyclerView.Adapter<ViewMnemonicAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_mnemonic_item_one, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val number = "${position + 1}."
        holder.tvNumber.text = number
        holder.tvWord.text = wordList[position]
    }

    override fun getItemCount(): Int {
        return wordList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWord: TextView = itemView.findViewById(R.id.tv_word)
        val tvNumber: TextView = itemView.findViewById(R.id.tv_word_number)
    }
}
