package com.example.cermatitestapp.api

import com.example.cermatitestapp.api.model.ResponseUserSearch
import com.example.cermatitestapp.common.Const.Companion.GET_USERS
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIServices {

    @GET(GET_USERS)
    fun getUsers(
        @Query("q", encoded = true) q: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10
    ): Observable<Response<ResponseUserSearch>>
}