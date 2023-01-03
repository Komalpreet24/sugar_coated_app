package com.komal.sugarcoated.splash.ui.fragment

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.AnimationUtils.loadAnimation
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.komal.sugarcoated.R
import com.komal.sugarcoated.databinding.FragmentSplashBinding
import com.komal.sugarcoated.home.ui.fragment.HomeFragmentDirections
import com.komal.sugarcoated.signup.ui.vm.SignupViewModel

class SplashFragment : Fragment() {

  private lateinit var binding: FragmentSplashBinding
  private val splashViewModel by viewModels<SignupViewModel>()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

    binding = FragmentSplashBinding.inflate(layoutInflater)

    // This is used to hide the status bar and make
    // the splash screen as a full screen activity.
//    window.setFlags(
//      WindowManager.LayoutParams.FLAG_FULLSCREEN,
//      WindowManager.LayoutParams.FLAG_FULLSCREEN
//    )

    // HERE WE ARE TAKING THE REFERENCE OF OUR IMAGE
    // SO THAT WE CAN PERFORM ANIMATION USING THAT IMAGE
    val slideAnimation = loadAnimation(context, R.anim.side_slide)
    binding.logoImage.startAnimation(slideAnimation)

    // we used the postDelayed(Runnable, time) method
    // to send a message with a delayed time.
    Handler().postDelayed({
      view?.let { Navigation.findNavController(it)
        .navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment()) }
    }, 3000) // 3000 is the delayed time in milliseconds.

    return binding.root

  }

//  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//    super.onViewCreated(view, savedInstanceState)
//
//  }

}