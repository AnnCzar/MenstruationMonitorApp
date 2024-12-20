package com.example.project

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Adapter for displaying chat messages in a RecyclerView.
 * This adapter handles the display of chat messages, distinguishing between messages sent by the user
 * and messages received from others, and formats the timestamp.
 *
 * @param mContext The context used to access resources and system services.
 * @param mChatList The list of chat messages to be displayed in the RecyclerView.
 */
class ChatAdapter(
    private val mContext: Context,
    private val mChatList: List<Chat>
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    /**
     * ViewHolder class that holds references to the UI elements of each message item.
     * The ViewHolder is used to avoid repeated calls to `findViewById` for each item.
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textMessage: TextView = itemView.findViewById(R.id.message_text)
        val textSeen: TextView? = itemView.findViewById(R.id.text_see)
        val textTimestamp: TextView = itemView.findViewById(R.id.text_timestamp)
    }

    /**
     * Inflates the correct layout for each message item based on whether the message is sent by the user
     * or received from others.
     *
     * @param parent The parent view group to which the new view will be added.
     * @param viewType The type of the view to be created (sent or received message).
     * @return A ViewHolder containing the inflated view for the message.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = if (viewType == MESSAGE_TYPE_RIGHT) {
            R.layout.message_item_right
        } else {
            R.layout.message_item_left
        }
        Log.d("ChatAdapter", "Inflating layout: $layoutId")
        val view = LayoutInflater.from(mContext).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    /**
     * Returns the total number of messages in the chat list.
     *
     * @return The size of the chat list.
     */
    override fun getItemCount(): Int = mChatList.size

    /**
     * Binds data to the views for a specific message at the given position.
     * This includes setting the message text, visibility of the "seen" status, and formatting the timestamp.
     *
     * @param holder The ViewHolder containing the views to be updated.
     * @param position The position of the item in the chat list.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = mChatList[position]

//        // Ustawienie treści wiadomości
        holder.textMessage.text = chat.message

        if (position == mChatList.size - 1) {
            holder.textSeen?.visibility = View.VISIBLE
            holder.textSeen?.text = if (chat.isseen) "Seen" else "Delivered"
        } else {
            holder.textSeen?.visibility = View.GONE
        }
        val timestamp = chat.timestamp
        val formattedDate = formatTimestamp(timestamp)
        holder.textTimestamp.text = formattedDate
    }

    /**
     * Returns the view type for the message, determining if it is a sent message or a received message.
     *
     * @param position The position of the item in the chat list.
     * @return The view type (either sent or received message).
     */
    override fun getItemViewType(position: Int): Int {
        return if (mChatList[position].sender == firebaseUser?.uid) {
            MESSAGE_TYPE_RIGHT
        } else {
            MESSAGE_TYPE_LEFT
        }
    }

    /**
     * Formats the timestamp into a readable date and time string.
     *
     * @param timestamp The timestamp to be formatted.
     * @return A formatted string representing the date and time.
     */
    // Funkcja formatująca timestamp na datę i godzinę
    private fun formatTimestamp(timestamp: Long): String {
        val dateFormat = android.text.format.DateFormat.getDateFormat(mContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(mContext)
        val date = java.util.Date(timestamp)
        return "${dateFormat.format(date)} ${timeFormat.format(date)}"
    }

    companion object {
        private const val MESSAGE_TYPE_RIGHT = 1
        private const val MESSAGE_TYPE_LEFT = 0
    }
}
