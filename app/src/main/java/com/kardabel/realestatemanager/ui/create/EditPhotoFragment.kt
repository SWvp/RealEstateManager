package com.kardabel.realestatemanager.ui.create

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.kardabel.realestatemanager.model.Photo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPhotoFragment : DialogFragment() {

    interface ConfirmListener {
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }


    var photoViewState: CreatePropertyPhotoViewState? = null

    companion object {
        fun newInstance(propertyPhotoViewState: CreatePropertyPhotoViewState) =
            EditPhotoFragment().apply {
                photoViewState = propertyPhotoViewState
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
                    photoViewState?.let {
                        val photoToDelete= Photo(
                            photoBitmap = photoViewState!!.photoBitmap,
                            photoDescription = photoViewState!!.photoDescription,
                            photoUri = photoViewState!!.photoUri
                        )
                        viewModel.deletePhoto(photoToDelete) }
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