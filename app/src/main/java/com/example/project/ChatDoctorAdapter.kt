package com.example.project

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter for displaying a list of chat users in a RecyclerView.
 * The adapter binds a list of `ChatUser` objects to the UI elements in each item of the list.
 * When a user item is clicked, the provided callback is invoked.
 *
 * @param usersNames List of `ChatUser` objects representing users in the chat.
 * @param onUserClick Callback function that gets triggered when a user is clicked. The clicked `ChatUser` is passed as an argument.
 */
class ChatDoctorAdapter(
    private val usersNames: List<ChatUser>,
    private val onUserClick: (ChatUser) -> Unit
) : RecyclerView.Adapter<ChatDoctorAdapter.ChatUserViewHolder>() {

    /**
     * ViewHolder for each item in the RecyclerView.
     * Holds the views that will display the user data.
     */
    class ChatUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userLogin: TextView = itemView.findViewById(R.id.textViewUserLogin)
    }

    /**
     * Called to create a new ViewHolder for an item in the list.
     * Inflates the layout for the item and returns a ViewHolder.
     *
     * @param parent The parent ViewGroup that this view will be attached to.
     * @param viewType The view type of the new item (not used in this case).
     * @return A new `ChatUserViewHolder` for the item view.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_list_item, parent, false)
        return ChatUserViewHolder(view)
    }

    /**
     * Binds data from the `usersNames` list to the ViewHolder for a specific position in the list.
     * Sets the user's login (name) into the TextView and sets up a click listener for the item.
     *
     * @param holder The ViewHolder that holds the views for the current item.
     * @param position The position in the list for which data should be bound.
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ChatUserViewHolder, position: Int) {
        val user = usersNames[position]
        holder.userLogin.text = user.login

        // Obsługa kliknięcia na element listy
        holder.itemView.setOnClickListener {
            onUserClick(user)
        }
    }

    /**
     * Returns the total number of items in the list.
     *
     * @return The size of the `usersNames` list.
     */
    override fun getItemCount(): Int = usersNames.size
}
