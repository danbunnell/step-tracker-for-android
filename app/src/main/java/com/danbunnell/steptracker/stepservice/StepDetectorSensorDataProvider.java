package com.danbunnell.steptracker.stepservice;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.HashMap;
import java.util.Map;

class StepDetectorSensorDataProvider implements SensorEventListener {

    private static final String TAG = "StepDetectorSensorDataProvider";

    /**
     * manages sensors
     */
    private SensorManager sensorManager;

    /**
     * the accelerometer sensor
     */
    private Sensor stepDetectorSensor;

    /**
     * the registered callbacks
     */
    private Map<String, StepListener> callbacks;


    /**
     * Initializes a new instance of the {@link StepDetectorSensorDataProvider} class.
     *
     * @param sensorManager the sensor manager
     */
    StepDetectorSensorDataProvider(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        this.stepDetectorSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        this.callbacks = new HashMap<>();
    }

    /**
     * Registers a callback for the onStep event.
     *
     * @param identifier an identifier for the callback
     * @param callback a callback
     */
    public void registerCallback(String identifier, StepListener callback) {
        this.callbacks.put(identifier, callback);
    }

    /**
     * Unregisters a callback for the onStep event.
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
        this.sensorManager.registerListener(this, this.stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
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
        for(StepListener callback : callbacks.values()) {
            callback.onSteps(1);
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
}