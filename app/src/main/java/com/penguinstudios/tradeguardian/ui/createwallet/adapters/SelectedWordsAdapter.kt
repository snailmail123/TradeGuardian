package com.penguinstudios.tradeguardian.ui.createwallet.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.penguinstudios.tradeguardian.R

class SelectedWordsAdapter(
    private val selectedWordsMap: Map<Int, String>,
    private val numEmptySpacesToDisplay: Int,
    private val callback: Callback
) : RecyclerView.Adapter<SelectedWordsAdapter.ViewHolder>() {

    interface Callback {
        fun onSelectedWordsClick(adapterPosition: Int)
    }

    private var nextItemClickTarget: Int = 0

    fun setNextTarget(adapterPositionToUpdate: Int) {
        nextItemClickTarget = adapterPositionToUpdate
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_mnemonic_item_one, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val number = "${position + 1}."
        holder.tvNumber.text = number

        val word = selectedWordsMap[position]
        if (word == null) {
            holder.tvWord.text = ""
        } else {
            holder.tvWord.text = word
        }

        if (position == nextItemClickTarget) {
            holder.tvWord.setBackgroundResource(R.drawable.custom_mnemonic_ripple_click_2)
        } else {
            holder.tvWord.setBackgroundResource(R.drawable.bgr_view_mnemonic)
        }
    }

    override fun getItemCount(): Int = numEmptySpacesToDisplay

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val tvWord: TextView = itemView.findViewById(R.id.tv_word)
        val tvNumber: TextView = itemView.findViewById(R.id.tv_word_number)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            callback.onSelectedWordsClick(adapterPosition)
        }
    }
}
