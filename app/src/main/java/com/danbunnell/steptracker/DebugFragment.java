package com.danbunnell.steptracker;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import static android.util.Log.d;

/**
 * A simple {@link Fragment} subclass.
 */
public class DebugFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "DebugFragment";

    private static final int GRAPH_WIDTH = 40;
    private static final int UI_UPDATE_INTERVAL_MS = 50;

    private SensorManager sensorManager;
    private Sensor accelSensor;

    private float rawAccelMagnitude = 0L;

    private LineGraphSeries<DataPoint> rawAccelSeries = DebugFragment.createSeries(Color.RED);
    private LineGraphSeries<DataPoint> movingAverage15SmoothedSeries = DebugFragment.createSeries(Color.BLUE);
    private LineGraphSeries<DataPoint> movingAverage5SmoothedSeries = DebugFragment.createSeries(Color.GREEN);
    private LineGraphSeries<DataPoint> movingAverage30SmoothedSeries = DebugFragment.createSeries(Color.BLACK);

    private IMovingAverage movingAverage15 = new WindowedMovingAverage(15);
    private IMovingAverage movingAverage5 = new WindowedMovingAverage(5);
    private IMovingAverage movingAverage30 = new WindowedMovingAverage(30);

    private final Handler handler = new Handler();
    private Runnable updateTimer;

    private double lastTimeInSeconds = 0d;

    /**
     * Required empty public constructor
     */
    public DebugFragment() {
        // Required empty public constructor
    }

    /**
     * This method is called when the view is created.
     *
     * @param inflater           inflates the layout
     * @param container          the layout container
     * @param savedInstanceState the saved state data
     * @return                   an inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_debug, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.init();
    }

    /**
     * Initialize the activities dependencies
     */
    private void init() {
        // Get the accelerometer for later sensing
        this.sensorManager = (SensorManager) super.getActivity().getSystemService(Context.SENSOR_SERVICE);
        this.accelSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Set up the GraphViews
        GraphView graphView = this.createGraphView(
                R.id.graph1,
                "Accelerometer Raw Data (X: Red, Y: Blue, Z: Green)",
                "m/s^2");
        graphView.addSeries(this.rawAccelSeries);
        graphView.addSeries(this.movingAverage15SmoothedSeries);
        graphView.addSeries(this.movingAverage5SmoothedSeries);
        graphView.addSeries(this.movingAverage30SmoothedSeries);
    }

    /**
     * This method is called when the sensor we have registered with changes value
     * @param sensorEvent Sensor event data
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch(sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                this.rawAccelMagnitude =
                        sensorEvent.values[DebugFragment.Axis.X.Offset]
                                + sensorEvent.values[DebugFragment.Axis.Y.Offset]
                                + sensorEvent.values[DebugFragment.Axis.Z.Offset];
                this.movingAverage15.Put(this.rawAccelMagnitude);
                this.movingAverage30.Put(this.rawAccelMagnitude);
                this.movingAverage5.Put(this.rawAccelMagnitude);
                d(DebugFragment.TAG, String.format("Raw Magnitude: %.5f", this.rawAccelMagnitude));
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        d(DebugFragment.TAG, "onAccuracyChanged() called");
    }

    /**
     * Called when the activity is resumed
     */
    @Override
    public void onResume() {
        super.onResume();
        d(DebugFragment.TAG, "onResume() called");
        this.sensorManager.registerListener(this, this.accelSensor, SensorManager.SENSOR_DELAY_FASTEST);

        this.updateTimer = new Runnable() {
            @Override
            public void run() {
                d(DebugFragment.TAG, "updateTimer triggered");
                lastTimeInSeconds += 1d;
                rawAccelSeries.appendData(new DataPoint(lastTimeInSeconds, rawAccelMagnitude), true, DebugFragment.GRAPH_WIDTH);
                movingAverage15SmoothedSeries.appendData(new DataPoint(lastTimeInSeconds, movingAverage15.GetAverage()), true, DebugFragment.GRAPH_WIDTH);
                movingAverage5SmoothedSeries.appendData(new DataPoint(lastTimeInSeconds, movingAverage5.GetAverage()), true, DebugFragment.GRAPH_WIDTH);
                movingAverage30SmoothedSeries.appendData(new DataPoint(lastTimeInSeconds, movingAverage30.GetAverage()), true, DebugFragment.GRAPH_WIDTH);
                handler.postDelayed(updateTimer, UI_UPDATE_INTERVAL_MS);
            }
        };

        handler.postDelayed(this.updateTimer, UI_UPDATE_INTERVAL_MS);
    }

    /**
     * Called when the activity is paused
     */
    @Override
    public void onPause() {
        super.onPause();
        d(DebugFragment.TAG, "onPause() called");
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

    private static LineGraphSeries<DataPoint> createSeries(int color) {
        DataPoint[] points = { new DataPoint(0,0) };
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(points);
        series.setColor(color);
        return series;
    }

    private GraphView createGraphView(int graphId, String title, String verticalAxisTitle) {
        GraphView graph = super.getActivity().findViewById(graphId);
        graph.setTitle(title);
        graph.getGridLabelRenderer().setVerticalAxisTitle(verticalAxisTitle);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(DebugFragment.GRAPH_WIDTH);

        return graph;
    }
}
