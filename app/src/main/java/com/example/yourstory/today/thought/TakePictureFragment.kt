package com.example.yourstory.today.thought

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.pm.ActivityInfo
import android.graphics.*
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.graphics.Bitmap
import androidx.camera.core.AspectRatio.RATIO_16_9
import androidx.navigation.findNavController
import com.example.yourstory.MainActivity
import com.example.yourstory.R
import com.example.yourstory.databinding.TakePictureFragmentCaptureModeBinding
import com.example.yourstory.databinding.TakePictureFragmentShowModeBinding


class TakePictureFragment : Fragment(){

    private lateinit var hostFramentNavController: NavController
    private lateinit var binding_show: TakePictureFragmentShowModeBinding
    private lateinit var viewModelShared: SharedThoughtDialogViewModel
    private lateinit var hostFragmentNavController: NavController
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var binding_capture: TakePictureFragmentCaptureModeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelShared = ViewModelProvider(requireActivity())[SharedThoughtDialogViewModel::class.java]

        if (container != null) {
            hostFramentNavController = container.findNavController()
        }

        if(viewModelShared.isInCaptureMode) {
            binding_capture = TakePictureFragmentCaptureModeBinding.inflate(inflater)
            hostFragmentNavController = NavHostFragment.findNavController(this)

            startCamera();

            // Set up the listener for take photo button
            binding_capture.cameraCaptureButton.setOnClickListener {
                takePhoto();
            }
            cameraExecutor = Executors.newSingleThreadExecutor()

            return binding_capture.root
        }
        binding_show = TakePictureFragmentShowModeBinding.inflate(inflater)

        binding_show.cancelThoughtDialogText.setOnClickListener{
            viewModelShared.isInCaptureMode = true
            requireActivity().onBackPressed()
        }

        binding_show.confirmThoughtDialogText.setOnClickListener{
            hostFragmentNavController.navigate(R.id.takePictureFragment)
        }

        viewModelShared.image.observe(viewLifecycleOwner, { image ->
            binding_show.pictureCaptured.setImageBitmap(image)
        })

        (requireActivity() as MainActivity).showBottomNav()

        return binding_show.root
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .setTargetAspectRatio(RATIO_16_9)
                .build()
                .also {
                    it.setSurfaceProvider(binding_capture.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(RATIO_16_9)
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return



        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
           ContextCompat.getMainExecutor(requireContext()), object :
                ImageCapture.OnImageCapturedCallback() {

                @SuppressLint("UnsafeOptInUsageError")
                override fun onCaptureSuccess(image: ImageProxy) {
                    var bmp = imageProxyToBitmap(image)
                    viewModelShared.image.postValue(rotateBitmap(bmp,90F))
                    viewModelShared.isInCaptureMode = false
                    hostFragmentNavController.navigate(R.id.takePictureFragment)
                }
                override fun onError(error: ImageCaptureException)
                {
                    // insert your code here.
                }
           })

    }

    override fun onResume() {
        super.onResume()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onPause() {
        super.onPause()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR;
    }

    override fun onDestroy() {
        super.onDestroy()
        if(binding_capture != null) {
            cameraExecutor.shutdown()
        }
    }

    /**
     *  convert image proxy to bitmap
     *  @param image
     */
    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}