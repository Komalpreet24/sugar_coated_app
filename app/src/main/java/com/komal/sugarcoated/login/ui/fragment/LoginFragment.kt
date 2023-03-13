package com.komal.sugarcoated.login.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.komal.sugarcoated.databinding.FragmentLoginBinding
import com.komal.sugarcoated.login.ui.vm.LoginViewModel
import com.komal.sugarcoated.network.NetworkResult
import com.komal.sugarcoated.utils.BaseFragment
import com.komal.sugarcoated.utils.hideProgress
import com.komal.sugarcoated.utils.showProgress
//import com.komal.sugarcoated.utils.hideProgress
//import com.komal.sugarcoated.utils.showProgress
import com.komal.sugarcoated.utils.showToast


class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            showProgress(requireContext())

            if(validate()){
                login(binding.etEmail.text.toString(),binding.etPassword.text.toString())
            }

        }

    }

    private fun validate(): Boolean {
        if(TextUtils.isEmpty(binding.etEmail.text.toString())){
            binding.etEmail.error = "Email cannot be empty"
            return false
        }
        if(TextUtils.isEmpty(binding.etPassword.text.toString())){
            binding.etPassword.error = "Password cannot be empty"
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
                        if(it.value.equals("Login Successful",ignoreCase = true)){
                            showToast(requireContext(), "Login Successful")
                            loginViewModel.resetLoginLiveData()
                            navigateToHomeFragment()
                        }else if(it.value.equals("Reset",ignoreCase = true)){

                        }
                        else{
                            showToast(requireContext(), "Login failed with ${it.value}")
                        }
                    }
                    is NetworkResult.ResultOf.Failure -> {
                        hideProgress()
                        val failedMessage =  it.message ?: "Unknown Error"
                        showToast(requireContext(), "Login failed with $failedMessage")
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

}