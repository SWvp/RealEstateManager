package com.kardabel.realestatemanager.ui.properties

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.databinding.ActivityCreatePropertyBinding
import com.kardabel.realestatemanager.databinding.ItemDetailsPictureBinding
import com.kardabel.realestatemanager.ui.properties.PropertiesAdapter.*

class PropertiesAdapter(
    private val listener : (PropertyViewState) -> Unit
) : ListAdapter<PropertyViewState, ViewHolder>(ListComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, listener)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val type: TextView = itemView.findViewById(R.id.property_type)
        private val address: TextView = itemView.findViewById(R.id.property_place)
        private val price: TextView = itemView.findViewById(R.id.property_price)

        fun bind(property: PropertyViewState, listener: (PropertyViewState) -> Unit ) {
            type.text = property.type
            address.text = property.address
            price.text = property.price

            itemView.setOnClickListener {
                listener.invoke(property)
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

    object ListComparator: DiffUtil.ItemCallback<PropertyViewState>() {
        override fun areItemsTheSame(oldItem: PropertyViewState, newItem: PropertyViewState): Boolean = oldItem === newItem

        override fun areContentsTheSame(oldItem: PropertyViewState, newItem: PropertyViewState): Boolean = oldItem == newItem
    }

}