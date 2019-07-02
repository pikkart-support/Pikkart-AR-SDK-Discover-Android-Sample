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

import com.pikkart.ar.recognition.RecognitionFragment;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRenderer implements GLTextureView.Renderer
{
    public boolean IsActive = false;

    private int CameraWidth, CameraHeight, ViewportWidth, ViewportHeight, Angle;

    /** Called when the surface is created or recreated. */
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Call native function to initialize com.pikkart.ar.rendering:
    }

    /** Called when the surface changed size. */
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        // Call native function to update com.pikkart.ar.rendering when render surface parameters have changed:
    }

    /** Called when the surface is destroyed. */
    public void onSurfaceDestroyed()
    {
        // Call native function for clear openGL context
    }

    /** Called to draw the current frame. */
    public void onDrawFrame(GL10 gl)
    {
        if (!IsActive) return;

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // Call our native function to render content
        RecognitionFragment.renderCamera(ViewportWidth, ViewportHeight, Angle);
    }

    public void updateViewport(int cameraWidth, int cameraHeight, int viewportWidth,
                               int viewportHeight, int angle) {
        CameraWidth = cameraWidth;
        CameraHeight = cameraHeight;
        ViewportWidth = viewportWidth;
        ViewportHeight = viewportHeight;
        Angle = angle;
    }
}
