package com.bharatsave.goldapp.view.main.home

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bharatsave.goldapp.custom_widget.CheckableAddressItem
import com.bharatsave.goldapp.model.AddressDetail
import com.bharatsave.goldapp.util.toPx

class AddressListAdapter(private val addressList: List<AddressDetail>) :
    RecyclerView.Adapter<AddressListAdapter.AddressViewHolder>() {

    private var lastCheckedPosition = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressListAdapter.AddressViewHolder {
        val addressItemView = CheckableAddressItem(parent.context)
        addressItemView.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(10.toPx.toInt(), 10.toPx.toInt(), 10.toPx.toInt(), 0)
        }
        return AddressViewHolder(addressItemView)
    }

    override fun onBindViewHolder(holder: AddressListAdapter.AddressViewHolder, position: Int) {
        holder.bind(addressList[position])
        (holder.itemView as CheckableAddressItem).isChecked = position == lastCheckedPosition
    }

    override fun getItemCount(): Int = addressList.size

    inner class AddressViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal fun bind(addressDetail: AddressDetail) {
            (itemView as CheckableAddressItem).run {
                setAddress(
                    addressDetail.name,
                    "${addressDetail.address}, ${addressDetail.city}, ${addressDetail.state}",
                    addressDetail.label,
                    addressDetail.pincode
                )
                setOnClickListener {
                    val copyOfLastCheckedPosition = lastCheckedPosition
                    lastCheckedPosition = adapterPosition
                    if (lastCheckedPosition != copyOfLastCheckedPosition) {
                        notifyItemChanged(copyOfLastCheckedPosition)
                        notifyItemChanged(lastCheckedPosition)
                    }
                }
            }
        }
    }

    fun getCheckedAddressId(): String =
        if (addressList.isNotEmpty()) addressList[lastCheckedPosition].id else ""
}