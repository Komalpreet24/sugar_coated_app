package com.komal.sugarcoated.addNewRecord.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.komal.sugarcoated.R
import com.komal.sugarcoated.databinding.FragmentAddNewRecordBinding
import com.komal.sugarcoated.utils.ExitWithAnimation
import com.komal.sugarcoated.utils.exitCircularReveal
import com.komal.sugarcoated.utils.startCircularReveal


class AddNewRecordFragment : Fragment(), ExitWithAnimation {

    override var posX: Int? = null
    override var posY: Int? = null

    override fun isToBeExitedWithAnimation(): Boolean = true

    lateinit var binding: FragmentAddNewRecordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        val backStackEntry = findNavController().currentBackStackEntry

        Log.d("backStackEntry", backStackEntry.toString())



        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d("TAG", "Fragment back pressed invoked")
                    isEnabled = true
                    view.exitCircularReveal(posX!!, posY!!){
                      Log.d("TAG", "Pop stack ")
                        if (isEnabled) {
                            isEnabled = false
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        )

    }
}