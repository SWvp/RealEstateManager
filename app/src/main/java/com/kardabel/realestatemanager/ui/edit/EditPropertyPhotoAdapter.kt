package com.kardabel.realestatemanager.ui.edit

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

class EditPropertyPhotoAdapter(
    private val listener : (EditPropertyPhotoViewState) -> Unit
) : ListAdapter<EditPropertyPhotoViewState, EditPropertyPhotoAdapter.ViewHolder>(ListComparator) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, listener)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val description: TextView = itemView.findViewById(R.id.picture_description)
        private val photo: ImageView = itemView.findViewById(R.id.picture)

        fun bind(photoPhotoViewState: EditPropertyPhotoViewState, listener: (EditPropertyPhotoViewState) -> Unit ) {
            description.text = photoPhotoViewState.photoDescription
            Glide.with(photo.context).load(photoPhotoViewState.photoUri).into(photo)

            itemView.setOnClickListener {
                listener.invoke(photoPhotoViewState)
            }
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_details_picture, parent, false)
                return ViewHolder(view)
            }
        }
    }

    object ListComparator: DiffUtil.ItemCallback<EditPropertyPhotoViewState>() {
        override fun areItemsTheSame(oldItem: EditPropertyPhotoViewState, newItem: EditPropertyPhotoViewState): Boolean = oldItem === newItem

        override fun areContentsTheSame(oldItem: EditPropertyPhotoViewState, newItem: EditPropertyPhotoViewState): Boolean = oldItem == newItem
    }
}