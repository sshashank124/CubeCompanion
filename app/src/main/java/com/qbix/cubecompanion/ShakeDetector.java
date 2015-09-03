package com.qbix.cubecompanion;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {

    //region #VARIABLES
    private float[] mGravity = { 0.0f, 0.0f, 0.0f };
    private float[] mLinearAcceleration = { 0.0f, 0.0f, 0.0f };

    private OnShakeListener mShakeListener;
    private SensorManager sm;
    private Sensor s;

    long startTime = 0;
    int moveCount = 0;
    //endregion

    //region #CONSTANTS
    private static final int MIN_SHAKE_ACCELERATION = 2;
    private static final int MIN_MOVEMENTS = 1;
    private static final int MAX_SHAKE_DURATION = 800;

    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;
    //endregion

    public ShakeDetector(Activity activity, OnShakeListener shakeListener) {
        mShakeListener = shakeListener;
        sm = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        s = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void register() {
        sm.registerListener(this, s, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void unregister() {
        sm.unregisterListener(this, s);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        setCurrentAcceleration(event);
        float maxLinearAcceleration = getMaxCurrentLinearAcceleration();
        if (maxLinearAcceleration > MIN_SHAKE_ACCELERATION) {
            long now = System.currentTimeMillis();
            if (startTime == 0) startTime = now;
            long elapsedTime = now - startTime;
            if (elapsedTime > MAX_SHAKE_DURATION) {
                resetShakeDetection();
            }
            else {
                moveCount++;
                if (moveCount > MIN_MOVEMENTS) {
                    mShakeListener.onShake();
                    resetShakeDetection();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void setCurrentAcceleration(SensorEvent event) {
        final float alpha = 0.8f;
        mGravity[X] = alpha * mGravity[X] + (1 - alpha) * event.values[X];
        mGravity[Y] = alpha * mGravity[Y] + (1 - alpha) * event.values[Y];
        mGravity[Z] = alpha * mGravity[Z] + (1 - alpha) * event.values[Z];
        mLinearAcceleration[X] = event.values[X] - mGravity[X];
        mLinearAcceleration[Y] = event.values[Y] - mGravity[Y];
        mLinearAcceleration[Z] = event.values[Z] - mGravity[Z];
    }

    private float getMaxCurrentLinearAcceleration() {
        float maxLinearAcceleration = mLinearAcceleration[X];
        if (mLinearAcceleration[Y] > maxLinearAcceleration) {
            maxLinearAcceleration = mLinearAcceleration[Y];
        }
        if (mLinearAcceleration[Z] > maxLinearAcceleration) {
            maxLinearAcceleration = mLinearAcceleration[Z];
        }
        return maxLinearAcceleration;
    }

    private void resetShakeDetection() {
        startTime = 0;
        moveCount = 0;
    }

    public interface OnShakeListener {
        void onShake();
    }
}