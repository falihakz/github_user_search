package com.example.cermatitestapp.database.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_search_results")
class UserSearchResult {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    @NonNull
    var id: Int? = null

    @ColumnInfo(name = "user_name")
    @NonNull
    var userName: String? = null

    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String? = null

    @ColumnInfo(name = "html_url")
    var htmlUrl: String? = null
}