package com.example.project

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatUserAdapter(
    private val usersNames: List<ChatUser>,
    private val onEditClick: (ChatUser) -> Unit,
    private val onDeleteClick: (ChatUser) -> Unit
) : RecyclerView.Adapter<ChatUserAdapter.ChatUserViewHolder>() {

    class ChatUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userLogin: TextView = itemView.findViewById(R.id.textViewUserLogin)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.medicine_item_modify, parent, false)
        return ChatUserViewHolder(view)
    }



    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ChatUserViewHolder, position: Int) {
        val userLogin = usersNames[position]
        holder.userLogin.text = userLogin.login
    }
    override fun getItemCount(): Int = usersNames.size
}




