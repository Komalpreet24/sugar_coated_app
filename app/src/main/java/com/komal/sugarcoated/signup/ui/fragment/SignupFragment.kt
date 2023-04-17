package com.komal.sugarcoated.signup.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.komal.sugarcoated.R
import com.komal.sugarcoated.databinding.FragmentSignupBinding
import com.komal.sugarcoated.network.NetworkResult
import com.komal.sugarcoated.signup.ui.vm.SignupViewModel
import com.komal.sugarcoated.utils.*
import com.komal.sugarcoated.utils.Constants.RESET

class SignupFragment : BaseFragment<FragmentSignupBinding>(FragmentSignupBinding::inflate) {

  private val signupViewModel by viewModels<SignupViewModel>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.btnSignup.setOnClickListener {

      if(validate()){
        signup(binding.etEmail.text.toString(),
          binding.etSetPassword.text.toString(),
          binding.etName.text.toString())
      }
    }

    binding.tvLogin.setOnClickListener{
      navigateUpToLoginFragment()
    }

    observeSignup()

  }

  private fun navigateUpToLoginFragment() {
    findNavController().navigateUp()
  }

  private fun validate(): Boolean {
    if(binding.etName.text.isNullOrEmpty()){
      binding.etEmail.error = getString(R.string.name_validation)
      return false
    }
    if(!isEmailValid(binding.etEmail.text.toString())){
      binding.etEmail.error = getString(R.string.email_validation)
      return false
    }
    if(!isPasswordValid(binding.etSetPassword.text.toString())){
      binding.etSetPassword.error = getString(R.string.password_validation)
      return false
    }
    if(!binding.etSetPassword.text.toString().equals(binding.etConfirmPassword.text.toString())){
      binding.etConfirmPassword.error = getString(R.string.password_does_not_match)
      return false
    }

    return true
  }

  private fun signup(email: String, password: String, name: String) {
    signupViewModel.signUp(email, password, name)
  }

  private fun observeSignup() {

    signupViewModel.signUpStatus.observe(viewLifecycleOwner) { result ->
      result?.let {
        when (it) {
          is NetworkResult.ResultOf.Success -> {
            hideProgress()
            if (it.value.equals(Constants.SIGNUP_SUCCESS, ignoreCase = true)) {
              showToast(requireContext(), getString(R.string.signup_successful))
              signupViewModel.resetSignUpLiveData()
              navigateUpToLoginFragment()
            }else if(!it.value.equals(RESET, ignoreCase = true)) {
              showToast(
                requireContext(),
                String.format(getString(R.string.signup_failed), it.value)
              )
            }
          }
          is NetworkResult.ResultOf.Failure -> {
            hideProgress()
            val failedMessage = it.message ?: getString(R.string.unknown_error)
            showToast(
              requireContext(),
              String.format(getString(R.string.signup_failed), failedMessage)
            )
          }
          is NetworkResult.ResultOf.Loading -> {
            showProgress(requireContext())
          }
        }
      }
    }

  }

}