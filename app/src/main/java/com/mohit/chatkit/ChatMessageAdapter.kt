package com.mohit.chatkit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pusher.chatkit.messages.multipart.Message
import com.pusher.chatkit.messages.multipart.Payload
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatMessageAdapter : RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>() {

    private var list = ArrayList<Message>()
    private lateinit var view: View

    fun addMessage(e: Message) {
        list.add(e)
    }

    fun addAllMessage(e: List<Message>) {
        list.addAll(e)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(parent!!.context)
            .inflate(R.layout.adapter_msg_conversion, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder!!.userName.text = list[position].sender.name
        val inlineMessage: Payload.Inline = list[position].parts[0].payload as Payload.Inline
        holder.message.text = inlineMessage.content
        val format = SimpleDateFormat("hh:mm dd/MM/yyyy", Locale.ENGLISH)
        holder.dateTime.text = format.format(list[position].createdAt)

        /*if (list[position].sender.id == AppController.currentUser.id) {
            holder.linearLayout.gravity = Gravity.END
        } else {
            holder.linearLayout.gravity = Gravity.START
        }*/

        val lp = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (list[position].sender.id == AppController.currentUser.id) {
            lp.setMargins(20, 0, 0, 10)
            holder.linearLayout.background =
                view.context.getResources().getDrawable(R.drawable.outgoing_msg)
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        } else {
            lp.setMargins(0, 0, 20, 10)
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            holder.linearLayout.background =
                view.context.getResources().getDrawable(R.drawable.incoming_msg)
        }
        holder.linearLayout.layoutParams = lp
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var linearLayout: LinearLayout = itemView!!.findViewById(R.id.linearLayout)
        var userName: TextView = itemView!!.findViewById(R.id.userName)
        var message: TextView = itemView!!.findViewById(R.id.message)
        var dateTime: TextView = itemView!!.findViewById(R.id.dateTime)
    }
}
