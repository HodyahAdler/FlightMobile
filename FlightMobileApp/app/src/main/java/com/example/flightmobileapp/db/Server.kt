package com.example.flightmobileapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
//hold the data in the DB
@Entity(tableName = "server_data_table")
data class Server (

    @PrimaryKey()
    @ColumnInfo(name = "url_name")
    var url : String,

    @ColumnInfo(name = "date")
    var date : Long

)