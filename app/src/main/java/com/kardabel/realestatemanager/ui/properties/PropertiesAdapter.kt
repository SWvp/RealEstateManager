package com.kardabel.realestatemanager.ui.properties

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.ui.properties.PropertiesAdapter.ViewHolder


class PropertiesAdapter(
    private val listener: (propertyId: Long) -> Unit
) : ListAdapter<PropertyViewState, ViewHolder>(ListComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val type: TextView = itemView.findViewById(R.id.item_property_type)
        private val county: TextView = itemView.findViewById(R.id.item_property_county)
        private val price: TextView = itemView.findViewById(R.id.item_property_price)
        private val saleStatus: TextView = itemView.findViewById(R.id.item_property_sale_status)
        private val vendor: TextView = itemView.findViewById(R.id.item_property_vendor)
        private val photo: ImageView = itemView.findViewById(R.id.item_property_photo)


        fun bind(propertyViewState: PropertyViewState, listener: (Long) -> Unit) {

            type.text = propertyViewState.type
            county.text = propertyViewState.county
            price.text = propertyViewState.price
            saleStatus.text = propertyViewState.saleStatus
            saleStatus.setBackgroundColor(propertyViewState.saleColor)
            vendor.text = propertyViewState.vendor

            Glide
                .with(photo.context)
                .load(propertyViewState.photoUri.toString())
                .into(photo)


            itemView.setOnClickListener {
                listener.invoke(propertyViewState.propertyId)
            }
        }


        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_property, parent, false)
                return ViewHolder(view)
            }
        }
    }

    object ListComparator : DiffUtil.ItemCallback<PropertyViewState>() {
        override fun areItemsTheSame(
            oldItem: PropertyViewState,
            newItem: PropertyViewState
        ): Boolean = oldItem === newItem

        override fun areContentsTheSame(
            oldItem: PropertyViewState,
            newItem: PropertyViewState
        ): Boolean = oldItem == newItem
    }
}