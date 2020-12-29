package com.example.slickypasscode.network

import com.example.slickypasscode.network.model.ApiCallResult
import java.lang.Exception

interface ISlickyMockServerInterface {

    suspend fun getPassCodeInternals(): ApiCallResult

    suspend fun validatePassword(typedPassCode : String) : ApiCallResult

}