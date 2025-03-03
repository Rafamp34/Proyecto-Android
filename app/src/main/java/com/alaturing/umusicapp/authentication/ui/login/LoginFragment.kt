package com.alaturing.umusicapp.authentication.ui.login

import android.content.Intent
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
import androidx.navigation.findNavController
import com.alaturing.umusicapp.main.MainActivity
import com.alaturing.umusicapp.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragmento para la pantalla de login
 */
@AndroidEntryPoint
class LoginFragment : Fragment() {
    // Enlace a los elementos de pantalla
    private lateinit var binding: FragmentLoginBinding
    // Estado de la pantalla
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeUiState()
    }

    private fun setupViews() {
        // Manejador botón logueo
        binding.loginBtn.setOnClickListener {
            val identifier = binding.identifierInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (validateInputs(identifier, password)) {
                viewModel.onLogin(identifier, password)
            }
        }

        binding.toRegisterBtn.setOnClickListener(::toRegister)
    }

    private fun validateInputs(identifier: String, password: String): Boolean {
        var isValid = true

        // Validar campo de identificador
        if (identifier.isBlank()) {
            binding.identifierLabel.error = "Este campo es obligatorio"
            isValid = false
        } else {
            binding.identifierLabel.error = null
        }

        // Validar campo de contraseña
        if (password.isBlank()) {
            binding.passwordLabel.error = "Este campo es obligatorio"
            isValid = false
        } else {
            binding.passwordLabel.error = null
        }

        return isValid
    }

    private fun observeUiState() {
        // Modificar apariencia de la pantalla en función del estado
        viewLifecycleOwner.lifecycleScope.launch {
            // Siempre que alcancemos el estado de iniciado
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Comenzamos a observar el estado que pública el view model asociado
                viewModel.uiState.collect { uiState ->
                    when(uiState) {
                        is LoginUiState.Error -> {
                            hideProgress()
                            enableInput()
                            showError(uiState.errorMessage)
                        }
                        LoginUiState.Initial -> {
                            hideProgress()
                            enableInput()
                            hideError()
                        }
                        LoginUiState.LoggedIn -> {
                            hideProgress()
                            hideError()
                            disableInput()
                            toMain()
                            requireActivity().finish()
                        }
                        LoginUiState.LoggingIn -> {
                            showProgress()
                            disableInput()
                            hideError()
                        }
                    }
                }
            }
        }
    }

    private fun toMain() = startActivity(Intent(requireContext(), MainActivity::class.java))

    private fun toRegister(v:View) {
        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        v.findNavController().navigate(action)
    }

    private fun setProgress(isVisible:Boolean) {
        binding.loginProgressIndicator.isVisible = isVisible
        // Opcional: hacer que el fondo se oscurezca durante la carga
        binding.loginCard.alpha = if (isVisible) 0.5f else 1.0f
    }

    private fun hideProgress() = setProgress(false)

    private fun showProgress() = setProgress(true)

    private fun setInputState(enable:Boolean) {
        binding.loginBtn.isEnabled = enable
        binding.passwordInput.isEnabled = enable
        binding.identifierInput.isEnabled = enable
        binding.toRegisterBtn.isEnabled = enable
    }

    private fun disableInput() = setInputState(false)

    private fun enableInput() = setInputState(true)

    private fun setError(message:String?=null) {
        if (message != null) {
            // Mostrar un Snackbar en lugar de solo poner el error en los campos
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(resources.getColor(android.R.color.holo_red_light, null))
                .setTextColor(resources.getColor(android.R.color.white, null))
                .show()
        }
    }

    private fun showError(message:String) = setError(message)

    private fun hideError() = setError()
}