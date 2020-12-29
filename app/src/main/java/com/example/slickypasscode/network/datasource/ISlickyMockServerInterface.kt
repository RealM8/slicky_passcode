package com.example.slickypasscode.network

import com.example.slickypasscode.network.model.ApiCallResult
import java.lang.Exception

interface ISlickyMockServerInterface {

    fun getPassCodeInternals(): ApiCallResult

    fun validatePassword(typedPassCode : String) : ApiCallResult

}