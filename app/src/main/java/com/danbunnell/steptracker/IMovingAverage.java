package com.danbunnell.steptracker;

interface IMovingAverage {
    float Put(float datum);

    float GetAverage();
}
