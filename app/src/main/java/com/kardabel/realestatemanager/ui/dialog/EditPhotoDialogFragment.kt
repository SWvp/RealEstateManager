package com.kardabel.realestatemanager.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.kardabel.realestatemanager.model.Photo
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.ui.create.CreatePropertyPhotoViewState
import com.kardabel.realestatemanager.ui.edit.EditPropertyPhotoViewState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPhotoDialogFragment : DialogFragment() {

    var photoViewState: CreatePropertyPhotoViewState? = null
    var photoId: Int? = null
    var propertyOwnerId: Long? = null
    var isEditInstance: Boolean = false

    companion object {
        fun createInstance(propertyPhotoViewState: CreatePropertyPhotoViewState) =
            EditPhotoDialogFragment().apply {
                photoViewState = propertyPhotoViewState
                isEditInstance = false
            }

        fun editInstance(editPropertyPhotoViewState: EditPropertyPhotoViewState) =
            EditPhotoDialogFragment().apply {
                photoViewState = CreatePropertyPhotoViewState(
                    //photoBitmap = editPropertyPhotoViewState.photoBitmap,
                    photoDescription = editPropertyPhotoViewState.photoDescription,
                    photoUri = editPropertyPhotoViewState.photoUri,
                )
                photoId = editPropertyPhotoViewState.photoId
                propertyOwnerId = editPropertyPhotoViewState.propertyOwnerId
                isEditInstance = true
            }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val viewModel =
            ViewModelProvider(this)[EditPhotoDialogFragmentViewModel::class.java]
        val editText = EditText(requireContext())
        editText.setText(photoViewState?.photoDescription)
        builder
            .setMessage("Edit your photo")
            .setView(editText)
            .setPositiveButton("Delete") { dialog, id ->

                if (isEditInstance) {
                    if (photoId != null) {
                        viewModel.deleteRegisteredPhotoFromRepository(photoId!!)
                    } else {
                        val photoToDelete = Photo(
                            //photoBitmap = photoViewState!!.photoBitmap,
                            photoDescription = photoViewState!!.photoDescription,
                            photoUri = photoViewState!!.photoUri,
                        )
                        viewModel.deletePhotoFromRepository(photoToDelete)
                    }
                } else {
                    photoViewState?.let {
                        val photoToDelete = Photo(
                            //photoBitmap = photoViewState!!.photoBitmap,
                            photoDescription = photoViewState!!.photoDescription,
                            photoUri = photoViewState!!.photoUri,
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
                            //photoViewState!!.photoBitmap,
                            photoViewState!!.photoUri,
                            editText.text.toString(),
                            propertyOwnerId,
                            photoId!!,
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