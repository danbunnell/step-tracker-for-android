package com.danbunnell.steptracker.stepservice;

import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Classifies steps based on acceleration data.
 */
class StepDetector {
    private static final String TAG = "StepDetector";

    private static final float PEAK_TO_MEAN_CUTOFF_RATIO = 0.6f;

    private static final float MIN_PEAK_TO_MEAN_BASE = 2.8f;

    private float[] buffer;

    private int bufferIndex;

    /**
     * the detection window size in milliseconds
     */
    private long detectionWindowMs;

    /**
     * a thread scheduler
     */
    private final Handler handler = new Handler();

    /**
     * timer that controls when we look to detect new steps
     */
    private Runnable stepDetectionTimer;

    private Map<String, StepListener> stepListeners;

    public StepDetector(long sampleRateMs, long detectionWindowMs) {
        this.detectionWindowMs = detectionWindowMs;
        int bufferSize = Math.round(detectionWindowMs / (float) sampleRateMs);

        this.buffer = new float[bufferSize];
        this.bufferIndex = 0;

        this.stepListeners = new HashMap<>();
    }

    /**
     * Called when the detector is started.
     */
    public void onStart() {
        this.stepDetectionTimer = new Runnable() {
            /**
             * Runs the user interface update procedure.
             */
            @Override
            public void run() {
                int stepCountSample = getStepCount();
                for(StepListener listener : stepListeners.values()) {
                    listener.onSteps(stepCountSample);
                }

                handler.postDelayed(stepDetectionTimer, detectionWindowMs);
            }
        };

        handler.postDelayed(this.stepDetectionTimer, detectionWindowMs);
    }

    /**
     * Called when the detector is stopped.
     */
    public void onStop() {
        this.handler.removeCallbacks(this.stepDetectionTimer);
    }

    /**
     * Registers a step listener.
     *
     * @param identifier the listener identifier
     * @param listener   the listener
     */
    public void registerStepListener(String identifier, StepListener listener) {
        this.stepListeners.put(identifier, listener);
    }

    /**
     * Unregisters a step listener.
     *
     * @param identifier the listener identifier
     */
    public void unregisterStepListener(String identifier) {
        this.stepListeners.remove(identifier);
    }

    /**
     * Adds a vector to the buffer.
     *
     * @param vector a vector
     */
    public void add(float vector) {
        this.buffer[this.bufferIndex] = vector;

        if (this.bufferIndex + 1 < this.buffer.length) {
            this.bufferIndex++;
        } else {
            this.bufferIndex = 0;
        }
    }

    /**
     * Gets the step count from the current buffer.
     *
     * @return a step count
     */
    private int getStepCount() {
        float[] bufferCopy = this.getBufferCopy();

        int peakCount = 0;
        int valleyCount = 0;
        float peakTotal = 0;
        float valleyTotal = 0;

        ArrayList<Float> peaks = new ArrayList<>();

        for (int i = 0; i < bufferCopy.length; i++) {
            float current = bufferCopy[i];
            float prev = (i != 0) ? bufferCopy[i - 1] : bufferCopy[bufferCopy.length - 1];
            float next = (i != bufferCopy.length - 1) ? bufferCopy[i + 1] : bufferCopy[0];

            float forwardSlope = next - current;
            float backwardSlope = current - prev;

            if (forwardSlope < 0 && backwardSlope > 0) {
                peakCount++;
                peakTotal += current;
                peaks.add(current);
            } else if (forwardSlope > 0 && backwardSlope < 0) {
                valleyCount++;
                valleyTotal += current;
            }
        }

        float peakMean = peakTotal / peakCount;
        float valleyMean = valleyTotal / valleyCount;

        int stepCount = 0;

        for(float peak : peaks) {
            if ((peak > PEAK_TO_MEAN_CUTOFF_RATIO * peakMean)
                    && (peak - valleyMean > MIN_PEAK_TO_MEAN_BASE)) {
                stepCount++;
            }
        }

        return stepCount;
    }

    /**
     * Copies the current buffer.  This is to help avoid concurrency issues.
     *
     * @return a copy of the current buffer
     */
    private float[] getBufferCopy() {
        float[] copy = new float[buffer.length];
        System.arraycopy(this.buffer, 0, copy, 0, this.buffer.length);
        return copy;
    }
}
