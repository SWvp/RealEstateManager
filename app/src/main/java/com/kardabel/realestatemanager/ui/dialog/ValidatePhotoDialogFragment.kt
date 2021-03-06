package com.kardabel.realestatemanager.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.ui.create.CreatePropertyPhotoViewState
import com.kardabel.realestatemanager.ui.edit.EditPropertyPhotoViewState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ValidatePhotoDialogFragment : DialogFragment() {

    var photoViewState: CreatePropertyPhotoViewState? = null
    var photoId: Int? = null
    var propertyTimestamp: String? = null
    var propertyOwnerId: Long? = null
    var propertyCreationDate: String? = null

    var isEditInstance: Boolean = false

    companion object {
        fun createPropertyActivityInstance(propertyPhotoViewState: CreatePropertyPhotoViewState) =
            ValidatePhotoDialogFragment().apply {
                photoViewState = propertyPhotoViewState
                isEditInstance = false
            }

        fun editPropertyActivityInstance(editPropertyPhotoViewState: EditPropertyPhotoViewState) =
            ValidatePhotoDialogFragment().apply {
                photoViewState = CreatePropertyPhotoViewState(
                    photoDescription = editPropertyPhotoViewState.photoDescription,
                    photoUri = editPropertyPhotoViewState.photoUri,
                    photoTimestamp = editPropertyPhotoViewState.photoTimestamp
                )
                photoId = editPropertyPhotoViewState.photoId
                propertyTimestamp = editPropertyPhotoViewState.photoTimestamp
                propertyOwnerId = editPropertyPhotoViewState.propertyOwnerId
                propertyCreationDate = editPropertyPhotoViewState.photoCreationDate

                isEditInstance = true
            }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val viewModel =
            ViewModelProvider(this)[ValidatePhotoDialogFragmentViewModel::class.java]
        val editText = EditText(requireContext())
        editText.setText(photoViewState?.photoDescription)
        builder
            .setMessage("Edit your photo")
            .setView(editText)
            .setPositiveButton("Delete") { _, _ ->

                if (isEditInstance) {
                    if (photoId != null) {
                        viewModel.deleteRegisteredPhotoFromRepository(photoId!!)
                    } else {
                        val photoToDelete = PhotoEntity(
                            photoDescription = photoViewState!!.photoDescription,
                            photoUri = photoViewState!!.photoUri,
                            propertyOwnerId = null,
                            photoTimestamp = photoViewState!!.photoTimestamp,
                            photoCreationDate = "will be set later",
                        )
                        viewModel.deletePhotoFromRepository(photoToDelete)
                    }
                } else {
                    photoViewState?.let {
                        val photoToDelete = PhotoEntity(
                            photoUri = photoViewState!!.photoUri,
                            photoDescription = photoViewState!!.photoDescription,
                            propertyOwnerId = null,
                            photoTimestamp = photoViewState!!.photoTimestamp,
                            photoCreationDate = "will be set later",
                        )
                        viewModel.deletePhotoFromRepository(photoToDelete)
                    }
                }
                dismiss()

            }

            .setNeutralButton("Edit description") { _, _ ->
                if (photoId != null) {
                    viewModel.updateRegisteredPhoto(
                        PhotoEntity(
                            photoUri = photoViewState!!.photoUri,
                            photoDescription = editText.text.toString(),
                            propertyOwnerId = propertyOwnerId,
                            photoTimestamp = propertyTimestamp!!,
                            photoCreationDate = propertyCreationDate!!,
                            photoId = photoId!!,
                        )
                    )
                }
                viewModel.editPhotoText(editText.text.toString(), photoViewState!!.photoUri)
                dismiss()
            }

            .setNegativeButton("Cancel") { _, _ ->
                dismiss()
            }

        return builder.create()
    }
}