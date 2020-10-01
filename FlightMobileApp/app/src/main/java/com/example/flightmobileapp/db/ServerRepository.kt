package com.example.flightmobileapp.db
import androidx.lifecycle.LiveData
import com.example.flightmobileapp.dbimport.ServerDAO
//connect the view model to the DB
class ServerRepository(private val dao : ServerDAO) {
//holt the servers
    val servers = dao.getAllServer()
//init new server
    suspend fun insert(server: Server): Long {
        return dao.insertServer(server)
    }
//update server
    suspend fun update(server: Server): Int {
        return dao.updateServerDate(server)
    }
//delete server
    suspend fun delete(server: Server): Int {
        return dao.deleteServer(server)
    }

    suspend fun deleteAll(): Int {
        return dao.deleteAll()
    }
//get numbers of rows
    suspend fun getNum(): Int {
        return dao.getNumRow()
    }
//hold all the servers
    suspend fun getAllServer(): LiveData<List<Server>> {
        return dao.getAllServer()
    }
//get the last that we used
    suspend fun getLastServer(): Server {
        return dao.getLastServer()
    }

}