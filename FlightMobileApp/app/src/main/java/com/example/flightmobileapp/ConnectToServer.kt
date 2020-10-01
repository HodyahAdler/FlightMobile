package com.example.flightmobileapp

import android.app.Activity
import android.content.Intent
import android.database.Observable
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.concurrent.fixedRateTimer


/**.
 * ConnectToServer responsibility on connect to server with url,
 * send post massage and get image from server.
 * @param activity1 current activity running.
 * @param url url server.
 */
class ConnectToServer(url: String, activity1: Activity){
    lateinit var retrofitObject: Retrofit
    var activityonPause = false
        get() = field
        set(value) { field = value }
    var activity: Activity = activity1
    init {
        val gson = GsonBuilder().setLenient().create()
        try {
            // create community with 10 second time out
            val okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build()
            retrofitObject = Retrofit.Builder().baseUrl(url)
                .client(okHttpClient).addConverterFactory(GsonConverterFactory.create(gson)).build()
        } catch (e : Exception){
            wasKillingError()
        }
    }
    /**.
     * runningImage running update image all 1 second on coroution scope.
     * stop when the activity stop running. or if was killing error.
     * @param image to update him.
     */
    fun runningImage(image: ImageView){
        val job = Job()
        try{
            val uiScope = CoroutineScope(Dispatchers.Main + job)
            fixedRateTimer("timer", false, 0L, 1000) {
                uiScope.launch {
                    updateImageFromServer(image);
                    if (activityonPause) cancel()
                }
                if (activityonPause){
                    cancel()
                }
            }
        }catch (e : java.lang.Exception){
            wasKillingError()
        }
    }


    /**
     * updateImageFromServer send get asking to server.
     * if seccsed load him, if was error anser update on error,
     * and if not seccsed connect update on killing error.
     * @param image to update him.
     */
    fun updateImageFromServer(image: ImageView){
        if (activityonPause) return
        val api = retrofitObject.create(NetworkApi::class.java)
        val body = api.getImg().enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    loadTheImage(image, response)
                } else{
                    wasError()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                wasKillingError()
            }
        })
    }
    /**
     * connectAndGetImageSeccsed check if url seccsed get image from sever.
     * try create community and get image, if succeed return true else false.
     * @param url to cunnect
     * @param context to activity
     */
    fun connectAndGetImageSeccsed(url: String, context:Activity){
        val api = retrofitObject.create(NetworkApi::class.java)
        val body = api.getImg().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    var nextActivite = Intent(context, FlightMobileActivity::class.java)
                    nextActivite.putExtra("url", url)
                    context.startActivity(nextActivite)
                } else{
                    val massage = "Communication with server failed.\n" +
                            "Try another url!"
                    Toast.makeText(context, massage, Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val massage = "Communication with server failed.\n" +
                        "Try another url!"
                Toast.makeText(context, massage, Toast.LENGTH_LONG).show()
            }
        })
    }
    /**
     * loadTheImage change the response from server to image and update
     * the image view. update error if have.
     * @param response from server
     * @param image view to update.
     */
    private fun loadTheImage(image: ImageView, response: Response<ResponseBody>){
        try{
            val byteStreamImage = response.body()?.byteStream()
            val bitMapImage = BitmapFactory.decodeStream(byteStreamImage)
            activity.runOnUiThread{
                image.setImageBitmap(bitMapImage)
            }

        } catch (e: Exception){
            wasError()
        }
    }
    /**
     * postSendIformation send in  Coroutine Scope post json object to server.
     * if was error answer update on error,
     * and if not succeed connect update on killing error.
     * @param json object to send
     */
    fun postSendIformation(json: JsonJoistick){
        if (activityonPause) return
        val api = retrofitObject.create(NetworkApi::class.java)
        val job = Job()
        try{
            val uiScope = CoroutineScope(Dispatchers.Main + job)
            uiScope.launch {
                val body = api.post(json).enqueue(object: Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (!response.isSuccessful) wasError()
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        wasKillingError()
                    }
                })
            }
        }catch (e: java.lang.Exception){
            wasKillingError()
        }
    }
    /**
     *  wasError jumping massage on error to screen.
     */
    fun wasError(){
        val massage = "There are problems communicating with the server"
        Toast.makeText(activity, massage, Toast.LENGTH_SHORT).show()
    }
    /**
     * wasKillingError update activity need pause and jumping massage
     * on critical error.
     */
    fun wasKillingError(){
        if(!activityonPause){
            activityonPause = true
            val massage = "Communication with server failed.\n" +
                    "Try going back to connecting a new server!"
            val toast = Toast.makeText(activity, massage, Toast.LENGTH_LONG)
            toast.show()
        }
    }
}