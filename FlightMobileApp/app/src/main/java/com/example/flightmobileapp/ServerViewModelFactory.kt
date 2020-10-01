package com.example.flightmobileapp
import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flightmobileapp.db.ServerRepository
import java.lang.IllegalArgumentException
//make the view model from the main activity
class ServerViewModelFactory
    (private val repository: ServerRepository, private val context: Activity)
    :ViewModelProvider.Factory {
    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
     if(modelClass.isAssignableFrom(ServerViewModel::class.java)){
         return ServerViewModel(repository, context) as T
     }
        throw IllegalArgumentException("Unknown View Model class")
    }
}