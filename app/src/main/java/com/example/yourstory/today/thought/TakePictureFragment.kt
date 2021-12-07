package com.example.yourstory.today.thought

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.yourstory.R
import com.example.yourstory.databinding.TakePictureFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.nio.ByteBuffer

class TakePictureFragment : Fragment(){

    private lateinit var viewModelShared: SharedThoughtDialogViewModel
    private lateinit var hostFragmentNavController: NavController
    private var _binding: TakePictureFragmentBinding? = null
    private lateinit var imageView: ImageView
    private lateinit var imageBitmap: Bitmap
    private val binding get() = _binding!!
    lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TakePictureFragmentBinding.inflate(inflater, container, false)
        hostFragmentNavController = NavHostFragment.findNavController(this)

        viewModelShared = ViewModelProvider(requireActivity())[SharedThoughtDialogViewModel::class.java]

        //Get the ImageView
        imageView = _binding!!.pictureView

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

        //Setup Confirm-Button
        binding.confirmThoughtDialogPicture.setOnClickListener {
            if(!viewModelShared.picSelected.value!!){
                materialAlertDialogBuilder.setTitle(R.string.take_a_picture_title)
                materialAlertDialogBuilder.setMessage(R.string.take_a_picture_text)
                materialAlertDialogBuilder.setPositiveButton("OK"){
                    dialog, which ->
                }
                materialAlertDialogBuilder.show()
            }else {
                viewModelShared.image = viewModelShared.pictureImage
                hostFragmentNavController.navigate(R.id.action_takePictureFragment_to_thought_dialog)
            }
        }

        binding.takeNewPictureButton.setOnClickListener{
            dispatchTakePictureIntent()
        }

        binding.cancelThoughtDialogPicture.setOnClickListener {
            hostFragmentNavController.navigate(R.id.action_takePictureFragment_to_thought_dialog)
        }

        viewModelShared.pictureImage.observe(viewLifecycleOwner,{
            newBitmap -> imageView.setImageBitmap(newBitmap)
        })

        return binding.root
    }
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageBitmap = data!!.extras!!.get("data") as Bitmap
            viewModelShared.picSelected.value = true
            viewModelShared.pictureImage.value = imageBitmap
        }
    }


    /**
     * Convert bitmap to byte array using ByteBuffer.
     */
    fun Bitmap.convertToByteArray(): ByteArray {
        //minimum number of bytes that can be used to store this bitmap's pixels
        val size = this.byteCount

        //allocate new instances which will hold bitmap
        val buffer = ByteBuffer.allocate(size)
        val bytes = ByteArray(size)

        //copy the bitmap's pixels into the specified buffer
        this.copyPixelsToBuffer(buffer)

        //rewinds buffer (buffer position is set to zero and the mark is discarded)
        buffer.rewind()

        //transfer bytes from buffer into the given destination array
        buffer.get(bytes)

        //return bitmap's pixels
        return bytes
    }
}