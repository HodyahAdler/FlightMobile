package com.example.flightmobileapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flightmobileapp.databinding.ActivityConnectScreenBinding
import com.example.flightmobileapp.db.Server
import com.example.flightmobileapp.db.ServerDatabase
import com.example.flightmobileapp.db.ServerRepository

//connect to server + data base
class ConnectScreenActivity: AppCompatActivity() {
    private lateinit var binding: ActivityConnectScreenBinding
    private lateinit var serverViewModel: ServerViewModel
    private lateinit var adapter: MyRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ///use binding
        binding = DataBindingUtil.setContentView(this,R.layout.activity_connect_screen)
        val dao = ServerDatabase.getInstance(application).serverDAO
        val repository = ServerRepository(dao)
        val factory = ServerViewModelFactory(repository,this)
        serverViewModel = ViewModelProvider(this,factory).get(ServerViewModel::class.java)
        binding.myViewModel = serverViewModel
        binding.lifecycleOwner = this
        initRecyclerView()

        serverViewModel.message.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this,it,Toast.LENGTH_LONG).show()
            }
        })

    }
//get the data from the d.b
    private fun initRecyclerView(){
        binding.subscriberRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyRecyclerViewAdapter({selectedItem: Server->listItemClicked(selectedItem)})
        binding.subscriberRecyclerView.adapter = adapter
        displaySubscribersList()
    }
//update the d.b
    private fun displaySubscribersList(){
        serverViewModel.servers.observe(this, Observer {
           // Log.i("MYTAG",it.toString())
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }
//use click on the url list
    private fun listItemClicked(subscriber: Server){
        serverViewModel.initUpdateAndDelete(subscriber)
    }

}
