package com.example.project

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatDoctorAdapter(
    private val usersNames: List<ChatUser>,
    private val onUserClick: (ChatUser) -> Unit
) : RecyclerView.Adapter<ChatDoctorAdapter.ChatUserViewHolder>() {

    // ViewHolder definiuje widoki elementu listy
    class ChatUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userLogin: TextView = itemView.findViewById(R.id.textViewUserLogin)
    }

    // Tworzenie nowego ViewHoldera
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_list_item, parent, false)
        return ChatUserViewHolder(view)
    }

    // Powiązanie danych z widokami
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ChatUserViewHolder, position: Int) {
        val user = usersNames[position]
        holder.userLogin.text = user.login

        // Obsługa kliknięcia na element listy
        holder.itemView.setOnClickListener {
            onUserClick(user)
        }
    }

    // Zwracanie liczby elementów w liście
    override fun getItemCount(): Int = usersNames.size
}
