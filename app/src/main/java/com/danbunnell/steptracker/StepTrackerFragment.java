package com.danbunnell.steptracker;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.danbunnell.steptracker.stepservice.StepListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepTrackerFragment extends Fragment {

    private static final String TAG = "StepTrackerFragment";

    /**
     * the user interface update interval in milliseconds
     */
    private static final long UI_UPDATE_INTERVAL_MS = 2000;

    /**
     * the number of steps it requires to earn a heart
     */
    private static final int STEPS_PER_HEART = 30;

    /**
     * a thread scheduler
     */
    private final Handler handler = new Handler();

    /**
     * timer that controls user interface update intervals
     */
    private Runnable interfaceUpdateTimer;

    /**
     * the health bar
     */
    private LinearLayout llHealthBar;

    /**
     * the layout containing hearts
     */
    private GridView gvHeartContainer;

    /**
     * the reset health button
     */
    private Button btnResetHealth;

    /**
     * Holds the current step count
     */
    private int currentStepCount = 0;

    /**
     * Holds the current number of hearts
     */
    private int hearts = 0;

    private HeartContainerImageAdapter heartContainerAdapter;

    /**
     * Required empty public constructor
     */
    public StepTrackerFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_step_tracker, container, false);

        this.llHealthBar = rootView.findViewById(R.id.healthBar);
        this.gvHeartContainer = rootView.findViewById(R.id.gvHeartContainer);
        this.heartContainerAdapter = new HeartContainerImageAdapter(rootView.getContext());
        this.gvHeartContainer.setAdapter(this.heartContainerAdapter);
        this.btnResetHealth = rootView.findViewById(R.id.btnResetHealth);

        return rootView;
    }

    /**
     * Called when the view is created.
     * @param view               the view
     * @param savedInstanceState saved data
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.btnResetHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentStepCount = 0;
                hearts = 0;
                updateInterface();
            }
        });
    }

    /**
     * Called when the fragment is started.
     */
    @Override
    public void onStart() {
        super.onStart();

        this.updateInterface();

        this.interfaceUpdateTimer = new Runnable() {
            @Override
            public void run() {
                updateInterface();
                handler.postDelayed(interfaceUpdateTimer, StepTrackerFragment.UI_UPDATE_INTERVAL_MS);
            }
        };

        MainActivity.getStepService().registerStepListener(StepTrackerFragment.TAG, new StepListener() {
            @Override
            public void onSteps(int stepCount) {
                if ((currentStepCount % STEPS_PER_HEART) + stepCount >= STEPS_PER_HEART) {
                    hearts++;
                }

                currentStepCount += stepCount;
            }
        });

        handler.postDelayed(this.interfaceUpdateTimer, StepTrackerFragment.UI_UPDATE_INTERVAL_MS);
    }

    /**
     * Called when the fragment is stopped.
     */
    @Override
    public void onStop() {
        super.onStop();
        MainActivity.getStepService().unregisterStepListener(StepTrackerFragment.TAG);
        this.handler.removeCallbacks(this.interfaceUpdateTimer);
    }

    /**
     * Updates the user interface.
     */
    private void updateInterface() {
        this.updateHealthBar();
        this.updateHeartContainer();
    }

    /**
     * Updates the health bar UI element
     */
    private void updateHealthBar() {
        LinearLayout.LayoutParams healthBarParams = (LinearLayout.LayoutParams) this.llHealthBar.getLayoutParams();
        healthBarParams.weight = (this.currentStepCount % STEPS_PER_HEART) / (float)STEPS_PER_HEART;
        this.llHealthBar.setLayoutParams(healthBarParams);
    }

    /**
     * Updates the heart container UI element
     */
    private void updateHeartContainer() {
        this.heartContainerAdapter.setHeartCount(hearts);
    }

    /**
     * Adapts a view to a heart container
     */
    private class HeartContainerImageAdapter extends BaseAdapter {

        /**
         * the adapter context
         */
        private Context context;

        /**
         * the count of hearts to display
         */
        private int heartCount;

        public HeartContainerImageAdapter(Context context) {
            this.context = context;
            heartCount = 0;
        }

        /**
         * Called to get the count of images to display.
         *
         * @return an image count
         */
        @Override
        public int getCount() {
            return this.heartCount;
        }

        /**
         * Gets an item based on position.
         *
         * @param position the position
         * @return         the item
         */
        @Override
        public Object getItem(int position) {
            return null;
        }

        /**
         * Gets an item identifier based on position.
         *
         * @param position the position
         * @return         the item identifier
         */
        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**
         * Gets the view to display for the specified position.
         *
         * @param position    the position
         * @param convertView the previous view, to be reused if possible
         * @param parent      the parent view group
         * @return            the view to display
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            if (convertView == null) {
                imageView = new ImageView(getActivity());
                imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(R.drawable.heart);
            return imageView;
        }

        /**
         * Sets the heart count.
         *
         * @param heartCount the heart count
         */
        public void setHeartCount(int heartCount) {
            this.heartCount = heartCount;
        }
    }
}