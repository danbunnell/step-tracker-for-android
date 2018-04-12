package com.danbunnell.steptracker;

import android.content.Context;
import android.hardware.SensorManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.danbunnell.steptracker.common.MovingAverageFilter;
import com.danbunnell.steptracker.common.SignalFilter;
import com.danbunnell.steptracker.stepservice.StepService;

/**
 * The main activity
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    /**
     * the moving average window size
     */
    private static final int MOVING_AVERAGE_WINDOW_SIZE = 30;

    /**
     * the accelerometer sampling rate in milliseconds
     */
    private static final long SAMPLE_RATE_MS = 5;

    /**
     * the step detection window in seconds
     */
    private static long STEP_DETECTION_WINDOW_MS = 2000;

    /**
     * provides step-related data services
     */
    private static StepService stepService;

    /**
     * Called when the activity is created.
     *
     * @param savedInstanceState saved state information
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        OrderedFragmentPagerAdapter adapter = new OrderedFragmentPagerAdapter(this, getSupportFragmentManager());
        adapter.addFragment(new StepTrackerFragment(), "Step Tracker");
        adapter.addFragment(new DebugFragment(), "Debug Information");

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        this.initializeStepService();
    }

    /**
     * Initialize the step service.
     */
    private void initializeStepService() {
        SignalFilter accelerometerFilter = new MovingAverageFilter(MOVING_AVERAGE_WINDOW_SIZE);

        this.stepService = new StepService(
                (SensorManager) getSystemService(Context.SENSOR_SERVICE),
                MainActivity.SAMPLE_RATE_MS,
                accelerometerFilter,
                MainActivity.STEP_DETECTION_WINDOW_MS);
    }

    /**
     * Called when the activity is started.
     */
    @Override
    public void onStart() {
        super.onStart();
        this.stepService.onStart();
    }

    /**
     * Called when the activity is stopped.
     */
    @Override
    public void onStop() {
        super.onStop();
        this.stepService.onStop();
    }

    /**
     * Returns the activity's step service.
     *
     * @return a step service
     */
    public static StepService getStepService() {
        return MainActivity.stepService;
    }
}
