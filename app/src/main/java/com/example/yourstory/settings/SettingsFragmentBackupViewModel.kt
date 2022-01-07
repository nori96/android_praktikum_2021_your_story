package com.example.yourstory.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.yourstory.database.Database
import com.example.yourstory.database.Repository
import com.example.yourstory.database.data.DiaryEntry
import com.example.yourstory.utils.DateEpochConverter
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager
import com.google.api.services.drive.model.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingsFragmentBackupViewModel(application: Application) : AndroidViewModel(application) {

    var repository: Repository
    var latestDBMetadata = MutableLiveData<File>(null)
    var latestDBFile = MutableLiveData<java.io.File>(null)

    init {
        repository = Repository(application)

        viewModelScope.launch(Dispatchers.IO) {

            if (Repository.googleDriveService != null) {
               // latestDBMetadata.postValue(repository.getLatestDBMetadata())
            }
        }
    }

    fun downloadDatabase(){
        viewModelScope.launch(Dispatchers.IO) {
            if(!repository.checkIfDataBaseExist()){
                return@launch
            }
            latestDBFile.postValue(repository.downloadLatestDB(latestDBMetadata.value!!.id))
        }
    }

    fun uploadDataBase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.uploadDataBaseToDrive()
            latestDBMetadata.postValue(repository.getLatestDBMetadata())
        }
    }

    fun setGoogleAccountAndInitDrive(googleSignInAccount: GoogleSignInAccount) {
        repository.signInToGoogle(googleSignInAccount)
    }

    fun getGoogleDisplayName(): String? {
        return repository.getGoogleAccount()?.displayName
    }

    fun getGoogleEmail(): String? {
        return repository.getGoogleAccount()?.email
    }
}

