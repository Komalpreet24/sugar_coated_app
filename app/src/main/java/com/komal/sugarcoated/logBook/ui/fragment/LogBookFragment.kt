package com.komal.sugarcoated.logBook.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.komal.sugarcoated.R
import com.komal.sugarcoated.databinding.FragmentLogBookBinding
import com.komal.sugarcoated.logBook.ui.vm.LogBookViewModel
import com.komal.sugarcoated.network.NetworkResult
import com.komal.sugarcoated.utils.*

class LogBookFragment : BaseFragment<FragmentLogBookBinding>(FragmentLogBookBinding::inflate) {

    private val logBookViewModel by viewModels<LogBookViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvRecords.layoutManager = LinearLayoutManager(requireContext())
        observeRecordList()
        getRecordList()

    }

    private fun getRecordList() {
        logBookViewModel.getRecordList()
    }

    private fun observeRecordList() {
        logBookViewModel.recordList.observe(viewLifecycleOwner) { result ->
            result?.let {
                when (it) {
                    is NetworkResult.ResultOf.Success -> {
                        hideProgress()
                        if (it.value?.size!! > 0) {
                            binding.rvRecords.adapter = LogBookAdapter(it.value)
                        } else {
                            showToast(
                                requireContext(),
                                String.format(getString(R.string.records_fetching_failed), it.value)
                            )
                        }
                        logBookViewModel.resetRecordListData()
                    }
                    is NetworkResult.ResultOf.Failure -> {
                        hideProgress()
                        val failedMessage = it.message ?: getString(R.string.unknown_error)
                        showToast(
                            requireContext(),
                            String.format(getString(R.string.records_fetching_failed), failedMessage)
                        )
                        logBookViewModel.resetRecordListData()
                    }
                    is NetworkResult.ResultOf.Loading -> {
                        showProgress(requireContext())
                    }
                }
            }
        }
    }

}