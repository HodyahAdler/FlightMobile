package com.example.flightmobileapp

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName



/**
 * information class. create json object for send.
 * @param elevatorIn value to json.
 * @param aileronIn value to json.
 * @param rudderIn value to json.
 * @param throttleIn value to json.
 */
class JsonJoistick(elevatorIn: Double, aileronIn: Double,
                   rudderIn: Float, throttleIn: Float) {
    @SerializedName("elevator")
    @Expose
    var elevator: Double = 0.0
        get() = field
        set(value) { field = value }

    @SerializedName("aileron")
    @Expose
    var aileron: Double = 0.0
        get() = field
        set(value) { field = value }

    @SerializedName("rudder")
    @Expose
    var rudder: Float = 0.0f
        get() = field
        set(value) { field = value }

    @SerializedName("throttle")
    @Expose
    var throttle : Float = 0.0f
        get() = field
        set(value) { field = value }
    init {
        elevator = elevatorIn
        aileron = aileronIn
        throttle = throttleIn
        rudder = rudderIn
    }

}