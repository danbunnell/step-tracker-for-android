package com.danbunnell.steptracker.stepservice;

/**
 * A callback to register with the {@link AccelerometerDataProvider}
 */
public interface AccelerometerListener {
    /**
     * Called when accelerometer data has been received.
     *
     * @param magnitude         raw accelerometer vector magnitude
     * @param filteredMagnitude filtered accelerometer vector magnitude
     */
    void onAccelerometerData(float magnitude, float filteredMagnitude);
}
