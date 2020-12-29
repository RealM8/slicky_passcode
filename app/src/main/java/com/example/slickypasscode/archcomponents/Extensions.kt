package com.example.slickypasscode.archcomponents

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider

inline fun <reified VM : ViewModel> AppCompatActivity.provideViewModel(crossinline vmPromise: () -> VM): Lazy<VM> {
    val factoryPromise = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return vmPromise() as T
            }
        }
    }

    return ViewModelLazy(VM::class, { viewModelStore }, factoryPromise)
}