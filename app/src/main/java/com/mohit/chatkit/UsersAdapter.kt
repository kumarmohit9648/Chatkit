package com.mohit.chatkit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pusher.chatkit.users.User

class UsersAdapter : RecyclerView.Adapter<UsersAdapter.UsersHolder>() {

    lateinit var view: View
    var list = ArrayList<User>()

    fun addUser(user: User) {
        list.add(user)
    }

    fun addAllUser(user: List<User>) {
        list.addAll(user)
    }

    fun getUsers(): List<User> {
        return list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_users, parent, false)
        return UsersHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: UsersHolder, position: Int) {
        holder.userName.text = list[position].name
    }

    inner class UsersHolder(view: View) : RecyclerView.ViewHolder(view) {
        var userImage: ImageView = view.findViewById(R.id.userImage)
        var userName: TextView = view.findViewById(R.id.userName)
    }
}