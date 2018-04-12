package com.danbunnell.steptracker.stepservice;

import android.hardware.SensorManager;

import com.danbunnell.steptracker.common.SignalFilter;

public class StepService {

    private static String TAG = "StepService";

    /**
     * the accelerometer listener
     */
    private AccelerometerDataProvider accelerometerDataProvider;

    /**
     * the step detector
     */
    private final StepDetector stepDetector;

    private final StepDetectorSensorDataProvider stepDetectorSensorDataProvider;

    /**
     * Initialize a new instance of the {@link StepService} class.
     *
     * @param sensorManager           the sensor manager
     * @param sampleRateInMs          sample rate in milliseconds
     * @param filter                  accelerometer data filter
     * @param stepDetectionWindowMs   the step detection window size in milliseconds
     */
    public StepService(
            SensorManager sensorManager,
            long sampleRateInMs,
            SignalFilter filter,
            long stepDetectionWindowMs) {

        // Initialize and start accelerometer listening
        this.accelerometerDataProvider = new AccelerometerDataProvider(
                sensorManager,
                sampleRateInMs,
                filter);

        this.stepDetector = new StepDetector(sampleRateInMs, stepDetectionWindowMs);
        this.stepDetectorSensorDataProvider = new StepDetectorSensorDataProvider(sensorManager);
    }

    /**
     * Called when the service is started.
     */
    public void onStart() {
        this.registerAccelerometerListener(
                StepService.TAG,
                new AccelerometerListener(){
                    @Override
                    public void onAccelerometerData(float raw, float filtered) {
                        stepDetector.add(filtered);
                    }
                });

        this.accelerometerDataProvider.onStart();
        this.stepDetector.onStart();
        this.stepDetectorSensorDataProvider.onStart();
    }

    /**
     * Called when the service is stopped.
     */
    public void onStop() {
        this.unregisterAccelerometerListener(StepService.TAG);
        this.stepDetector.onStop();
        this.stepDetectorSensorDataProvider.onStop();
        this.accelerometerDataProvider.onStop();
    }

    /**
     * Registers a new listener to receive accelerometer events.
     *
     * @param identifier the listener identifier
     * @param listener   the listener
     */
    public void registerAccelerometerListener(String identifier, AccelerometerListener listener) {
        this.accelerometerDataProvider.registerCallback(identifier, listener);
    }

    /**
     * Unregisters a listener from accelerometer events.
     *
     * @param identifier the listener identifier
     */
    public void unregisterAccelerometerListener(String identifier) {
        this.accelerometerDataProvider.unregisterCallback(identifier);
    }

    /**
     * Registers a listener to receive steps from the step detector sensor.
     *
     * @param identifier the listener identifier
     * @param listener   the listener
     */
    public void registerStepDetectorSensorListener(String identifier, StepListener listener) {
        this.stepDetectorSensorDataProvider.registerCallback(identifier, listener);
    }

    /**
     * Unregisters a listener to receive step events from the step detector sensor.
     * @param identifier the listener identifier
     */
    public void unregisterStepDetectorSensorListener(String identifier) {
        this.stepDetectorSensorDataProvider.unregisterCallback(identifier);
    }

    /**
     * Registers a new listener to receive step events.
     *
     * @param identifier the listener identifier
     * @param listener  the listener
     */
    public void registerStepListener(String identifier, StepListener listener) {
        this.stepDetector.registerStepListener(identifier, listener);
    }

    /**
     * Unregisters a listener from step events.
     *
     * @param identifier the listener identifier
     */
    public void unregisterStepListener(String identifier) {
        this.stepDetector.unregisterStepListener(identifier);
    }
}
