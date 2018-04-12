package com.danbunnell.steptracker.stepservice;

/**
 * Listens for steps
 */
public interface StepListener {
    /**
     * Called when new steps are detected.
     * @param stepCount the number of steps detected
     */
    void onSteps(int stepCount);
}
