package com.komal.sugarcoated.splash.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils.loadAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.komal.sugarcoated.R
import com.komal.sugarcoated.databinding.FragmentSplashBinding
import com.komal.sugarcoated.utils.AppSharedPreferences

class SplashFragment : Fragment() {

  private lateinit var binding: FragmentSplashBinding
  private var sharedPreferences: AppSharedPreferences? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentSplashBinding.inflate(layoutInflater)
    val slideAnimation = loadAnimation(context, R.anim.side_slide)
    binding.logoImage.startAnimation(slideAnimation)
    sharedPreferences = AppSharedPreferences.getInstance(requireContext())
    Handler(Looper.getMainLooper()).postDelayed({
      view?.let { navigate() }
    }, 3000)

    return binding.root
  }

  private fun navigate() {
    if(sharedPreferences?.isLoggedIn == true)
      navigateToHomeFragment()
    else
      navigateToLoginFragment()
  }

  private fun navigateToHomeFragment() {
    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
  }

  private fun navigateToLoginFragment() {
    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
  }

}