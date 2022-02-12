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
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.graphics.Bitmap
import android.util.Size
import com.example.yourstory.MainActivity
import com.example.yourstory.R
import com.example.yourstory.databinding.TakePictureFragmentCaptureModeBinding
import com.example.yourstory.databinding.TakePictureFragmentShowModeBinding
import android.graphics.PorterDuffXfermode
import android.graphics.RectF


class TakePictureFragment : Fragment(){

    private lateinit var viewModelShared: SharedThoughtDialogViewModel
    private lateinit var hostFragmentNavController: NavController
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var bindingCapture : TakePictureFragmentCaptureModeBinding? = null
    private var bindingShow : TakePictureFragmentShowModeBinding? = null

    private val screenSize = Size(720, 1280)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModelShared = ViewModelProvider(requireActivity())[SharedThoughtDialogViewModel::class.java]

        if (container != null) {
            hostFragmentNavController = NavHostFragment.findNavController(this)
        }

        if(viewModelShared.isInCaptureMode) {
            bindingCapture = TakePictureFragmentCaptureModeBinding.inflate(inflater)
            startCamera()

            // Set up the listener for take photo button
            bindingCapture!!.cameraCaptureButton.setOnClickListener {
                takePhoto()
            }
            cameraExecutor = Executors.newSingleThreadExecutor()

            return bindingCapture!!.root
        }
        bindingShow = TakePictureFragmentShowModeBinding.inflate(inflater)

        bindingShow!!.cancelThoughtDialogText.setOnClickListener{
            if(!viewModelShared.pictureIsTaken) {
                viewModelShared.image.postValue(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
                viewModelShared.isInCaptureMode = true
            }
            this.hostFragmentNavController.navigate(R.id.action_takePictureFragment_to_thought_dialog)
        }

        bindingShow!!.confirmThoughtDialogText.setOnClickListener{
            viewModelShared.pictureIsTaken = true
            this.hostFragmentNavController.navigate(R.id.action_takePictureFragment_to_thought_dialog)
        }

        bindingShow!!.retakePicture.setOnClickListener{
            viewModelShared.isInCaptureMode = true
            this.hostFragmentNavController.navigate(R.id.action_takePictureFragment_self)
        }

        viewModelShared.image.observe(viewLifecycleOwner, { image ->
            bindingShow!!.pictureCaptured.setImageBitmap(
                ImageHelper.getRoundedCornerBitmap(image,40)
            )
        })

        return bindingShow!!.root
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .setTargetResolution(screenSize)
                .build()
                .also {
                    it.setSurfaceProvider(bindingCapture!!.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setTargetResolution(screenSize)
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

                    if(image.imageInfo.rotationDegrees == 90){
                        bmp = rotateBitmap(bmp,90F)!!
                    }

                    viewModelShared.image.postValue(bmp)
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
        if(bindingCapture != null) {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onPause() {
        super.onPause()
        if(bindingCapture != null) {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(bindingCapture != null) {
            cameraExecutor.shutdown()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(bindingCapture == null){
            (requireActivity() as MainActivity).showBottomNav()
        }else{
            (requireActivity() as MainActivity).hideBottomNav()
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


    object ImageHelper {
        fun getRoundedCornerBitmap(bitmap: Bitmap, pixels: Int): Bitmap {
            val output = Bitmap.createBitmap(
                bitmap.width, bitmap
                    .height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            val rectF = RectF(rect)
            val roundPx = pixels.toFloat()
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)
            return output
        }
    }
}