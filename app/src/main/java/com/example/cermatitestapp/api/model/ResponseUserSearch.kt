package com.example.cermatitestapp.api.model

data class ResponseUserSearch(
    val incomplete_results: Boolean,
    val items: List<GithubUser>,
    val total_count: Int
)