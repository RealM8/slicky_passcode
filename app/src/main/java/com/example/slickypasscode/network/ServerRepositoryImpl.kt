package com.example.slickypasscode.network

import com.example.slickypasscode.SlickyPasscodeApp
import com.example.slickypasscode.network.model.ApiCallResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServerRepositoryImpl (private val slickyMockServer : ISlickyMockServerInterface) : IServerRepository {

    override suspend fun getPassCodeInternals(): ApiCallResult = withContext(Dispatchers.IO) {
        return@withContext slickyMockServer.getPassCodeInternals()
    }

    override suspend fun validatePassword(typedPassCode: String): ApiCallResult = withContext(Dispatchers.IO) {
        return@withContext slickyMockServer.validatePassword(typedPassCode)
    }
}