package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.LoginInfo
import ru.netology.nmedia.util.SingleLiveEvent
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AuthState

class LoginViewModel : ViewModel() {

    val isAuthorized: Boolean
        get() = AppAuth.getInstance().authStateFlow.value.id != 0L

    private val _loginSuccessEvent = SingleLiveEvent<Unit>()
    val loginSuccessEvent: LiveData<Unit>
        get() = _loginSuccessEvent

    private val _loginError = MutableLiveData<String?>(null)
    val loginError: LiveData<String?>
        get() = _loginError

    private val _loginInfo = MutableLiveData(LoginInfo())
    val loginInfo: LiveData<LoginInfo>
        get() = _loginInfo

    private val _completionWarningSet = MutableLiveData(emptySet<Int>())
    val completionWarningSet: LiveData<Set<Int>>
        get() = _completionWarningSet

    fun doLogin() {

        if (!completed()) {
            _loginError.value = "Login with uncompleted status error!"
            return
        }

         _loginError.value = null

        viewModelScope.launch {
            try {
                updateUser()
                if (isAuthorized)
                    _loginSuccessEvent.value = Unit
                else {
                    _loginError.value = "Unexpected login error!"
                }
            } catch (e: Exception) {
                println("CATCH OF UPDATE USER - ${e.message.toString()}")
                val errText = if (e.message.toString() == "") "Unknown login error!"
                        else e.message.toString()
                _loginError.value = errText
            }
        }

    }

    private suspend fun updateUser() {
        val response: Response<AuthState>?
        try {
            response = PostsApi.retrofitService.updateUser(
                loginInfo.value?.login ?: "",
                loginInfo.value?.password ?: ""
            )

        } catch (e: Exception) {
            throw RuntimeException("Server response failed: ${e.message.toString()}")
        }

        if (!response.isSuccessful) {
            val errText = if (response.message() == "")
                "No server response" else response.message()
            throw RuntimeException("Request declined: $errText")
        }
        val authState = response.body() ?: throw RuntimeException("body is null")

        AppAuth.getInstance().setAuth(authState.id, authState.token ?: throw RuntimeException("token is null"))

    }

    fun resetLoginInfo(newLoginInfo: LoginInfo) {
        _loginError.value = null
        _loginInfo.value = newLoginInfo
        val warnIdSet = mutableSetOf<Int>()

        loginInfo.value?.let {
            if (it.login.isEmpty()) {
                warnIdSet.add(R.string.warning_no_login)
            }
            if (it.password.isEmpty()) {
                warnIdSet.add(R.string.warning_no_password)
            }
        }
        _completionWarningSet.value = warnIdSet
        viewModelScope.launch { delay(300) }
    }

    fun completed(): Boolean {
        return _completionWarningSet.value?.isEmpty() ?: false
    }

}
