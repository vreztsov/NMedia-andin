package ru.netology.nmedia.activity

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.auth.LoginInfo
import ru.netology.nmedia.databinding.FragmentLoginBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.LoginViewModel


class LoginFragment : Fragment() {

    val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        binding.signIn.isEnabled = true

        subscribe()
        setListeners()

        return binding.root
    }

    private fun setListeners() {

        binding.signIn.setOnClickListener {

            AndroidUtils.hideKeyboard(requireView())

            viewModel.resetLoginInfo(
                LoginInfo(
                    binding.login.text.toString(),
                    binding.password.text.toString()
                )
            )

            if (viewModel.completed()) {
                binding.signIn.isEnabled = false
                viewModel.doLogin()
            }
        }
    }

    private fun subscribe() {

        viewModel.loginSuccessEvent.observe(viewLifecycleOwner) {

            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
            println("LoginFragment was leaved")
        }

        viewModel.completionWarningSet.observe(viewLifecycleOwner) { warnings ->
            if (warnings.isEmpty()) return@observe
            val msg = warnings.joinToString("; ") { getString(it) }
            showToast(msg)
            Log.d("LoginFragment", "Login info: ${viewModel.loginInfo}")

        }

        viewModel.loginError.observe(viewLifecycleOwner) { errText ->
            if (errText == null) return@observe
             showToast(errText)
            binding.signIn.isEnabled = true
        }

    }

    private fun showToast(textInformation: String) {
        val warnToast = Toast.makeText(
            activity,
            textInformation,
            Toast.LENGTH_SHORT
        )
        warnToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        warnToast.show()
    }

}
