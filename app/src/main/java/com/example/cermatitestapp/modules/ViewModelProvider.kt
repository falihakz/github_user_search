package com.example.cermatitestapp.modules

import com.example.cermatitestapp.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainViewModel = module {
    viewModel { MainViewModel(get()) }
}