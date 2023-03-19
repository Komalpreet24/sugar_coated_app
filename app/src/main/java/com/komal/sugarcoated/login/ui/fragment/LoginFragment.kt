package com.komal.sugarcoated.login.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.komal.sugarcoated.R
import com.komal.sugarcoated.databinding.FragmentLoginBinding
import com.komal.sugarcoated.login.ui.vm.LoginViewModel
import com.komal.sugarcoated.network.NetworkResult
import com.komal.sugarcoated.utils.*
import com.komal.sugarcoated.utils.Constants.RESET

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

  private val loginViewModel by viewModels<LoginViewModel>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.btnLogin.setOnClickListener {

      if(validate()){
        login(binding.etEmail.text.toString(),binding.etPassword.text.toString())
      }

    }

    binding.tvSignUp.setOnClickListener{
      navigateToSignUpFragment()
    }

  }

  private fun validate(): Boolean {
    if(!isEmailValid(binding.etEmail.text.toString())){
      binding.etEmail.error = getString(R.string.email_validation)
      return false
    }
    if(!isPasswordValid(binding.etPassword.text.toString())){
      binding.etPassword.error = getString(R.string.password_validation)
      return false
    }
    return true
  }

  private fun login(email: String, password: String) {
    loginViewModel.signIn(email, password)
    observeLogin()
  }

  private fun observeLogin() {

    loginViewModel.loginStatus.observe(viewLifecycleOwner, Observer {result->
      result?.let {
        when(it){
          is NetworkResult.ResultOf.Success ->{
            hideProgress()
            if(it.value.equals(Constants.LOGIN_SUCCESS,ignoreCase = true)){
              showToast(requireContext(), getString(R.string.login_successful))
              loginViewModel.resetLoginLiveData()
              navigateToHomeFragment()
            }else if(it.value.equals(RESET,ignoreCase = true)){

            }
            else{
              showToast(
                requireContext(),
                String.format(getString(R.string.login_failed), it.value)
              )
            }
          }
          is NetworkResult.ResultOf.Failure -> {
            hideProgress()
            val failedMessage =  it.message ?: getString(R.string.unknown_error)
            showToast(
              requireContext(),
              String.format(getString(R.string.login_failed), failedMessage)
            )
          }
          is NetworkResult.ResultOf.Loading -> {
            showProgress(requireContext())
          }
        }
      }
    })

  }

  private fun navigateToHomeFragment() {
    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
  }

  private fun navigateToSignUpFragment() {
    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignupFragment())
  }

}