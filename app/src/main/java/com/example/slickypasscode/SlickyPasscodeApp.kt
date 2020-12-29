package com.example.slickypasscode

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.example.slickypasscode.network.ISlickyMockServerInterface
import com.example.slickypasscode.network.IServerRepository
import com.example.slickypasscode.network.ServerRepositoryImpl
import com.example.slickypasscode.network.SlickyMockServerImpl

class SlickyPasscodeApp : MultiDexApplication() {

    companion object {

        lateinit var instance: SlickyPasscodeApp
            private set

    }

    val serverRepository : IServerRepository by lazy {ServerRepositoryImpl(slickyMockServer)}
    val slickyMockServer : ISlickyMockServerInterface by lazy {SlickyMockServerImpl()}

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}