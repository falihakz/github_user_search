package com.example.cermatitestapp.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.cermatitestapp.R
import com.example.cermatitestapp.api.model.GithubUser
import kotlinx.android.synthetic.main.item_user_list.view.*

class UserListAdapter(private val glide: RequestManager) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    var userList = mutableListOf<GithubUser>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_user_list,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val githubUser: GithubUser = userList[position]

        glide.load(githubUser.avatar_url).into(holder.ivAvatar)
        holder.tvUserName.text = githubUser.login
    }

    fun setItems(items: List<GithubUser>?) {
        items?.let {
            userList = it.toMutableList()
            notifyDataSetChanged()
        }
    }

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAvatar: ImageView = itemView.iv_avatar
        val tvUserName: TextView = itemView.tv_user_name
    }

}
