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
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * [Fragment] para la gestión de registro de usuarios
 */
@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeUiState()
    }

    private fun setupViews() {
        // Configurar botón de registro
        binding.registerBtn.setOnClickListener {
            if (validateForm()) {
                val userName = binding.identifierInput.text.toString()
                val email = binding.emailInput.text.toString()
                val password = binding.passwordInput.text.toString()
                viewModel.onRegister(userName, email, password)
            }
        }

        // Configurar botón para volver al login si existe
        binding.toLoginBtn?.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Validación de email
        if (binding.emailInput.isEmpty(binding.emailLabel)) {
            isValid = false
        }

        // Validación de nombre de usuario
        if (binding.identifierInput.isEmpty(binding.identifierLabel)) {
            isValid = false
        }

        // Validación de contraseña
        if (binding.passwordInput.isEmpty(binding.passwordLabel)) {
            isValid = false
        }

        // Validación de confirmación de contraseña
        if (binding.passwordRepeatInput.isEmpty(binding.passwordRepeatLabel)) {
            isValid = false
        }

        // Validación de coincidencia de contraseñas
        if (binding.passwordInput.differentContent(
                binding.passwordRepeatInput,
                binding.passwordRepeatLabel
            )) {
            isValid = false
        }

        return isValid
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
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

                            // Mostrar mensaje de éxito antes de navegar
                            Snackbar.make(
                                binding.root,
                                "Registro exitoso. Ya puedes iniciar sesión.",
                                Snackbar.LENGTH_SHORT
                            ).show()

                            // Esperar un momento para que el usuario vea el mensaje
                            binding.root.postDelayed({
                                findNavController().popBackStack()
                            }, 1500)
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

    private fun hideProgress() {
        binding.registerProgressIndicator.isVisible = false
        binding.registerCard.alpha = 1.0f
    }

    private fun showProgress() {
        binding.registerProgressIndicator.isVisible = true
        binding.registerCard.alpha = 0.5f
    }

    private fun disableInput() {
        binding.registerBtn.isEnabled = false
        binding.passwordInput.isEnabled = false
        binding.passwordRepeatInput.isEnabled = false
        binding.identifierInput.isEnabled = false
        binding.emailInput.isEnabled = false
        binding.toLoginBtn?.isEnabled = false
    }

    private fun enableInput() {
        binding.registerBtn.isEnabled = true
        binding.passwordInput.isEnabled = true
        binding.passwordRepeatInput.isEnabled = true
        binding.identifierInput.isEnabled = true
        binding.emailInput.isEnabled = true
        binding.toLoginBtn?.isEnabled = true
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(resources.getColor(android.R.color.holo_red_light, null))
            .setTextColor(resources.getColor(android.R.color.white, null))
            .show()
    }

    private fun hideError() {
        binding.passwordLabel.error = null
        binding.identifierLabel.error = null
        binding.emailLabel.error = null
        binding.passwordRepeatLabel.error = null
    }
}