package com.example.project

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatDoctorAdapter(
    private val usersNames: List<ChatUser>,
) : RecyclerView.Adapter<ChatDoctorAdapter.ChatUserViewHolder>() {

    class ChatUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userLogin: TextView = itemView.findViewById(R.id.textViewUserLogin)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_list_item, parent, false)
        return ChatUserViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ChatUserViewHolder, position: Int) {
        val userLogin = usersNames[position]
        holder.userLogin.text = userLogin.login
        println("Binding login: ${userLogin.login}") // Debugging
    }
    override fun getItemCount(): Int = usersNames.size
}




