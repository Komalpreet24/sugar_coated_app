package com.komal.sugarcoated.home.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.komal.sugarcoated.R
import com.komal.sugarcoated.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

  private lateinit var binding: ActivityHomeBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityHomeBinding.inflate(layoutInflater)
    val view = binding.root
    setContentView(view)

    setUpNavigation()

  }

  private fun setUpNavigation() {
    val navHostFragment =
      supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
    NavigationUI.setupWithNavController(
      binding.bottomNav,
      navHostFragment.navController
    )

    navHostFragment.navController.addOnDestinationChangedListener { controller, destination, arguments ->
      when (destination.id) {
        R.id.home_fragment,
        R.id.files_fragment,
        R.id.calendar_fragment,
        R.id.log_book_fragment,
        R.id.settings_fragment  -> showBottomNav()
        else                    -> hideBottomNav()
      }
    }
  }

  private fun showBottomNav() {
    binding.bottomNav.visibility = View.VISIBLE
  }

  private fun hideBottomNav() {
    binding.bottomNav.visibility = View.GONE
  }

}