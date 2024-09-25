package com.aashay.fetch_exercise_demo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetMenuDialog(
    var selectedOption: Int,
    var hideNullName: Boolean,
    private val onApply: (Int, Boolean) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_menu, container, false)

        val radioGroup: RadioGroup = view.findViewById(R.id.radio_group)
        val checkbox: CheckBox = view.findViewById(R.id.checkbox_hide_null_name)

        radioGroup.check(selectedOption)
        checkbox.isChecked = hideNullName

        view.findViewById<Button>(R.id.btn_apply).setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            val hideNull = checkbox.isChecked
            onApply(selectedId, hideNull)
            dismiss()
        }

        view.findViewById<Button>(R.id.btn_reset).setOnClickListener {
            val selectedId = -1
            val hideNull = false
            onApply(selectedId, hideNull)
            dismiss()
        }

        view.findViewById<Button>(R.id.btn_me).setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}


