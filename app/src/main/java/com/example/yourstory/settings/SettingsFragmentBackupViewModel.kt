package com.example.yourstory.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourstory.database.Repository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingsFragmentBackupViewModel(application: Application) : AndroidViewModel(application)  {

    var client: GoogleSignInClient? = null
    lateinit var repository: Repository

    init{
        repository = Repository(application)
    }

    fun uploadDataBase(){
        viewModelScope.launch (Dispatchers.IO){
            repository.uploadDataBaseToDrive()
        }
    }

    fun initDriveStructure() {
    }

    fun getGoogleDisplayName(): String? {
        return repository.googleAccount.displayName
    }

    fun getGoogleEmail(): String? {
        return repository.googleAccount.email
    }

}