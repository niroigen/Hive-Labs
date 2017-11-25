package com.example.niroigensuntharam.elec390application;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IARegion;
import com.indooratlas.android.sdk.resources.IAFloorPlan;
import com.indooratlas.android.sdk.resources.IAResourceManager;
import com.indooratlas.android.sdk.resources.IAResult;
import com.indooratlas.android.sdk.resources.IAResultCallback;
import com.indooratlas.android.sdk.resources.IATask;

import java.util.logging.Logger;

public class NavigationActivity extends AppCompatActivity {

    private IALocationManager mLocationManager;
    private IAResourceManager mResourceManager;
    private ImageView mFloorPlanImage;

    private IATask<IAFloorPlan> mPendingAsyncResult;

    private static String TAG = "NavigationActivity";

    private IARegion.Listener mRegionListener = new IARegion.Listener() {
        @Override
        public void onEnterRegion(IARegion region) {
            if (region.getType() == IARegion.TYPE_FLOOR_PLAN) {
                fetchFloorPlan(region.getId());
            }
        }

        @Override
        public void onExitRegion(IARegion region) {
            // leaving a previously entered region
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.rgb(147, 33, 56)));

        setContentView(R.layout.activity_navigation);

        mFloorPlanImage = (ImageView) findViewById(R.id.navigationImageView);
        // ...
        // Create instance of IAResourceManager class
        mResourceManager = IAResourceManager.create(this);

        MainActivity.mIaLocationManager.registerRegionListener(mRegionListener);
    }

    private void fetchFloorPlan(String id) {
        // Cancel pending operation, if any
        if (mPendingAsyncResult != null && !mPendingAsyncResult.isCancelled()) {
            mPendingAsyncResult.cancel();
        }

        mPendingAsyncResult = mResourceManager.fetchFloorPlanWithId(id);
        if (mPendingAsyncResult != null) {
            mPendingAsyncResult.setCallback(new IAResultCallback<IAFloorPlan>() {
                @Override
                public void onResult(IAResult<IAFloorPlan> result) {
                    Log.d(TAG, "onResult: " + result);

                    if (result.isSuccess()) {
                        Toast.makeText(NavigationActivity.this,
                                "Sucess: ", Toast.LENGTH_LONG)
                                .show();

                        handleFloorPlanChange(result.getResult());
                    } else {
                        // do something with error
                        Toast.makeText(NavigationActivity.this,
                                "loading floor plan failed: " + result.getError(), Toast.LENGTH_LONG)
                                .show();
                    }
                }
            }, Looper.getMainLooper()); // deliver callbacks in main thread
        }
    }

    private void handleFloorPlanChange(IAFloorPlan newFloorPlan){
        Picasso.with(this)
                .load(newFloorPlan.getUrl())
                .into(mFloorPlanImage);
    }
}
