package com.komal.sugarcoated.files.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.komal.sugarcoated.R
import com.komal.sugarcoated.databinding.FragmentFilesBinding
import com.komal.sugarcoated.files.model.FileData
import com.komal.sugarcoated.files.ui.vm.FileClickListener
import com.komal.sugarcoated.files.ui.vm.FilesViewModel
import com.komal.sugarcoated.network.NetworkResult
import com.komal.sugarcoated.utils.*

class FilesFragment : BaseFragment<FragmentFilesBinding>(FragmentFilesBinding::inflate),
                      FileClickListener {

  private val filesViewModel by viewModels<FilesViewModel>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    observeFileUpload()
    observeFileList()

    val layoutManager = GridLayoutManager(requireContext(), 3)
    binding.rvFileList.layoutManager = layoutManager

    getFileList()

    binding.btnUpload.setOnClickListener {

      val intent = Intent(Intent.ACTION_GET_CONTENT)
      intent.type = "*/*"
      uploadFileLauncher.launch(intent)

    }

  }

  private fun observeFileUpload() {

    filesViewModel.uploadFileStatus.observe(viewLifecycleOwner) { result ->
      result?.let {
        when (it) {
          is NetworkResult.ResultOf.Success -> {
            hideProgress()
            if (it.value.equals(Constants.UPLOAD_FILE_SUCCESS, ignoreCase = true)) {
              showToast(requireContext(), getString(R.string.file_upload_successful))
              filesViewModel.getFileList()
            } else if (!it.value.equals(Constants.RESET, ignoreCase = true)) {
              showToast(
                requireContext(),
                String.format(getString(R.string.file_upload_failed), it.value)
              )
            }
            filesViewModel.resetUploadFileData()
          }
          is NetworkResult.ResultOf.Failure -> {
            hideProgress()
            val failedMessage = it.message ?: getString(R.string.unknown_error)
            showToast(
              requireContext(),
              String.format(
                getString(R.string.file_upload_failed), failedMessage)
            )
            filesViewModel.resetUploadFileData()
          }
          is NetworkResult.ResultOf.Loading -> {
            showProgress(requireContext())
          }
        }
      }
    }

  }

  private fun getFileList() {
    filesViewModel.getFileList()
  }

  private fun observeFileList() {
    filesViewModel.fileList.observe(viewLifecycleOwner) { result ->
      result?.let {
        when (it) {
          is NetworkResult.ResultOf.Success -> {
            hideProgress()
            if (it.value?.size!! > 0) {
              binding.rvFileList.adapter = FileListAdapter(it.value, this)
            } else {
              showToast(
                requireContext(),
                String.format(getString(R.string.files_fetching_failed), it.value)
              )
            }
            filesViewModel.resetFileListData()
          }
          is NetworkResult.ResultOf.Failure -> {
            hideProgress()
            val failedMessage = it.message ?: getString(R.string.unknown_error)
            showToast(
              requireContext(),
              String.format(getString(R.string.files_fetching_failed), failedMessage)
            )
            filesViewModel.resetFileListData()
          }
          is NetworkResult.ResultOf.Loading -> {
            showProgress(requireContext())
          }
        }
      }
    }
  }

  private val uploadFileLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
      val data: Intent? = result.data
      val selectedFileUri = data?.data
      if (selectedFileUri != null) {
        filesViewModel.uploadFile("fileName", selectedFileUri)
      }
    }
  }

  override fun onFileClicked(file: FileData) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(Uri.parse(file.url), "application/pdf")
    val chooserIntent = Intent.createChooser(intent, "Choose an Application:")
    chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    requireContext().startActivity(chooserIntent)
  }

}