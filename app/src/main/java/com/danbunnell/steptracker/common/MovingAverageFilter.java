package com.danbunnell.steptracker.common;

/**
 * A moving-average signal filtering strategy
 */
public class MovingAverageFilter implements SignalFilter {
    private int windowSize;
    private float[] window;
    private int nextIndex;
    private float average;

    /**
     * Initializes an instance of the {@link MovingAverageFilter} class
     */
    public MovingAverageFilter(int windowSize) {
        this.windowSize = windowSize;
        this.window = new float[windowSize];
        this.nextIndex = 0;
        this.average = 0;
    }

    /**
     * Filters a signal
     * @param vector the vector to filter
     * @return       the filtered vector
     */
    public float filter(float vector) {
        this.window[this.nextIndex % this.windowSize] = vector;
        this.nextIndex++;

        float sum = 0;
        for(float f : this.window) {
            sum += f;
        }

        this.average = sum / this.windowSize;
        return this.average;
    }
}
