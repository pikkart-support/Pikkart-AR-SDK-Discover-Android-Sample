/* ===============================================================================
 * Copyright (c) 2016 Pikkart S.r.l. All Rights Reserved.
 * Pikkart is a trademark of Pikkart S.r.l., registered in Europe,
 * the United States and other countries.
 *
 * This file is part of Pikkart AR SDK Tutorial series, a series of tutorials
 * explaining how to use and fully exploits Pikkart's AR SDK.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============================================================================*/
package com.pikkart;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;

import com.pikkart.ar.recognition.items.InterestPoint;
import com.pikkart.discover.DiscoverNativeWrapper;


public class InterestPointView extends View implements View.OnTouchListener {
    public int device_orientation;
    public int camera_orientation;
    private float camerah = 480;
    private float cameraw = 640;
    private float radius;
    private int minRadius = 40;
    private int maxRadius = 80;
    private Context mContext;

    // Canvas draw
    Paint mPaintPoint;
    private int diff;
    private int w, h;
    private float ratiocamera;
    private float ratio_screen_camera;
    private float offset;
    private boolean valid = false;

    public InterestPointView(Context context) {
        super(context);
        init();
        setOnTouchListener(this);
        mContext = context;
        SetupAnimation();
    }

    private void init() {
        mPaintPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintPoint.setColor(Color.RED);
    }

    protected void cameraToScreen(int diffangle,float ratio_screen_camera,float offset, float canvas_w,float canvas_h, float x, float y, float[] xytransform)
    {
        switch (diffangle) {
        case 0:
            xytransform [0] = x*ratio_screen_camera;
            xytransform[1] = y*ratio_screen_camera-offset;
            break;
        case 90:
            xytransform[0] = canvas_w+offset-y*ratio_screen_camera;
            xytransform [1] = x*ratio_screen_camera;
            break;
        case 180:
            xytransform [0] = canvas_w-x*ratio_screen_camera;
            xytransform[1] =  canvas_h+offset-y*ratio_screen_camera;
            break;
        default:
            xytransform[0] = y*ratio_screen_camera-offset;
            xytransform [1] = canvas_h-x*ratio_screen_camera;
            break;
        }

    }

    public void changeOrientation(){
        int screenorient = 0;
        switch (device_orientation) {
            case Surface.ROTATION_0:
                screenorient = 0;
                break;
            case Surface.ROTATION_90:
                screenorient = 90;
                break;
            case Surface.ROTATION_180:
                screenorient = 180;
                break;
            default:
                screenorient = 270;
                break;
        }

        diff = (360 + camera_orientation - screenorient) % 360;
        valid = false;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw with correct orientation/scale
        if(!valid) {
            w = canvas.getWidth();
            h = canvas.getHeight();

            ratiocamera = camerah/cameraw;
            ratio_screen_camera = Math.max(h, w) / cameraw;
            offset = (ratiocamera * Math.max(h, w) - Math.min(h, w)) / 2;
            valid = true;
        }

        float[] xy = new float[2];
        DiscoverNativeWrapper.UpdateInterestPointPositions();
        for(InterestPoint ip : DiscoverNativeWrapper.interest_points) {
            float px = ip.getX();
            float py = ip.getY();
            if(px != -1 || py != -1) {
                cameraToScreen(diff, ratio_screen_camera, offset, w, h, px, py, xy);
                float x = xy[0];
                float y = xy[1];
                Log.i("onDraw", "X: " + px + ", Y:" + py);
                canvas.drawCircle(x, y, radius, mPaintPoint);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Get eventually touch
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                for(InterestPoint ip : DiscoverNativeWrapper.interest_points)
                    {
                        float xp = event.getX();
                        float yp = event.getY();
                        float xc = ip.getX();
                        float yc = ip.getY();

                        //Adjust points to camera
                        float[] xy = new float[2];
                        cameraToScreen(diff, ratio_screen_camera, offset, w, h, xc, yc, xy);

                        double d = Math.sqrt(Math.pow(xp - xy[0], 2) + Math.pow(yp - xy[1], 2));
                        double dSquare = Math.pow(d, 2);
                        double rSquare = Math.pow(maxRadius, 2);
                        if (!(dSquare > rSquare)) // Poi touched
                        {
                            // Attach listener
                            // Attach content
                        }
                    break;
                }
        }
        return false;
    }

    // Set up simple poi's animation
    private void SetupAnimation()
    {
        PropertyValuesHolder propertyRadiusUp = PropertyValuesHolder.ofInt("radius", minRadius, maxRadius);
        ValueAnimator radiusUp = new ValueAnimator();
        radiusUp.setValues(propertyRadiusUp);
        radiusUp.setDuration(2000);
        radiusUp.setRepeatCount(ValueAnimator.INFINITE);
        radiusUp.setRepeatMode(ValueAnimator.REVERSE);

        radiusUp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                radius = (int)animation.getAnimatedValue("radius");
                invalidate();
            }
        });
        radiusUp.start();
    }
}
