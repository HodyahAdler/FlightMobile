package com.example.flightmobileapp
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.Image
import android.util.Patterns
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightmobileapp.db.Server
import com.example.flightmobileapp.db.ServerRepository
import kotlinx.coroutines.launch
import java.lang.Exception


class ServerViewModel(private val repository: ServerRepository, private val contextNew: Activity)
    : ViewModel(), Observable {

    val servers = repository.servers
    val context = contextNew

    @Bindable
    //hold the input value
    val inputName = MutableLiveData<String>()
    private val statusMessage = MutableLiveData<Event<String>>()

    val message: LiveData<Event<String>>
        get() = statusMessage

//check in the data base if we neet to update
    fun saveOrUpdate() {
 //   deletAll()
    //the input isnt empty
         if (inputName.value == null) {
             statusMessage.value = Event("Please enter url")
         } else {
             val list = servers.value
             var url1 = inputName.value!!
             if (!url1.endsWith("/")) {
                 var url2 = "$url1/"
                 url1 = url2
             }
             val date = System.currentTimeMillis()
             var curServer = Server(url1, date)
             //the url allready exsist
             if (list!!.find{ it.url == url1 } != null) {
                 update(curServer)
                 //the size of the DB is 5
             } else if (list?.size!! < 5) {
                 insert(curServer)
                 inputName.value = null
             } else if (list?.size!! >= 5) {
                 //delete the last one
                 deletLast()
                 insert(curServer)
                 inputName.value = null
             }
//check if the connect suscsses
             var connectToServer = ConnectToServer(url1, context)
             try{
                connectToServer.connectAndGetImageSeccsed(url1, context)
             }catch (e : Exception){
                 statusMessage.value = Event("we have error with community, try another url")
                 return
             }
         }
     }


//delete the last url from the repo
    fun deletLast()  = viewModelScope.launch  {
        val lastServer: Server = repository.getLastServer()
        repository.delete(lastServer)
    }
    //delete all
    fun deletAll()  = viewModelScope.launch  {
        repository.deleteAll()
    }
//insert new url
    fun insert(server: Server) = viewModelScope.launch {
        val newRowId = repository.insert(server)
        if (newRowId > -1) {
            statusMessage.value = Event("Server Inserted Successfully")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }
//update url the allready exsist
    fun update(server: Server) = viewModelScope.launch {
        val noOfRows = repository.update(server)
        if (noOfRows > 0) {
            inputName.value = null
           // statusMessage.value = Event("Row Updated Successfully")
        } else {
            statusMessage.value = Event("Error Occurred")
        }

    }
//delete one server
    fun delete(server: Server) = viewModelScope.launch {
        val noOfRowsDeleted = repository.delete(server)

        if (noOfRowsDeleted > 0) {
          //  statusMessage.value = Event("Row Deleted Successfully")
        } else {
            statusMessage.value = Event("Error Occurred")
        }

    }


//init the value
    fun initUpdateAndDelete(server: Server) {
        inputName.value = server.url
    }

//observable
    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
    //observable
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }


}