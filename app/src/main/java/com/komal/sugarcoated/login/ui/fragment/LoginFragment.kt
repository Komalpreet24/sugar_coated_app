package com.komal.sugarcoated.signup.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.komal.sugarcoated.databinding.FragmentLoginBinding
import com.komal.sugarcoated.home.ui.fragment.HomeFragmentDirections
import com.komal.sugarcoated.signup.ui.vm.SignupViewModel
import com.komal.sugarcoated.utils.KeyboardUtils
import com.komal.sugarcoated.utils.KeyboardUtils.SoftKeyboardToggleListener


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel by viewModels<SignupViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener{
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
        }


    }
}