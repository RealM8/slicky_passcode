package com.example.slickypasscode.ui

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import com.example.slickypasscode.R
import com.example.slickypasscode.SlickyPasscodeApp
import com.example.slickypasscode.archcomponents.provideViewModel
//import com.example.slickypasscode.archcomponents.provideViewModel
import com.example.slickypasscode.databinding.PasscodeEntryLayoutBinding
import com.example.slickypasscode.network.viewmodel.ErrorType
import com.example.slickypasscode.network.viewmodel.NetworkViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityBinding: PasscodeEntryLayoutBinding

    private var listOfNumericalKeyboardButtons = ArrayList<Button>()
    private var listOfPinEnteringIndicators = ArrayList<ImageView>()

    private val networkViewModel: NetworkViewModel by provideViewModel {
        // here we can put some parameters from our activity/fragment in to our vm instance
        NetworkViewModel(SlickyPasscodeApp.instance.serverRepository)
    }

    /*private inline fun <reified VM : ViewModel> provideViewModel(crossinline vmPromise: () -> VM): Lazy<VM> {
        val factoryPromise = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return vmPromise() as T
                }
            }
        }

        return ViewModelLazy(VM::class, { viewModelStore }, factoryPromise)
    }*/

    /*private val networkViewModel : NetworkViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return NetworkViewModel(SlickyPasscodeApp.instance.serverRepository) as T
            }
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivityBinding = PasscodeEntryLayoutBinding.inflate(layoutInflater)

        setContentView(mainActivityBinding.root)

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(mainActivityBinding){
            listOfNumericalKeyboardButtons.apply {
                add(bottom)
                add(topLeft)
                add(topCenter)
                add(topRight)
                add(centerLeft)
                add(centerCenter)
                add(centerRight)
                add(bottomLeft)
                add(bottomCenter)
                add(bottomRight)
            }
            listOfPinEnteringIndicators.apply {
                add(pinFirst)
                add(pinSecond)
                add(pinThird)
                add(pinFourth)
            }
            buttonCancel.setOnClickListener {
                networkViewModel.onCancelTypedPassCodeClick()
            }
            buttonDelete.setOnClickListener {
                networkViewModel.onDeleteClick()
            }
        }
    }

    private fun setupObservers() {
        networkViewModel.getNetworkViewState().observe(this, { viewState ->
            when (viewState) {
                is NetworkViewModel.ViewState.Loading -> {
                    mainActivityBinding.loadingIndicatorLayout.visibility = View.VISIBLE
                }
                is NetworkViewModel.ViewState.Success -> {
                    mainActivityBinding.loadingIndicatorLayout.visibility = View.GONE
                    networkViewModel.onCancelTypedPassCodeClick()
                    val listOfPossibleButtonViewIdsIndices = viewState.passCodeScreenSetup
                    val buttonsViewIdsOrder = listOfPossibleButtonViewIdsIndices.withIndex()
                        .associate { it.value to it.index }
                    listOfNumericalKeyboardButtons =
                        ArrayList(listOfNumericalKeyboardButtons.sortedBy {
                            buttonsViewIdsOrder[listOfNumericalKeyboardButtons.indexOf(it)]
                        })
                    listOfNumericalKeyboardButtons.forEach { button ->
                        val number = listOfNumericalKeyboardButtons.indexOf(button)
                        button.text = number.toString()
                        button.setOnClickListener {
                            networkViewModel.onNumberClick(number)
                        }
                    }
                }
                is NetworkViewModel.ViewState.Error -> {
                    if (!viewState.errorMessageStringId.hasBeenHandled) {
                        mainActivityBinding.loadingIndicatorLayout.visibility = View.GONE
                        when (viewState.errorType) {
                            ErrorType.NETWORK_FAILURE, ErrorType.EMPTY_SETUP_DATA ->
                                showAlertDialogWithSendAPICallError(viewState.errorMessageStringId.peekContent()) { _: DialogInterface, _: Int -> networkViewModel.getPassCodeInternals() }
                            ErrorType.WRONG_PASSWORD -> {
                                Toast.makeText(
                                    this,
                                    resources.getString(viewState.errorMessageStringId.peekContent()),
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }
            }
        })

        networkViewModel.getPassCodeEnteringProgressLiveData().observe(this, { passCodeLength ->
            listOfPinEnteringIndicators.forEachIndexed { index, imageView ->
                if (index < passCodeLength) {
                    imageView.setImageResource(R.drawable.ic_progressfull)
                } else {
                    imageView.setImageResource(R.drawable.ic_progressempty)
                }
            }
        })
    }

    private fun showAlertDialogWithSendAPICallError(@StringRes errorMessageStringId : Int, positiveButtonOnClick : DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_baseline_error_24)
            .setTitle(R.string.alert)
            .setMessage(errorMessageStringId)
            .setPositiveButton(R.string.retry, positiveButtonOnClick)
            .setNegativeButton(R.string.exit) { _, _ -> finish() }
            .setCancelable(false)
            .create()
            .show()
    }

}