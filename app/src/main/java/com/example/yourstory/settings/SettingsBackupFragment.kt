package com.example.yourstory.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.yourstory.R
import com.example.yourstory.database.Repository
import com.example.yourstory.database.Repository.Companion.googleDriveService
import com.example.yourstory.databinding.SettingsBackupLoggedInFragmentBinding
import com.example.yourstory.databinding.SettingsBackupNotLoggedInFragmentBinding
import com.fasterxml.jackson.core.JsonFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import java.io.File

class SettingsBackupFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsBackupFragment()
    }

    private lateinit var hostFramentNavController: NavController
    private lateinit var viewModel: SettingsFragmentBackupViewModel
    private lateinit var binding_not_logged_in: SettingsBackupNotLoggedInFragmentBinding
    private lateinit var binding_logged_in: SettingsBackupLoggedInFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[SettingsFragmentBackupViewModel::class.java]


        if (container != null) {
            hostFramentNavController = container.findNavController()
        }

        if(viewModel.repository.googleAccount == null){
            binding_not_logged_in = SettingsBackupNotLoggedInFragmentBinding.inflate(layoutInflater)

            binding_not_logged_in.signInButton.setOnClickListener{
                requestSignIn()
            }

            binding_not_logged_in.signInButton.setSize(SignInButton.SIZE_WIDE)
            (binding_not_logged_in.signInButton.getChildAt(0) as TextView).text = getString(R.string.login_with_google)
            (binding_not_logged_in.signInButton.getChildAt(0) as TextView).setTextColor(Color.GRAY)

            return binding_not_logged_in.root
        }
        binding_logged_in = SettingsBackupLoggedInFragmentBinding.inflate(layoutInflater)
        binding_logged_in.textViewName.text = viewModel.getGoogleDisplayName()
        binding_logged_in.textViewEmail.text = viewModel.getGoogleEmail()

        initDriveServiceWithGoogleAccount()

        //viewModel.initAppFolder()
        viewModel.uploadDataBase()

        return binding_logged_in.root
    }

    private fun initDriveServiceWithGoogleAccount() {
        var credential = GoogleAccountCredential.usingOAuth2(requireActivity(), setOf(DriveScopes.DRIVE_FILE))
        credential.selectedAccount = viewModel.repository.googleAccount.account

        Repository.googleDriveService = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            credential).setApplicationName("Your Story").build()
    }

    private fun requestSignIn() {
        var signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .requestEmail()
            .build()

        var client = GoogleSignIn.getClient(requireActivity(),signInOptions)

        startActivityForResult(client.signInIntent,400)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // TODO: Use the ViewModel
    }

    fun uploadDatabase(view: View){

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            400 -> {
                if(resultCode == RESULT_OK){
                    handleSignInIntent(data)
                }
            }
        }
    }

    private fun handleSignInIntent(data: Intent?) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
            .addOnSuccessListener {

                viewModel.repository.googleAccount = it
                hostFramentNavController.navigate(R.id.settingsFragmentBackup)
            }
            .addOnFailureListener{
                this.requireView().invalidate()
            }
    }

}