package com.danbunnell.steptracker.stepservice;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.danbunnell.steptracker.common.SignalFilter;

import java.util.HashMap;
import java.util.Map;

import static android.util.Log.*;

class AccelerometerDataProvider implements SensorEventListener {

    private static final String TAG = "AccelerometerDataProvider";

    /**
     * manages sensors
     */
    private SensorManager sensorManager;

    /**
     * the accelerometer sensor
     */
    private Sensor accelerometer;

    /**
     * the registered callbacks
     */
    private Map<String, AccelerometerListener> callbacks;

    /**
     * used to filter raw accelerometer data
     */
    private SignalFilter filter;

    /**
     * sensor sample rate in hertz (Hz)
     */
    private long sampleRateMs;

    /**
     * Initializes a new instance of the {@link AccelerometerDataProvider} class.
     *
     * @param sensorManager the sensor manager
     * @param sampleRateMs  the sample rate in milliseconds
     * @param filter        the signal filter
     */
    AccelerometerDataProvider(SensorManager sensorManager, long sampleRateMs, SignalFilter filter) {
        this.sensorManager = sensorManager;
        this.accelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.callbacks = new HashMap<>();
        this.sampleRateMs = sampleRateMs;
        this.filter = filter;
    }

    /**
     * Registers a callback for the onAccelerometerData event.
     *
     * @param identifier an identifier for the callback
     * @param callback a callback
     */
    public void registerCallback(String identifier, AccelerometerListener callback) {
        this.callbacks.put(identifier, callback);
    }

    /**
     * Unregisters a callback for the onAccelerometerData event.
     *
     * @param identifier the callback identifier
     */
    public void unregisterCallback(String identifier) {
        this.callbacks.remove(identifier);
    }

    /**
     * Starts listening to the accelerometer.
     */
    public void onStart() {
        this.sensorManager.registerListener(this, this.accelerometer, this.convertMsToMicroseconds(this.sampleRateMs));
    }

    /**
     * Stops listening to the accelerometer.
     */
    public void onStop() {
        this.sensorManager.unregisterListener(this);
    }

    /**
     * Called when the registered sensor changes value.
     *
     * @param sensorEvent Sensor event data
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float magnitude = this.getMagnitude(sensorEvent);
        float filteredMagnitude = this.filter.filter(magnitude);

        for(AccelerometerListener callback : callbacks.values()) {
            callback.onAccelerometerData(magnitude, filteredMagnitude);
        }
    }

    /**
     * Called when the sensor accuracy changes.
     *
     * @param sensor the sensor whose accuracy changed
     * @param i      the accuracy value
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /**
     * Gets the magnitude of a sensor event.
     *
     * @param sensorEvent the sensor event
     * @return            a magnitude
     */
    private float getMagnitude(SensorEvent sensorEvent) {
        return sensorEvent.values[0] + sensorEvent.values[1] + sensorEvent.values[2] - SensorManager.GRAVITY_EARTH;
    }

    /**
     * Converts milliseconds to microseconds.
     *
     * @param ms a duration in milliseconds
     * @return   a duration in microseconds
     */
    private int convertMsToMicroseconds(long ms) {
        return (int) ms * 1000;
    }
}