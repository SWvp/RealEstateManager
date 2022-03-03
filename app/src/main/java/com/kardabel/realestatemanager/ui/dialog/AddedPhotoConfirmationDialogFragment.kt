package com.kardabel.realestatemanager.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.kardabel.realestatemanager.model.Photo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddedPhotoConfirmationDialogFragment : DialogFragment() {

    interface ConfirmDeleteListener {
        fun onDialogNegativeClick()
    }

    var photoUri: String? = null

    companion object {
        fun newInstance(uriString: String) = AddedPhotoConfirmationDialogFragment().apply {
            photoUri = uriString
        }
    }

    var listener: ConfirmDeleteListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val viewModel =
            ViewModelProvider(this)[AddedPhotoConfirmationDialogFragmentViewModel::class.java]
        val editText = EditText(requireContext())
        builder
            .setMessage("Enter description")
            .setView(editText)
            .setPositiveButton("Validate") { _, _ ->
                if (!editText.text.isNullOrEmpty()) {
                    val newPhoto = Photo(
                        //photoBitmap = photoBitmap!!,
                        photoDescription = editText.text.toString(),
                        photoUri = photoUri!!,

                        )
                    viewModel.addPhoto(newPhoto)
                }
                dismiss()
            }
            .setNegativeButton("Cancel") { _, _ ->
                listener?.onDialogNegativeClick()
            }
        return builder.create()
    }
}