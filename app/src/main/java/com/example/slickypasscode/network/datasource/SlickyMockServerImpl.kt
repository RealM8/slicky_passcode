package com.example.slickypasscode.network

import android.util.Log
import com.example.slickypasscode.R
import com.example.slickypasscode.archcomponents.Event
import com.example.slickypasscode.network.ISlickyMockServerInterface.*
import com.example.slickypasscode.network.model.ApiCallResult
import com.example.slickypasscode.network.model.NetworkResult
import com.example.slickypasscode.network.viewmodel.ErrorType
import com.example.slickypasscode.network.viewmodel.NetworkViewModel
import kotlin.random.Random

//Mock server for architecture demonstration purposes.
//All logins/passwords and tokens validations must be performed on a remote server.
class SlickyMockServerImpl : ISlickyMockServerInterface {

    private val passCodeDigits = 0..9
    private val possibleCodes = arrayOf("1234", "4321")
    private lateinit var correctPassCode : String

    override suspend fun getPassCodeInternals(): ApiCallResult {
        //Imitates long running network request
        Thread.sleep(3000)

        //Imitates network failure
        if (Random.nextInt(2) == 0) {
            correctPassCode = possibleCodes[Random.nextInt(2)]
            return ApiCallResult(NetworkResult.SUCCESS, listIntValue = passCodeDigits.shuffled())
        } else {
            return ApiCallResult(NetworkResult.FAILURE)
        }
    }

    override suspend fun validatePassword(typedPassCode: String): ApiCallResult {
        if (correctPassCode == typedPassCode)
            return ApiCallResult(NetworkResult.SUCCESS, booleanValue = true)

        return ApiCallResult(NetworkResult.SUCCESS, booleanValue = false)
    }

}