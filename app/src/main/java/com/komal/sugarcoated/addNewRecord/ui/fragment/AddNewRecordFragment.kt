package com.komal.sugarcoated.addNewRecord.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.komal.sugarcoated.databinding.FragmentAddNewRecordBinding

class AddNewRecordFragment : Fragment() {

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

//        binding.textView1.text = "FILES FRAGMENT"
    }

}