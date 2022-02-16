package com.kardabel.realestatemanager.ui.create

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.kardabel.realestatemanager.model.Photo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoConfirmationFragment : DialogFragment() {

    interface ConfirmDeleteListener {
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }

    var photoBitmap: Bitmap? = null
    var photoUri: Uri? = null

    companion object {
        fun newInstance(photo: Bitmap, uri: Uri) = PhotoConfirmationFragment().apply {
            photoBitmap = photo
            photoUri = uri
        }
    }

    var listener: ConfirmDeleteListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val viewModel =
            ViewModelProvider(this)[PhotoConfirmationFragmentViewModel::class.java]
        val editText = EditText(requireContext())
        builder
            .setMessage("Enter description")
            .setView(editText)
            .setPositiveButton("Validate", DialogInterface.OnClickListener { dialog, id ->
                if (!editText.text.isNullOrEmpty()) {
                    val newPhoto = Photo(
                        photoBitmap = photoBitmap!!,
                        photoDescription = editText.text.toString(),
                        photoUri = photoUri!!,

                    )
                    viewModel.addPhoto(newPhoto)
                }
                dismiss()
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                listener?.onDialogNegativeClick()
            })
        return builder.create()
    }
}