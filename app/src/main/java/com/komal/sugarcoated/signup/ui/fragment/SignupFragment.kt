package com.komal.sugarcoated.signup.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.komal.sugarcoated.databinding.FragmentHomeBinding
import com.komal.sugarcoated.databinding.FragmentSignupBinding
import com.komal.sugarcoated.home.ui.fragment.HomeFragmentDirections
import com.komal.sugarcoated.signup.ui.vm.SignupViewModel
import com.komal.sugarcoated.utils.Constants

class SignupFragment : Fragment() {

  private lateinit var binding: FragmentSignupBinding
  private val signUpViewModel by viewModels<SignupViewModel>()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

    binding = FragmentSignupBinding.inflate(layoutInflater)
    return binding.root

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)



  }
}