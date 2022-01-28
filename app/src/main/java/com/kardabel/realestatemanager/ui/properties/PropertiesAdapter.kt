package com.kardabel.realestatemanager.ui.properties

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.ui.properties.PropertiesAdapter.*

class PropertiesAdapter : ListAdapter<PropertyViewState, ViewHolder>(ListComparator) {

    private var onItemClicked: OnItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, onItemClicked)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val type: TextView = itemView.findViewById(R.id.property_type)
        private val place: TextView = itemView.findViewById(R.id.property_place)
        private val price: TextView = itemView.findViewById(R.id.property_price)

        fun bind(property: PropertyViewState, onItemClicked: OnItemClickListener?) {
            type.text = property.type
            place.text = property.place
            price.text = property.price

            itemView.setOnClickListener {
                onItemClicked!!.onItemClicked(property)
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
        override fun areItemsTheSame(oldItem: PropertyViewState, newItem: PropertyViewState): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PropertyViewState, newItem: PropertyViewState): Boolean {
            return oldItem == newItem
        }
    }

    fun setOnItemClickedListener(listener: OnItemClickListener) {
        onItemClicked = listener
    }

    interface OnItemClickListener {
        fun onItemClicked(property: PropertyViewState)
    }

}