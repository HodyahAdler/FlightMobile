package com.example.flightmobileapp.dbimport
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.flightmobileapp.db.Server

@Dao
interface ServerDAO {

    @Insert
    suspend fun insertServer(server: Server) : Long

    @Update
    suspend fun updateServerDate(server: Server) : Int

    @Delete
    suspend fun deleteServer(server: Server) : Int

    @Query("DELETE FROM server_data_table")
    suspend fun deleteAll() : Int

    @Query("SELECT * FROM server_data_table ORDER BY date DESC")
    fun getAllServer():LiveData<List<Server>>
    @Query("SELECT * FROM server_data_table ORDER BY date ASC LIMIT 1")
    suspend fun getLastServer():Server

    @Query("SELECT COUNT(url_name) FROM server_data_table")
    suspend fun getNumRow():Int

    @Query("SELECT COUNT(url_name) FROM server_data_table WHERE url_name=:url")
    suspend fun getListByUrl(url: String): Int
}
