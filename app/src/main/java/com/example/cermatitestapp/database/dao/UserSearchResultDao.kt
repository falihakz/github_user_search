package com.example.cermatitestapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cermatitestapp.database.entities.UserSearchResult

@Dao
interface UserSearchResultDao {
    @Query("SELECT * FROM user_search_results")
    fun getAll(): LiveData<List<UserSearchResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg userSearchResult: UserSearchResult)

    @Delete
    fun delete(userSearchResult: UserSearchResult): Int

    @Query("DELETE FROM user_search_results WHERE id = :id")
    fun delete(id: String): Int

    @Query("DELETE FROM user_search_results")
    fun deleteAll()

    @Query("SELECT * FROM user_search_results WHERE user_name LIKE '%' || :keyword || '%'")
    fun findUserByName(keyword: String): List<UserSearchResult>
}