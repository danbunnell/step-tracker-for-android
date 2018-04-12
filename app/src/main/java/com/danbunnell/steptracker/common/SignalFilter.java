package com.danbunnell.steptracker.common;

/**
 * A signal filtering strategy
 */
public interface SignalFilter {
    /**
     * Filters a signal
     * @param vector the vector to filter
     * @return       the filtered vector
     */
    float filter(float vector);
}
