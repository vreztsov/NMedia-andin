package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import ru.netology.nmedia.R
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.auth.RegisterInfo
import ru.netology.nmedia.util.SingleLiveEvent

class RegisterViewModel : ViewModel() {

    val isAuthorized: Boolean
        get() = AppAuth.getInstance().authStateFlow.value.id != 0L

    private val _registerSuccessEvent = SingleLiveEvent<Unit>()
    val registerSuccessEvent: LiveData<Unit>
        get() = _registerSuccessEvent

    private val _registerError = MutableLiveData<String?>(null)
    val registerError: LiveData<String?>
        get() = _registerError

    private val _registerInfo =
        MutableLiveData(RegisterInfo())
    val registerInfo: LiveData<RegisterInfo>
        get() = _registerInfo

    private val _completionWarningSet = MutableLiveData(emptySet<Int>())
    val completionWarningSet: LiveData<Set<Int>>
        get() = _completionWarningSet

    fun doRegister() {

        if (!completed()) {
            _registerError.value = "Register with uncompleted status error!"
            return
        }

        _registerError.value = null

        viewModelScope.launch {
            try {
                registerUser()
                if (isAuthorized)
                    _registerSuccessEvent.value = Unit
                else {
                    _registerError.value = "Unexpected register error!"
                }
            } catch (e: Exception) {
                println("CATCH OF REGISTER USER - ${e.message.toString()}")
                val errText = if (e.message.toString() == "") "Unknown register error!"
                else e.message.toString()
                _registerError.value = errText
            }
        }

    }

    private suspend fun registerUser() {
        var response: Response<AuthState>? = null
        try {
            response = PostsApi.retrofitService.registerUser(
                registerInfo.value?.login ?: "",
                registerInfo.value?.password ?: "",
                registerInfo.value?.username ?: "",
            )
        } catch (e: Exception) {
            throw RuntimeException("Server response failed: ${e.message.toString()}")
        }

        if (!response.isSuccessful) {
            val errText = if (response.message() == "")
                "No server response" else response.message()
            throw RuntimeException("Request declined: $errText")
        }
        val responseToken = response.body() ?: throw RuntimeException("body is null")
        AppAuth.getInstance().setAuth(
            responseToken.id,
            responseToken.token ?: throw RuntimeException("token is null")
        )

    }

    fun resetRegisterInfo(newRegisterInfo: RegisterInfo) {
        _registerError.value = null
        _registerInfo.value = newRegisterInfo
        val warnIdSet = mutableSetOf<Int>()

        registerInfo.value?.let {
            if (it.login.isBlank()) {
                warnIdSet.add(R.string.warning_no_login)
            }
            if (it.password.isEmpty()) {
                warnIdSet.add(R.string.warning_no_password)
            }
            if (it.password.length != it.password2.length) {
                warnIdSet.add(R.string.warning_no_matching_password)
            }
            if (it.username.isBlank()) {
                warnIdSet.add(R.string.warning_no_username)
            }
        }
        _completionWarningSet.value = warnIdSet
        viewModelScope.launch { delay(300) }
    }

    fun completed(): Boolean {
        return _completionWarningSet.value?.isEmpty() ?: false
    }

}
