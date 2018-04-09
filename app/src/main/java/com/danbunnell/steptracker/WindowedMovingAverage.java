package com.danbunnell.steptracker;

class WindowedMovingAverage implements IMovingAverage {
    private int windowSize;
    private float[] window;
    private int nextIndex;
    private float average;

    public WindowedMovingAverage(int windowSize) {
        this.windowSize = windowSize;
        this.window = new float[windowSize];
        this.nextIndex = 0;
        this.average = 0;
    }

    public float Put(float datum) {
        window[this.nextIndex % this.windowSize] = datum;
        this.nextIndex++;

        float sum = 0;
        for(float f : window) {
            sum += f;
        }

        this.average = sum / this.windowSize;
        return this.average;
    }

    public float GetAverage() {
        return this.average;
    }
}
