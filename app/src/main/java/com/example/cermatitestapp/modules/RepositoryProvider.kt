package com.example.cermatitestapp.modules

import com.example.cermatitestapp.database.repositories.UserSearchResultRepository
import org.koin.dsl.module

val userSearchResultRepository = module {
    single { UserSearchResultRepository(get(), get()) }
}