package com.example.cermatitestapp.database.repositories

import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.MutableLiveData
import com.example.cermatitestapp.api.APIServices
import com.example.cermatitestapp.api.model.GithubUser
import com.example.cermatitestapp.api.model.ResponseUserSearch
import com.example.cermatitestapp.database.AppDatabase
import com.example.cermatitestapp.database.dao.UserSearchResultDao
import com.example.cermatitestapp.database.entities.UserSearchResult
import io.reactivex.Observable
import retrofit2.Response

class UserSearchResultRepository(
    private val context: Context,
    private val apiServices: APIServices
) {

    companion object {
        private fun convertUserSearchResultIntoGithubUser(userSearchResults: List<UserSearchResult>): List<GithubUser> {
            val githubUsers = arrayListOf<GithubUser>()
            userSearchResults.forEach {
                githubUsers.add(GithubUser(id = it.id, login = it.userName, avatar_url = it.avatarUrl, html_url = it.htmlUrl))
            }
            return githubUsers
        }

        private fun convertGithubUserIntoUserSearchResult(githubUsers: List<GithubUser>): List<UserSearchResult> {
            val userSearchResults = arrayListOf<UserSearchResult>()
            githubUsers.forEach {
                val usr = UserSearchResult()
                usr.id = it.id
                usr.userName = it.login
                usr.avatarUrl = it.avatar_url
                usr.htmlUrl = it.html_url
                userSearchResults.add(usr)
            }
            return userSearchResults
        }
    }

    private val userSearchResultDao: UserSearchResultDao
    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val userSearchResults = MutableLiveData<List<GithubUser>>()

    init {
        val db = AppDatabase.getDatabase(context)
        userSearchResultDao = db!!.userSearchResultDao()
    }

    // You must call this on a non-UI thread or your app will crash.
    // Like this, Room ensures that you're not doing any long running operations on the main
    // thread, blocking the UI.
    fun insert(vararg userSearchResult: UserSearchResult) {
        InsertAsyncTask(userSearchResultDao).execute(*userSearchResult)
    }

    private class InsertAsyncTask internal constructor(private val mAsyncTaskDao: UserSearchResultDao) :
        AsyncTask<UserSearchResult, Void, Void>() {

        override fun doInBackground(vararg params: UserSearchResult): Void? {
            mAsyncTaskDao.insert(*params)
            return null
        }
    }

    fun deleteAll() {
        DeleteAllAsyncTask(userSearchResultDao).execute()
    }

    private class DeleteAllAsyncTask internal constructor(private val mAsyncTaskDao: UserSearchResultDao) :
        AsyncTask<Void, Void, Unit>() {

        override fun doInBackground(vararg params: Void?) {
            mAsyncTaskDao.deleteAll()
        }
    }

    fun findUserByName(keyword: String) {
        FindUserByNameAsyncTask(userSearchResultDao, userSearchResults).execute(keyword)
    }

    fun fetchRemoteData(page: Int, keyword: String): Observable<Response<ResponseUserSearch>> {
        val sanitizedKeyword = keyword.trim().replace(" ", "+")
        val keywordStringQuery = "$sanitizedKeyword%20in%3Alogin%20OR%20$sanitizedKeyword%20in%3Aname"
        return apiServices.getUsers(keywordStringQuery, page)
    }

    fun convertAndInsert(items: List<GithubUser>?) {
        items?.let {
            insert(*convertGithubUserIntoUserSearchResult(it).toTypedArray())
        }
    }

    private class FindUserByNameAsyncTask internal constructor(
        private val mAsyncTaskDao: UserSearchResultDao,
        private val resultLiveData: MutableLiveData<List<GithubUser>>
    ) :
        AsyncTask<String, Void, List<UserSearchResult>>() {

        override fun doInBackground(vararg params: String): List<UserSearchResult>? {
            return mAsyncTaskDao.findUserByName(params[0])
        }

        override fun onPostExecute(result: List<UserSearchResult>?) {
            super.onPostExecute(result)
            result?.let {
                resultLiveData.postValue(convertUserSearchResultIntoGithubUser(it))
            }
        }
    }
}