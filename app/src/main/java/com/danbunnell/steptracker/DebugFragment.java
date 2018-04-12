package com.danbunnell.steptracker;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.danbunnell.steptracker.stepservice.AccelerometerListener;
import com.danbunnell.steptracker.stepservice.StepListener;
import com.danbunnell.steptracker.stepservice.StepService;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import static android.util.Log.d;

/**
 * A {@link Fragment} which displays debugging information.
 */
public class DebugFragment extends Fragment {

    private static final String TAG = "DebugFragment";

    /**
     * the accelerometer data graph width in seconds
     */
    private static final float GRAPH_WIDTH_S = 2f;

    /**
     * the user interface update interval
     */
    private static final int UI_UPDATE_INTERVAL_MS = 50;

    /**
     * latest raw acceleration magnitude
     */
    private float rawAcceleration = 0L;

    /**
     * latest filtered acceleration magnitude
     */
    private float filteredAcceleration = 0L;

    /**
     * the current step count
     */
    private int currentStepCount = 0;

    /**
     * the current step count from the step sensor
     */
    private int currentStepSensorStepCount = 0;

    /**
     * series holding raw acceleration data to be displayed via the graph
     */
    private LineGraphSeries<DataPoint> rawSeries = DebugFragment.createSeries(Color.RED);

    /**
     * series holding filtered acceleration data to be displayed via the graph
     */
    private LineGraphSeries<DataPoint> filteredSeries = DebugFragment.createSeries(Color.BLACK);

    /**
     * a thread scheduler
     */
    private final Handler handler = new Handler();

    /**
     * timer that controls user interface update intervals
     */
    private Runnable interfaceUpdateTimer;

    /**
     * the time in seconds since the epoch
     */
    private float timeInSeconds = 0f;

    /**
     * the accelerometer data graph view
     */
    private GraphView graphView;

    /**
     * displays the current step count
     */
    private TextView tvCurrentStepCount;

    /**
     * displays the current step sensor step count
     */
    private TextView tvStepSensorStepCount;

    /**
     * Initializes a new instance of the {@link DebugFragment} class.
     *
     * <p>Must accept zero parameters.</p>
     */
    public DebugFragment() {
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

    /**
     * Called when the fragment is started.
     */
    @Override
    public void onStart() {
        super.onStart();

        this.tvCurrentStepCount = super.getActivity().findViewById(R.id.tvCurrentStepCount);
        this.tvStepSensorStepCount = super.getActivity().findViewById(R.id.tvStepSensorStepCount);
        this.graphView= this.createGraphView(
                R.id.graph1,
                "Accelerometer Data (Raw (red), Filtered (black))",
                "m/s^2");
        this.graphView.addSeries(this.rawSeries);
        this.graphView.addSeries(this.filteredSeries);

        StepService stepService = MainActivity.getStepService();
        stepService.registerAccelerometerListener(
                DebugFragment.TAG,
                new AccelerometerListener() {
                    @Override
                    public void onAccelerometerData(float magnitude, float filteredMagnitude) {
                        rawAcceleration = magnitude;
                        filteredAcceleration = filteredMagnitude;
                    }
                });

        stepService.registerStepListener(
                DebugFragment.TAG,
                new StepListener() {
                    @Override
                    public void onSteps(int stepCount) {
                        currentStepCount += stepCount;
                    }
                });

        stepService.registerStepDetectorSensorListener(
                DebugFragment.TAG,
                new StepListener() {
                    @Override
                    public void onSteps(int stepCount) {
                        currentStepSensorStepCount += stepCount;
                    }
                }
        );

        this.updateUserInterface();

        this.interfaceUpdateTimer = new Runnable() {
            /**
             * Runs the user interface update procedure.
             */
            @Override
            public void run() {
                updateUserInterface();
                handler.postDelayed(interfaceUpdateTimer, UI_UPDATE_INTERVAL_MS);
            }
        };

        handler.postDelayed(this.interfaceUpdateTimer, UI_UPDATE_INTERVAL_MS);
    }

    /**
     * Called when the fragment is stopped.
     */
    @Override
    public void onStop() {
        super.onStop();

        this.handler.removeCallbacks(this.interfaceUpdateTimer);

        StepService stepService = MainActivity.getStepService();
        stepService.unregisterStepListener(DebugFragment.TAG);
        stepService.unregisterAccelerometerListener(DebugFragment.TAG);
    }

    /**
     * Creates a series of data points.
     *
     * @param color a color for the series when graphed
     * @return      a series of data points
     */
    private static LineGraphSeries<DataPoint> createSeries(int color) {
        DataPoint[] points = { new DataPoint(0,0) };
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(points);
        series.setColor(color);
        return series;
    }

    /**
     * Creates a graph view.
     *
     * @param graphId           the graph view resource id
     * @param title             the title
     * @param verticalAxisTitle the Y-axis title
     * @return                  a graph view
     */
    private GraphView createGraphView(int graphId, String title, String verticalAxisTitle) {
        GraphView graph = super.getActivity().findViewById(graphId);
        graph.setTitle(title);
        graph.getGridLabelRenderer().setVerticalAxisTitle(verticalAxisTitle);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(GRAPH_WIDTH_S);

        return graph;
    }

    /**
     * Updates the user interface
     */
    private void updateUserInterface() {
        timeInSeconds += UI_UPDATE_INTERVAL_MS / 1000f;
        int graphWidthDataPoints = Math.round(GRAPH_WIDTH_S / (UI_UPDATE_INTERVAL_MS / 1000));
        rawSeries.appendData(new DataPoint(timeInSeconds, rawAcceleration), true, graphWidthDataPoints);
        filteredSeries.appendData(new DataPoint(timeInSeconds, filteredAcceleration), true, graphWidthDataPoints);
        tvCurrentStepCount.setText("Accelerometer-based steps: " + currentStepCount);
        tvStepSensorStepCount.setText("Step Sensor steps: " + currentStepSensorStepCount);
    }
}
