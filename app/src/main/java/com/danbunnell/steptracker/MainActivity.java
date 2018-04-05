package com.danbunnell.steptracker;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int GRAPH_WIDTH = 40;
    private static final int UI_UPDATE_INTERVAL_MS = 1000;

    private SensorManager sensorManager;
    private Sensor accelSensor;

    private float[] rawAccelValues = new float[3];

    private LineGraphSeries<DataPoint> seriesX;
    private LineGraphSeries<DataPoint> seriesY;
    private LineGraphSeries<DataPoint> seriesZ;

    private final Handler handler = new Handler();
    private Runnable updateTimer;

    private double lastTimeInSeconds = 0d;

    /**
     * Called when the activity is created
     * @param savedInstanceState saved state information
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.init();
    }

    /**
     * Initialize the activities dependencies
     */
    private void init() {
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.accelSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        DataPoint[] initialData = { new DataPoint(0,0) };

        GraphView graphRawAccel = (GraphView) this.findViewById(R.id.graphRawAccel);
        this.seriesX = new LineGraphSeries<>(initialData);
        this.seriesX.setColor(Color.RED);
        graphRawAccel.addSeries(this.seriesX);

        this.seriesY = new LineGraphSeries<>(initialData);
        this.seriesY.setColor(Color.BLUE);
        graphRawAccel.addSeries(this.seriesY);

        this.seriesZ = new LineGraphSeries<>(initialData);
        this.seriesZ.setColor(Color.GREEN);
        graphRawAccel.addSeries(this.seriesZ);

        graphRawAccel.setTitle("Accelerometer Raw Data (X: Red, Y: Blue, Z: Green)");
        graphRawAccel.getGridLabelRenderer().setVerticalAxisTitle("m/s^2");
        graphRawAccel.getViewport().setXAxisBoundsManual(true);
        graphRawAccel.getViewport().setMinX(0);
        graphRawAccel.getViewport().setMaxX(MainActivity.GRAPH_WIDTH);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch(sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                this.rawAccelValues[Axis.X.Offset] = sensorEvent.values[Axis.X.Offset];
                this.rawAccelValues[Axis.Y.Offset] = sensorEvent.values[Axis.Y.Offset];
                this.rawAccelValues[Axis.Z.Offset] = sensorEvent.values[Axis.Z.Offset];
                Log.d("onSensorChanged", String.format("X: %f, Y: %f, Z: %f", this.rawAccelValues[Axis.X.Offset], this.rawAccelValues[Axis.Y.Offset], this.rawAccelValues[Axis.Z.Offset]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d("onAccuracyChanged", "onAccuracyChanged() called");
    }

    /**
     * Called when the activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume", "onResume() called");
        this.sensorManager.registerListener(this, this.accelSensor, SensorManager.SENSOR_DELAY_UI);

        this.updateTimer = new Runnable() {
            @Override
            public void run() {
                Log.d("updateTime", "updateTimer triggered");
                lastTimeInSeconds += 1d;
                seriesX.appendData(new DataPoint(lastTimeInSeconds, rawAccelValues[Axis.X.Offset]), true, MainActivity.GRAPH_WIDTH);
                seriesY.appendData(new DataPoint(lastTimeInSeconds, rawAccelValues[Axis.Y.Offset]), true, MainActivity.GRAPH_WIDTH);
                seriesZ.appendData(new DataPoint(lastTimeInSeconds, rawAccelValues[Axis.Z.Offset]), true, MainActivity.GRAPH_WIDTH);
                handler.postDelayed(updateTimer, UI_UPDATE_INTERVAL_MS);

            }
        };

        handler.postDelayed(this.updateTimer, UI_UPDATE_INTERVAL_MS);
    }

    /**
     * Called when the activity is paused
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onPause", "onPause() called");
        this.handler.removeCallbacks(this.updateTimer);
        this.sensorManager.unregisterListener(this);
    }

    private enum Axis {
        X(0),
        Y(1),
        Z(2);

        public final int Offset;

        private Axis(int offset) {
            this.Offset = offset;
        }
    }
}
