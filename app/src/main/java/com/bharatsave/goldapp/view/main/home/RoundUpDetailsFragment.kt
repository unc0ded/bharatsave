package com.bharatsave.goldapp.view.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentRoundUpDetailsBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

class RoundUpDetailsFragment : Fragment() {

    private var _binding: FragmentRoundUpDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var datePicker: MaterialDatePicker<Long>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"))
        calendar.timeInMillis = MaterialDatePicker.todayInUtcMilliseconds()

        val constraintBuilder = CalendarConstraints.Builder().setValidator(
            DateValidatorPointForward.from(
                LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.of("+05:30"))
                    .toEpochMilli()
            )
        )
        // TODO theme datePicker
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select end date")
            .setCalendarConstraints(constraintBuilder.build())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            (binding.datePickerEndDate.editText as MaterialAutoCompleteTextView).setText(
                LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.of("Asia/Kolkata")).format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                ), false
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoundUpDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        (binding.menuRoundUpAmount.editText as MaterialAutoCompleteTextView).setText(
            "Hundred",
            false
        )

        // TODO theme exposed dropdown menus
        val items = listOf("Hundred", "Fifty", "Ten")
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu_item, items)
        (binding.menuRoundUpAmount.editText as MaterialAutoCompleteTextView).setAdapter(adapter)

        val goalAdapter =
            ArrayAdapter(
                requireContext(),
                R.layout.dropdown_menu_item,
                listOf("Education", "Wedding", "Vacation")
            )
        (binding.menuChooseGoal.editText as MaterialAutoCompleteTextView).setAdapter(goalAdapter)

        binding.btnContinue.setOnClickListener {
            if ((binding.datePickerEndDate.editText as MaterialAutoCompleteTextView).text.toString()
                    .isNotBlank() && (binding.menuRoundUpAmount.editText as MaterialAutoCompleteTextView).text.toString()
                    .isNotBlank() && (binding.menuChooseGoal.editText as MaterialAutoCompleteTextView).text.toString()
                    .isNotBlank()
            ) {
                // TODO save plan
                findNavController().navigate(RoundUpDetailsFragmentDirections.actionFinishPlan())
                Toast.makeText(context, "New plan created", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(context, "All fields required", Toast.LENGTH_SHORT).show()
        }

        binding.datePickerEndDate.setEndIconOnClickListener {
            datePicker.show(parentFragmentManager, "End Date")
        }
        (binding.datePickerEndDate.editText as MaterialAutoCompleteTextView).setOnClickListener {
            datePicker.show(
                parentFragmentManager,
                "End Date"
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}