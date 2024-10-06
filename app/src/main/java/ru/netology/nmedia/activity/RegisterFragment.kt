package ru.netology.nmedia.activity

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.auth.RegisterInfo
import ru.netology.nmedia.databinding.FragmentRegisterBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.RegisterViewModel


class RegisterFragment : Fragment() {

    val viewModel by activityViewModels<RegisterViewModel>()
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        binding.register.isEnabled = true

        subscribe()
        setListeners()

        return binding.root
    }

    private fun setListeners() {

        binding.register.setOnClickListener {

            AndroidUtils.hideKeyboard(requireView())

            viewModel.resetRegisterInfo(
                RegisterInfo(
                    binding.username.text.toString(),
                    binding.login.text.toString(),
                    binding.password.text.toString(),
                    binding.password2.text.toString(),
                )
            )

            if (viewModel.completed()) {
                binding.register.isEnabled = false
                viewModel.doRegister()
            }
        }
    }

    private fun subscribe() {

        viewModel.registerSuccessEvent.observe(viewLifecycleOwner) {

            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
            println("RegisterFragment was leaved")
        }

        viewModel.completionWarningSet.observe(viewLifecycleOwner) { warnings ->
            if (warnings.isEmpty()) return@observe
            val msg = warnings.joinToString("; ") { getString(it) }
            showToast(msg)
            Log.d("RegisterFragment", "Register info: ${viewModel.registerInfo}")

        }

        viewModel.registerError.observe(viewLifecycleOwner) { errText ->
            if (errText == null) return@observe
            showToast(errText)
            binding.register.isEnabled = true
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
