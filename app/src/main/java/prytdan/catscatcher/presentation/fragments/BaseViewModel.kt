package prytdan.catscatcher.presentation.fragments

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    private var isAlreadyInitialized = false

    fun attachView() {
        if (!isAlreadyInitialized) {
            isAlreadyInitialized = true
            onFirstAttach()
        }
    }

    protected open fun onFirstAttach() {}
}