package com.kardabel.realestatemanager.ui.create

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.kardabel.realestatemanager.model.Photo
import com.kardabel.realestatemanager.ui.edit.EditPropertyPhotoViewState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPhotoFragment : DialogFragment() {

    interface ConfirmListener {
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }


    var photoViewState: CreatePropertyPhotoViewState? = null
    var photoId: Int? = null
    var isEditInstance: Boolean = false

    companion object {
        fun createInstance(propertyPhotoViewState: CreatePropertyPhotoViewState) =
            EditPhotoFragment().apply {
                photoViewState = propertyPhotoViewState
                isEditInstance = false
            }

        fun editInstance(editPropertyPhotoViewState: EditPropertyPhotoViewState) =
            EditPhotoFragment().apply {
                photoViewState = CreatePropertyPhotoViewState(
                    photoBitmap = editPropertyPhotoViewState.photoBitmap,
                    photoDescription = editPropertyPhotoViewState.photoDescription,
                    photoUri = Uri.parse(editPropertyPhotoViewState.photoUri),
                )
                photoId = editPropertyPhotoViewState.photoId
                isEditInstance = true
            }

    }


    var listener: ConfirmListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val viewModel =
            ViewModelProvider(this)[EditPhotoFragmentViewModel::class.java]
        val editText = EditText(requireContext())
        editText.setText(photoViewState?.photoDescription)
        builder
            .setMessage("Edit your photo")
            .setView(editText)
            .setPositiveButton("Delete", DialogInterface.OnClickListener { dialog, id ->

                if (isEditInstance) {
                    if(photoId != null){
                        viewModel.deletePhotoFromDataBase(photoId!!)
                        viewModel.deletePhotoEntityFromRepository(photoId!!)
                    }else {
                        val photoToDelete = Photo(
                            photoBitmap = photoViewState!!.photoBitmap,
                            photoDescription = photoViewState!!.photoDescription,
                            photoUri = photoViewState!!.photoUri,
                        )
                        viewModel.deletePhotoFromRepository(photoToDelete)
                    }
                }else{
                    photoViewState?.let {
                        val photoToDelete = Photo(
                            photoBitmap = photoViewState!!.photoBitmap,
                            photoDescription = photoViewState!!.photoDescription,
                            photoUri = photoViewState!!.photoUri,
                        )
                        viewModel.deletePhotoFromRepository(photoToDelete)
                    }
                }
                dismiss()

            })

            .setNeutralButton("Edit description", DialogInterface.OnClickListener { dialog, id ->
                viewModel.editPhotoText(editText.text.toString(), photoViewState!!.photoBitmap)
                dismiss()
            })

            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                dismiss()
            })





        return builder.create()
    }
}