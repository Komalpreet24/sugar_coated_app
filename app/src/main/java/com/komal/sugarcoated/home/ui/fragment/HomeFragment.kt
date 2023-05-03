package com.komal.sugarcoated.home.ui.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.komal.sugarcoated.R
import com.komal.sugarcoated.databinding.FragmentHomeBinding
import com.komal.sugarcoated.home.ui.vm.HomeViewModel
import com.komal.sugarcoated.utils.BaseFragment
import com.komal.sugarcoated.utils.Constants.FIVE_MINUTES
import com.komal.sugarcoated.utils.Constants.ONE_SECOND
import com.komal.sugarcoated.utils.findLocationOfCenterOnTheScreen


class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val homeViewModel by viewModels<HomeViewModel>()
    private lateinit var webView: WebView
    private var url: String? = null
    private var isWebViewReloading = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addNewRecord.setOnClickListener{

            val positions = it.findLocationOfCenterOnTheScreen()
            val bundle = bundleOf("positions" to positions)
            findNavController()
                .navigate(R.id.action_home_fragment_to_add_new_record_fragment, bundle)

        }

        setupWebView()

        reloadWebView()

        binding.swipeContainer.setOnRefreshListener {
            binding.swipeContainer.isRefreshing = true
            Handler(Looper.getMainLooper()).postDelayed({
                url?.let { webView.loadUrl(it) }
                binding.swipeContainer.isRefreshing = false
            }, ONE_SECOND)
        }

    }

    private fun setupWebView(){
        webView = binding.webView

        url = homeViewModel.getTomatoSharedUrl()

        url?.let { webView.loadUrl(it) }

        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object :WebViewClient(){

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                url?.let { webView.loadUrl(it) }
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if(!isWebViewReloading)
                    makeWebViewInvisible()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (!isWebViewReloading)
                    makeWebViewVisible()
            }

        }
    }

    private fun reloadWebView() {
        Handler(Looper.getMainLooper()).postDelayed({
            reloadWebView()
            isWebViewReloading = true
            url?.let { webView.loadUrl(it) }
        }, FIVE_MINUTES)
    }

    fun makeWebViewInvisible() {
        binding.webView.visibility = View.INVISIBLE
        binding.progressCircular.visibility = View.VISIBLE
    }

    fun makeWebViewVisible() {
        binding.webView.visibility = View.VISIBLE
        binding.progressCircular.visibility = View.INVISIBLE
    }

}