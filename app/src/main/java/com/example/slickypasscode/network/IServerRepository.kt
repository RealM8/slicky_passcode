package com.example.slickypasscode.network

import com.example.slickypasscode.network.model.ApiCallResult

interface IServerRepository {

    suspend fun getPassCodeInternals() : ApiCallResult

    suspend fun validatePassword(typedPassCode : String) : ApiCallResult

}