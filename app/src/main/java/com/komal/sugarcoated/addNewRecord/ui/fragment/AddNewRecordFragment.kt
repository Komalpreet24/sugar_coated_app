package com.komal.sugarcoated.addNewRecord.ui.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.komal.sugarcoated.databinding.FragmentAddNewRecordBinding
import com.komal.sugarcoated.utils.ExitWithAnimation
import com.komal.sugarcoated.utils.exitCircularReveal
import com.komal.sugarcoated.utils.startCircularReveal
import java.text.SimpleDateFormat
import java.util.*


class AddNewRecordFragment : Fragment(), ExitWithAnimation {

    override var posX: Int? = null
    override var posY: Int? = null

    override fun isToBeExitedWithAnimation(): Boolean = true

    lateinit var binding: FragmentAddNewRecordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNewRecordBinding.inflate(layoutInflater)
        return binding.root
    }

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

//        binding.etDateAndTime.transformIntoDatePicker(requireContext(), "MM/dd/yyyy", Date())
        binding.etDateAndTime.pickDateTime()

    }

//    fun EditText.transformIntoDatePicker(context: Context, format: String, maxDate: Date? = null) {
//        isFocusableInTouchMode = false
//        isClickable = true
//        isFocusable = false
//
//        val myCalendar = Calendar.getInstance()
//        val datePickerOnDataSetListener =
//            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
//                myCalendar.set(Calendar.YEAR, year)
//                myCalendar.set(Calendar.MONTH, monthOfYear)
//                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//                val sdf = SimpleDateFormat(format, Locale.UK)
//                setText(sdf.format(myCalendar.time))
//            }
//
//        setOnClickListener {
//            DatePickerDialog(
//                context, datePickerOnDataSetListener, myCalendar
//                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                myCalendar.get(Calendar.DAY_OF_MONTH)
//            ).run {
//                maxDate?.time?.also { datePicker.maxDate = it }
//                show()
//            }
//        }
//    }

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
        val sdf = SimpleDateFormat("dd/mm/yy hh:mm", Locale.UK)
        this.setText(sdf.format(currentTime.time))

        setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, day ->
                TimePickerDialog(requireContext(), { _, hour, minute ->
                    val pickedDateTime = Calendar.getInstance()
                    pickedDateTime.set(year, month, day, hour, minute)
                    this.setText(sdf.format(pickedDateTime.time))
                }, startHour, startMinute, false).show()
            }, startYear, startMonth, startDay).show()
        }
    }

}