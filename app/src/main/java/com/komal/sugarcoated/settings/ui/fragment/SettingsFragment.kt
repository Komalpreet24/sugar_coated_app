package com.komal.sugarcoated.settings.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.komal.sugarcoated.R
import com.komal.sugarcoated.login.ui.vm.LoginViewModel
import com.komal.sugarcoated.network.NetworkResult
import com.komal.sugarcoated.utils.Constants
import com.komal.sugarcoated.utils.Constants.RESET
import com.komal.sugarcoated.utils.hideProgress
import com.komal.sugarcoated.utils.showProgress
import com.komal.sugarcoated.utils.showToast

class SettingsFragment : PreferenceFragmentCompat() {

    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (preferenceManager.findPreference<Preference>("logout"))?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                logout()
                true
            }

        preferenceManager.findPreference<Preference>("web-view link")?.let {
            it.setOnPreferenceChangeListener { _, newValue ->
                updateUserDetails(newValue)
                true
            }
        }

    }

    private fun logout() {
        loginViewModel.logout()
        observeLogout()
    }

    private fun observeLogout() {

        loginViewModel.logoutStatus.observe(viewLifecycleOwner) { result ->
            result?.let {
                when (it) {
                    is NetworkResult.ResultOf.Success -> {
                        hideProgress()
                        if (it.value.equals(Constants.LOGOUT_SUCCESS, ignoreCase = true)) {
                            showToast(requireContext(), getString(R.string.logout_successful))
                            loginViewModel.resetLoginLiveData()
                            navigateToLoginScreen()
                        }else if (!it.value.equals(RESET, ignoreCase = true)){
                            showToast(
                                requireContext(),
                                String.format(getString(R.string.logout_failed), it.value)
                            )
                        }
                    }
                    is NetworkResult.ResultOf.Failure -> {
                        hideProgress()
                        val failedMessage = it.message ?: getString(R.string.unknown_error)
                        showToast(
                            requireContext(),
                            String.format(getString(R.string.logout_failed), failedMessage)
                        )
                    }
                    is NetworkResult.ResultOf.Loading -> {
                        showProgress(requireContext())
                    }
                }
            }
        }

    }

    private fun navigateToLoginScreen() {
        findNavController().navigate(
            SettingsFragmentDirections.actionSettingsFragmentToLoginFragment())
    }

    private fun updateUserDetails(newValue: Any?) {
        loginViewModel.setUserData(newValue)
    }

}