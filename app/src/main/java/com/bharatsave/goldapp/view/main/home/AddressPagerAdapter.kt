package com.bharatsave.goldapp.view.main.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class AddressPagerAdapter(fragment: Fragment, private val purpose: AddressBottomSheetPurpose) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AddressSelectionFragment.newInstance(purpose)
            1 -> NewAddressFragment()
            else -> throw IndexOutOfBoundsException("Page position out of index range")
        }
    }
}