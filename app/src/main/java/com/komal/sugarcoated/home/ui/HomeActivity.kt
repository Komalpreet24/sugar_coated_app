package com.komal.sugarcoated.home.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.komal.sugarcoated.R
import com.komal.sugarcoated.addNewRecord.ui.fragment.AddNewRecordFragment
import com.komal.sugarcoated.databinding.ActivityHomeBinding
import com.komal.sugarcoated.utils.ExitWithAnimation
import com.komal.sugarcoated.utils.exitCircularReveal

class HomeActivity : AppCompatActivity() {

  private lateinit var binding: ActivityHomeBinding
  private var isHomeScreen = false

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

    navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
      when (destination.id) {
        R.id.files_fragment,
        R.id.calendar_fragment,
        R.id.log_book_fragment,
        R.id.settings_fragment  -> {
          showBottomNav()
          isHomeScreen = false
        }
        R.id.home_fragment      -> {
          showBottomNav()
          isHomeScreen = true
        }
        else                    -> {
          hideBottomNav()
          isHomeScreen = false
        }
      }
    }

  }

  private fun showBottomNav() {
    binding.bottomNav.visibility = View.VISIBLE
  }

  private fun hideBottomNav() {
    binding.bottomNav.visibility = View.GONE
  }

  override fun onSupportNavigateUp(): Boolean {
    val navController = findNavController(R.id.nav_host_fragment_container)
    return navController.navigateUp() || super.onSupportNavigateUp()
  }

}