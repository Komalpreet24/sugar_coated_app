package com.komal.sugarcoated.addNewRecord.ui.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.komal.sugarcoated.R
import com.komal.sugarcoated.addNewRecord.model.RecordDataModel
import com.komal.sugarcoated.addNewRecord.ui.vm.RecordViewModel
import com.komal.sugarcoated.databinding.FragmentAddNewRecordBinding
import com.komal.sugarcoated.network.NetworkResult
import com.komal.sugarcoated.utils.*
import com.komal.sugarcoated.utils.Constants.DATE_TIME_FORMAT
import java.text.SimpleDateFormat
import java.util.*


class AddNewRecordFragment: BaseFragment<FragmentAddNewRecordBinding>
                            (FragmentAddNewRecordBinding::inflate), ExitWithAnimation {

    private val recordViewModel by viewModels<RecordViewModel>()

    override var posX: Int? = null
    override var posY: Int? = null
    private val checkboxStates = booleanArrayOf(false, false, false, false, false, false, false)

    override fun isToBeExitedWithAnimation(): Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val positions = arguments?.getIntArray("positions")
        if (positions != null && positions.size == 2) {
            posX = positions[0]
            posY = positions[1]
        }
        view.startCircularReveal(false)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    isEnabled = true
                    view.exitCircularReveal(posX!!, posY!!){
                        if (isEnabled) {
                            isEnabled = false
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        )

        setListenersOnCheckBoxes()

        observeSaveRecord()

        binding.etDateAndTime.pickDateTime()

        binding.btnSave.setOnClickListener{
            if(validate()){
                saveRecord(RecordDataModel(
                    binding.etBloodSugar.editableText.toString(),
                    getCheckedIndex(),
                    binding.etDateAndTime.editableText.toString(),
                    binding.etNote.editableText.toString()))
            }
        }

    }

    private fun EditText.pickDateTime() {

        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)
        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
        var pickedDateTime: Calendar
        this.setText(sdf.format(currentTime.time))

        setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, day ->
                TimePickerDialog(requireContext(), { _, hour, minute ->
                    pickedDateTime = Calendar.getInstance()
                    pickedDateTime.set(year, month, day, hour, minute)
                    if (pickedDateTime.timeInMillis <= currentDateTime.timeInMillis) {
                        this.setText(sdf.format(pickedDateTime.time))
                    } else {
                        showToast(requireContext(), getString(R.string.invalid_date_and_time))
                    }
                }, startHour, startMinute, false).show()
            }, startYear, startMonth, startDay).apply {
                datePicker.maxDate = currentDateTime.timeInMillis
            }.show()
        }

    }

    private fun updateCheckboxState(index: Int, isChecked: Boolean) {
        for (i in checkboxStates.indices) {
            checkboxStates[i] = i == index && isChecked
        }
        setCheckboxState()
    }

    private fun setCheckboxState() {

        removeListenersFromCheckboxes()

        binding.ivBreakfast.isChecked = checkboxStates[0]
        binding.ivLunch.isChecked = checkboxStates[1]
        binding.ivSnack.isChecked = checkboxStates[2]
        binding.ivDinner.isChecked = checkboxStates[3]
        binding.ivFasting.isChecked = checkboxStates[4]
        binding.ivRandom.isChecked = checkboxStates[5]
        binding.ivBeforeBed.isChecked = checkboxStates[6]

        setListenersOnCheckBoxes()
    }

    private fun setListenersOnCheckBoxes(){
        binding.ivBreakfast.setOnCheckedChangeListener { _, isChecked ->
            updateCheckboxState(0, isChecked)
        }
        binding.ivLunch.setOnCheckedChangeListener { _, isChecked ->
            updateCheckboxState(1, isChecked)
        }
        binding.ivSnack.setOnCheckedChangeListener { _, isChecked ->
            updateCheckboxState(2, isChecked)
        }
        binding.ivDinner.setOnCheckedChangeListener { _, isChecked ->
            updateCheckboxState(3, isChecked)
        }
        binding.ivFasting.setOnCheckedChangeListener { _, isChecked ->
            updateCheckboxState(4, isChecked)
        }
        binding.ivRandom.setOnCheckedChangeListener { _, isChecked ->
            updateCheckboxState(5, isChecked)
        }
        binding.ivBeforeBed.setOnCheckedChangeListener { _, isChecked ->
            updateCheckboxState(6, isChecked)
        }
    }

    private fun removeListenersFromCheckboxes(){

        binding.ivBreakfast.setOnCheckedChangeListener(null)
        binding.ivLunch.setOnCheckedChangeListener(null)
        binding.ivSnack.setOnCheckedChangeListener(null)
        binding.ivDinner.setOnCheckedChangeListener(null)
        binding.ivFasting.setOnCheckedChangeListener(null)
        binding.ivRandom.setOnCheckedChangeListener(null)
        binding.ivBeforeBed.setOnCheckedChangeListener(null)

    }


    private fun getCheckedIndex(): String {
        var index = -1
        for (i in checkboxStates.indices) {
            if (checkboxStates[i]) {
                index =  i
            }
        }

        when(index){
            0 -> return "Breakfast"
            1 -> return "Lunch"
            2 -> return "Snack"
            3 -> return "Dinner"
            4 -> return "Fasting"
            5 -> return "Random"
            6 -> return "Bed Time"
        }

        return ""

    }

    private fun saveRecord(recordData: RecordDataModel) {
        recordViewModel.saveRecord(recordData)
    }

    private fun observeSaveRecord() {

        recordViewModel.saveRecordStatus.observe(viewLifecycleOwner) { result ->
            result?.let {
                when (it) {
                    is NetworkResult.ResultOf.Success -> {
                        hideProgress()
                        if (it.value.equals(Constants.SAVE_RECORD_SUCCESS, ignoreCase = true)) {
                            showToast(requireContext(), getString(R.string.new_record_added))
                            recordViewModel.resetLoginLiveData()
                            navigateToHomeFragment()
                        } else if (!it.value.equals(Constants.RESET, ignoreCase = true)) {
                            showToast(
                                requireContext(),
                                String.format(
                                    getString(R.string.new_record_addition_failed), it.value)
                            )
                        }
                    }
                    is NetworkResult.ResultOf.Failure -> {
                        hideProgress()
                        val failedMessage = it.message ?: getString(R.string.unknown_error)
                        showToast(
                            requireContext(),
                            String.format(
                                getString(R.string.new_record_addition_failed), failedMessage)
                        )
                    }
                    is NetworkResult.ResultOf.Loading -> {
                        showProgress(requireContext())
                    }
                }
            }
        }

    }

    private fun navigateToHomeFragment(){
        findNavController().navigate(
            AddNewRecordFragmentDirections.actionAddNewRecordFragmentToHomeFragment())
    }

    private fun validate(): Boolean {
        if (binding.etBloodSugar.text.isNullOrEmpty()) {
            binding.etBloodSugar.error = getString(R.string.blood_sugar_value_validation)
            return false
        }

        return true
    }

}