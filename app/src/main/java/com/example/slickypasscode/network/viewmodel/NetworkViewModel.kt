package com.example.slickypasscode.network.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.slickypasscode.R
import com.example.slickypasscode.SlickyPasscodeApp
import com.example.slickypasscode.archcomponents.Event
import com.example.slickypasscode.network.IServerRepository
import com.example.slickypasscode.network.model.ApiCallResult
import com.example.slickypasscode.network.model.NetworkResult
import kotlinx.coroutines.launch

class NetworkViewModel(private var serverRepository : IServerRepository) : ViewModel() {

    private val networkViewState = MutableLiveData<ViewState>()
    fun getNetworkViewState () : MutableLiveData<ViewState> {return networkViewState}
    private val passCodeEnteringProgressLiveData = MutableLiveData<Int>()
    fun getPassCodeEnteringProgressLiveData () : MutableLiveData<Int> {return passCodeEnteringProgressLiveData}

    private var typedPassCode = ""

    init {
        getPassCodeInternals()
    }

    //Repository callers
    fun getPassCodeInternals() {
        viewModelScope.launch {
            serverRepository.getPassCodeInternals().let { apiCallResult ->
                processApiCallRequest(apiCallResult) {
                    if (apiCallResult.listIntValue != null) {
                        networkViewState.value = ViewState.Success(apiCallResult.listIntValue)
                    } else {
                        networkViewState.value = ViewState.Error(Event(R.string.network_viewmodel_error_empty_data_loaded_message), ErrorType.EMPTY_SETUP_DATA)
                    }
                }
            }
        }
        networkViewState.value = ViewState.Loading
    }

    private fun validatePassword() {
        viewModelScope.launch {
            serverRepository.validatePassword(typedPassCode).let { apiCallResult ->
                processApiCallRequest(apiCallResult) {
                    if (apiCallResult.booleanValue != null) {
                        if (apiCallResult.booleanValue) {
                            getPassCodeInternals()
                        }
                        else
                        {
                            onCancelTypedPassCodeClick()
                            networkViewState.value = ViewState.Error(Event(R.string.main_activity_wrong_password_toast), ErrorType.WRONG_PASSWORD)
                        }
                    } else {
                        networkViewState.value = ViewState.Error(Event(R.string.network_viewmodel_error_empty_data_loaded_message), ErrorType.EMPTY_SETUP_DATA)
                    }
                }
            }
        }
        networkViewState.value = ViewState.Loading
    }


    private fun processApiCallRequest (apiCallResult: ApiCallResult, actionOnSuccessfulApiCall : () -> Unit) {
        when (apiCallResult.code) {
            NetworkResult.SUCCESS -> {
                actionOnSuccessfulApiCall()
            }
            NetworkResult.FAILURE -> {
                networkViewState.value =
                    ViewState.Error(Event(R.string.network_viewmodel_error_during_loading_data_dialog_message), ErrorType.NETWORK_FAILURE)
            }
        }
    }

    //Button onclicks
    fun onNumberClick(number : Int) {
        typedPassCode += number
        passCodeEnteringProgressLiveData.value = typedPassCode.length
        if (typedPassCode.length >= 4) {
            viewModelScope.launch {
                validatePassword()
            }
        }
    }

    //Also used by other parts of this viewmodel to erase entered passcode
    fun onCancelTypedPassCodeClick() {
        typedPassCode = ""
        passCodeEnteringProgressLiveData.value = typedPassCode.length
    }

    fun onDeleteClick() {
        if (typedPassCode.isNotEmpty()) {
            typedPassCode = typedPassCode.removeRange(typedPassCode.length - 1, typedPassCode.length)
            passCodeEnteringProgressLiveData.value = typedPassCode.length
        }
    }

    sealed class ViewState() {
        object Loading: ViewState()
        data class Success(val passCodeScreenSetup: List<Int>): ViewState()
        data class Error(val errorMessageStringId: Event<Int>, val errorType : ErrorType): ViewState()
    }
}