package com.example.yourstory.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yourstory.databinding.SettingsBackupFragmentBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.drive.DriveScopes

class SettingsBackupFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsBackupFragment()
    }

    private lateinit var viewModel: SettingsFragmentBackupViewModel
    private lateinit var binding: SettingsBackupFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SettingsBackupFragmentBinding.inflate(layoutInflater)

        requestSignIn()

        return binding.root
    }

    private fun requestSignIn() {
        var signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        var client = GoogleSignIn.getClient(requireActivity(),signInOptions)
        startActivityForResult(client.signInIntent,400)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[SettingsFragmentBackupViewModel::class.java]
        // TODO: Use the ViewModel
    }

    fun uploadDatabase(view: View){

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            400 -> {
                if(resultCode == RESULT_OK) {
                    handleSignInIntent(data)
                }
            }
        }
    }

    private fun handleSignInIntent(data: Intent?) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
            .addOnSuccessListener {

                var credential = GoogleAccountCredential.usingOAuth2(requireContext(),
                    setOf(DriveScopes.DRIVE_FILE)
                )
                credential.selectedAccount = it.account
                //var googleDriveService =
            }
            .addOnFailureListener{

            }
    }

}