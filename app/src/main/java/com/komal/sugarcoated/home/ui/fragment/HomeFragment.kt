package com.komal.sugarcoated.home.ui.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.komal.sugarcoated.databinding.FragmentHomeBinding
import com.komal.sugarcoated.home.ui.vm.HomeViewModel


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel by viewModels<HomeViewModel>()
    private lateinit var webView: WebView
    private lateinit var url: String
    private var isWebViewReloading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addNewRecord.setOnClickListener{
            Navigation.findNavController(it).navigate(HomeFragmentDirections.actionHomeToAddNewRecord())
        }

        setupWebView()

        reloadWebView()

        binding.swipeContainer.setOnRefreshListener {
            binding.swipeContainer.isRefreshing = true
            Handler(Looper.getMainLooper()).postDelayed({
                webView.loadUrl(url)
                binding.swipeContainer.isRefreshing = false
            }, 1000)
        }

    }

    private fun reloadWebView() {
        Handler(Looper.getMainLooper()).postDelayed({
            reloadWebView()
            isWebViewReloading = true
            webView.loadUrl(url)
        }, 300000)
    }

    private fun setupWebView(){
        webView = binding.webView

        url = homeViewModel.getTomatoSharedUrl()

        webView.loadUrl(url)

        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object :WebViewClient(){

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                webView.loadUrl(url)
                return true;
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

    fun makeWebViewInvisible() {
        binding.webView.visibility = View.INVISIBLE
        binding.progressCircular.visibility = View.VISIBLE
    }

    fun makeWebViewVisible() {
        binding.webView.visibility = View.VISIBLE
        binding.progressCircular.visibility = View.INVISIBLE
    }

}