package com.example.flightmobileapp.db

import android.content.Context
import com.example.flightmobileapp.db.Server
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flightmobileapp.dbimport.ServerDAO

@Database(entities = [Server::class],version = 1)
abstract class ServerDatabase : RoomDatabase() {
//make one DB
    abstract val serverDAO : ServerDAO

    companion object{
      @Volatile
      private var INSTANCE : ServerDatabase? = null
          fun getInstance(context: Context):ServerDatabase{
              synchronized(this){
                  var instance = INSTANCE
                      if(instance==null){
                          instance = Room.databaseBuilder(
                                 context.applicationContext,
                                 ServerDatabase::class.java,
                                 "subscriber_data_database"
                          ).build()
                      }
                  return instance
              }
          }

    }
}

