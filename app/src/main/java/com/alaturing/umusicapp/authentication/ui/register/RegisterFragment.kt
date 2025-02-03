package com.alaturing.umusicapp.authentication.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.alaturing.umusicapp.common.validation.isEmpty
import com.alaturing.umusicapp.common.validation.differentContent
import com.alaturing.umusicapp.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 *  [Fragment] para la gestión de registro de usuarios
 */
@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    private fun validateForm(): Boolean {
        val isNotValid = binding.passwordInput.isEmpty(binding.passwordLabel) ||
                binding.passwordRepeatInput.isEmpty(binding.passwordRepeatLabel) ||
                binding.emailInput.isEmpty(binding.emailLabel) ||
                binding.identifierInput.isEmpty(binding.identifierLabel) ||
                binding.passwordInput.differentContent(
                    binding.passwordRepeatInput,
                    binding.passwordRepeatLabel
                )
        return !isNotValid
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerBtn.setOnClickListener {



            if (validateForm())
            {
                val userName = binding.identifierInput.text.toString()
                val email = binding.emailInput.text.toString()
                val password = binding.passwordInput.text.toString()
                viewModel.onRegister(userName, email, password)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    uiState ->
                        when (uiState) {
                            is RegisterUiState.Error -> {
                                hideProgress()
                                enableInput()
                                showError(uiState.errorMessage)
                            }
                            RegisterUiState.Initial -> {
                                hideProgress()
                                hideError()
                                enableInput()
                            }
                            RegisterUiState.Registered -> {
                                hideProgress()
                                hideError()
                                findNavController().popBackStack()
                            }
                            RegisterUiState.Registering -> {
                                showProgress()
                                disableInput()
                                hideError()
                            }
                        }
                }
            }
        }
    }
    private fun hideProgress() { binding.registerProgressIndicator.isVisible = false }
    private fun showProgress() { binding.registerProgressIndicator.isVisible = true }
    private fun disableInput() {
        binding.registerBtn.isEnabled = false
        binding.passwordInput.isEnabled = false
        binding.passwordRepeatInput.isEnabled = false
        binding.identifierInput.isEnabled = false
        binding.emailInput.isEnabled = false
    }
    private fun enableInput() {
        binding.registerBtn.isEnabled = true
        binding.passwordInput.isEnabled = true
        binding.passwordRepeatInput.isEnabled = true
        binding.identifierInput.isEnabled = true
        binding.emailInput.isEnabled = true
    }
    private fun showError(message:String) {
        binding.passwordLabel.error = message
        binding.identifierLabel.error = message
        binding.emailLabel.error = message
    }
    private fun hideError() {
        binding.passwordLabel.error = null
        binding.identifierLabel.error = null
        binding.emailLabel.error = null
    }

}