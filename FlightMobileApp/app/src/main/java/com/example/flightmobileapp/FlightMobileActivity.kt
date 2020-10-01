package com.example.flightmobileapp

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.SyncStateContract
import android.view.Gravity
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.activity_flight_mobile.*

/**
 * FlightMobileActivity create activity on filght mobile.
 * responsibility manger update image and handle joistick
 * information and send updates.
 */
class FlightMobileActivity: AppCompatActivity() {
    var elevator: Double = 0.0
    var aileron: Double = 0.0
    var rudder: Float = 0.0f
    var throttle: Float = 0.0f
    lateinit var textRudder: TextView
    lateinit var textThrottle: TextView
    lateinit var connectToServer: ConnectToServer
    lateinit var image: ImageView
    /**
     * onCreate override activity fun. update and connect to all object in screen,
     * try connect to server and begging asking image. else update on error in connect.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flight_mobile)
        lifecycle.currentState.isAtLeast (Lifecycle.State.RESUMED)
        textRudder = findViewById<TextView>(R.id.textRudder)
        textRudder.text = (getString(R.string.rudder) + "   " +  rudder)
        textThrottle = findViewById<TextView>(R.id.textThrottle)
        textThrottle.text = (getString(R.string.throttle) + "  "+ throttle)
        val serverUrl: String = intent.getStringExtra("url").toString()
        image = findViewById<ImageView>(R.id.ImageView)
        try{
            connectToServer = ConnectToServer(serverUrl, this)
            connectToServer.runningImage(image)
        }catch (e: Exception){
            val massage = "Communication with server failed.\n" +
                    "Try going back to connecting a new server!"
            val toast = Toast.makeText(this, massage, Toast.LENGTH_LONG)
            toast.show()
            return;
        }
        val joystick = findViewById<JoystickView>(R.id.joystickView)
        joystick.setOnMoveListener {angle,strength ->
            if (calculateXYLocation(angle,strength)) {
                sendUpDateMassage()
            }
        }
        rudderSeekBarListener()
        throttleSeekBarListener()
    }
    /**
     * return to connect screen when click on back pressed.
     */
    override fun onBackPressed() {
        val backActivite = Intent(this, ConnectScreenActivity::class.java)
        startActivity(backActivite)
    }
    /**
     * onPause update connect to server stop send asking.
     */
    override fun onPause(){
        super.onPause()
        connectToServer.activityonPause = true
    }
    /**
     * onResume update connect to server return send asking and play update image.
     */
    override fun onResume() {
        super.onResume()
        connectToServer.activityonPause = false
        connectToServer.runningImage(image)
    }
    /**
     * calculateXYLocation calculate aileron and elevator joystick with angle
     * and strenge.
     * @param angle of joistick change
     * @param strenge of joistick change
     */
    private fun calculateXYLocation(angle: Int, strenge: Int): Boolean {
        var newAileron  = (strenge * Math.cos(Math.toRadians(angle.toDouble())) / 100)
        var newElevator = (strenge * Math.sin(Math.toRadians(angle.toDouble())) / 100)
        return checkIfTheChangeIsMorePercent(newAileron, newElevator)
    }
    /**
     * check if the movemeent was more then percent. if yes, change value and update thah
     * semothing change true. else, return false.
     * @param newAileron value that wont check.
     * @param newElevator value that wont check.
     */
    private fun checkIfTheChangeIsMorePercent(newAileron: Double, newElevator: Double): Boolean {
        var somethingCange = false;
        var aileronDistance = newAileron - aileron
        var elevatorDistance = newElevator - elevator
        // chake aileron chnged
        if (Math.abs(aileronDistance) >= 0.01){
            aileron = newAileron
            somethingCange = true
        }
        // chake elevator chnged
        if (Math.abs(elevatorDistance) >= 0.01){
            elevator = newElevator
            somethingCange = true
        }
        return somethingCange
    }
    /**
     * rudderSeekBarListener listen to rudder seek bar.
     * update the value when changed, and when stop changed if was
     * change more precent, update the value.
     */
    private fun rudderSeekBarListener(){
        val rudderSeekBar = findViewById<SeekBar>(R.id.rudderSeekBar);
        rudderSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                // update the text on rudder value movement
                var newRudder = (changeInSeekBarIfMorePrecent(rudderSeekBar.progress)/10)
                textRudder.text = (getString(R.string.rudder) + "   " +  newRudder)
            }
            override fun onStartTrackingTouch(seek: SeekBar) {}

            override fun onStopTrackingTouch(seek: SeekBar) {
                // update on change in text and send massage
                var newRudder = (changeInSeekBarIfMorePrecent(rudderSeekBar.progress)/10)
                textRudder.text = (getString(R.string.rudder) + "   " +  newRudder)
                if (newRudder != 0.0f){
                    rudder = newRudder
                    sendUpDateMassage()
                }
            }
        })
    }
    /**
     * throttleSeekBarListener listen to throttle seek bar.
     * update the value when changed, and when stop changed if was
     * change more precent, update the value.
     */
    private fun throttleSeekBarListener(){
        val throttleSeekBar = findViewById<SeekBar>(R.id.throttleSeekBar)
        throttleSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seek: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                // update the text on throttle value movement
                var newThrottle = (changeInSeekBarIfMorePrecent(throttleSeekBar.progress)/10)
                textThrottle.text = (getString(R.string.throttle) + "  "+ newThrottle)
            }
            override fun onStartTrackingTouch(seek: SeekBar) {}

            override fun onStopTrackingTouch(seek: SeekBar) {
                // update on change in text and send massage
                var newThrottle = (changeInSeekBarIfMorePrecent(throttleSeekBar.progress)/10)
                textThrottle.text = (getString(R.string.throttle) + "  "+ newThrottle)
                if (newThrottle != 0.0f){
                    throttle =  newThrottle
                    sendUpDateMassage()
                }
            }
        })
    }
    /**
     * changeInSeekBarIfMorePrecent check if value is more then one precent change.
     */
    private fun changeInSeekBarIfMorePrecent(newRudder: Int): Float{
        var standardizedRudder = (newRudder / 100f)
        var distance = standardizedRudder - rudder
        if (Math.abs(distance) >= 0.01){
            return standardizedRudder
        }
        return 0.0f;
    }
    /**
     * sendUpDateMassage if not need stop, send post massage with new value.
     */
    private fun sendUpDateMassage(){
        if(!connectToServer.activityonPause){
            if (elevator > 1 || elevator < -1 || aileron > 1
                || aileron < -1 || rudder > 1 || rudder < -1
                || throttle > 1 || throttle < 0) return
            // create json and send him
            var json = JsonJoistick(elevator, aileron, rudder, throttle)
            connectToServer.postSendIformation(json);
        }
    }
}

