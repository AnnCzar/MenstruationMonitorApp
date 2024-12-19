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

class ChatAdapter(
    private val mContext: Context,
    private val mChatList: List<Chat>
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textMessage: TextView = itemView.findViewById(R.id.message_text)
        val textSeen: TextView? = itemView.findViewById(R.id.text_see)
        val textTimestamp: TextView = itemView.findViewById(R.id.text_timestamp)
    }

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

    override fun getItemCount(): Int = mChatList.size

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

    override fun getItemViewType(position: Int): Int {
        return if (mChatList[position].sender == firebaseUser?.uid) {
            MESSAGE_TYPE_RIGHT
        } else {
            MESSAGE_TYPE_LEFT
        }
    }

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
