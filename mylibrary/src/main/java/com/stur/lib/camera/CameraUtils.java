/*
 * Copyright 2014 Google Inc. All rights reserved.
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
 */

package com.stur.lib.camera;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.Surface;

import com.stur.lib.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Camera-related utility functions.
 */
public class CameraUtils {
    private static CameraUtils sCamUtils = null;

    private CameraUtils(){

    }

    public static CameraUtils getInstance() {
        if (sCamUtils == null) {
            sCamUtils = new CameraUtils();
            return sCamUtils;
        } else {
            return sCamUtils;
        }
    }

    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.') + 1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    /**
     * 检查有没有Camera硬件
     *
     * @param context context
     * @return true have camera hardware ,otherwise
     */
    public static boolean hasCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * Attempts to find a preview size that matches the provided width and
     * height (which specify the dimensions of the encoded video). If it fails
     * to find a match it just uses the default preview size for video.
     * <p>
     * TODO: should do a best-fit match, e.g.
     * https://github.com/commonsguy/cwac-camera/blob/master/camera/src/com/commonsware/cwac/camera/CameraUtils.java
     */
    public static void choosePreviewSize(Camera.Parameters parms, int width, int height) {
        // We should make sure that the requested MPEG size is less than the
        // preferred
        // size, and has the same aspect ratio.
        Camera.Size ppsfv = parms.getPreferredPreviewSizeForVideo();
        if (ppsfv != null) {
            Log.d(getTag(), "Camera preferred preview size for video is " + ppsfv.width + "x" + ppsfv.height);
        }

        // for (Camera.Size size : parms.getSupportedPreviewSizes()) {
        // Log.d(TAG, "supported: " + size.width + "x" + size.height);
        // }

        for (Camera.Size size : parms.getSupportedPreviewSizes()) {
            if (size.width == width && size.height == height) {
                parms.setPreviewSize(width, height);
                return;
            }
        }

        Log.w(getTag(), "Unable to set preview size to " + width + "x" + height);
        if (ppsfv != null) {
            parms.setPreviewSize(ppsfv.width, ppsfv.height);
        }
        // else use whatever the default size is
    }

    /**
     * Attempts to find a fixed preview frame rate that matches the desired
     * frame rate.
     * <p>
     * It doesn't seem like there's a great deal of flexibility here.
     * <p>
     * TODO: follow the recipe from
     * http://stackoverflow.com/questions/22639336/#22645327
     *
     * @return The expected frame rate, in thousands of frames per second.
     */
    public static int chooseFixedPreviewFps(Camera.Parameters parms, int desiredThousandFps) {
        List<int[]> supported = parms.getSupportedPreviewFpsRange();

        for (int[] entry : supported) {
            // Log.d(TAG, "entry: " + entry[0] + " - " + entry[1]);
            if ((entry[0] == entry[1]) && (entry[0] == desiredThousandFps)) {
                parms.setPreviewFpsRange(entry[0], entry[1]);
                return entry[0];
            }
        }

        int[] tmp = new int[2];
        parms.getPreviewFpsRange(tmp);
        int guess;
        if (tmp[0] == tmp[1]) {
            guess = tmp[0];
        } else {
            guess = tmp[1] / 2; // shrug
        }

        Log.d(getTag(), "Couldn't find match for " + desiredThousandFps + ", using " + guess);
        return guess;
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private CameraSizeComparator sizeComparator = new CameraSizeComparator();

    public Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, sizeComparator);

        int i = 0;
        for (Size s : list) {
            if ((s.width >= minWidth) && equalRate(s, th)) {
                Log.i(this, "PreviewSize:w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;// 如果没找到，就选最小的size
        }
        return list.get(i);
    }

    public Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, sizeComparator);

        int i = 0;
        for (Size s : list) {
            if ((s.width >= minWidth) && equalRate(s, th)) {
                Log.i(this, "PictureSize : w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;// 如果没找到，就选最小的size
        }
        return list.get(i);
    }

    public boolean equalRate(Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        } else {
            return false;
        }
    }

    public class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Size lhs, Size rhs) {
            // TODO Auto-generated method stub
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }

    }

    /**
     * 打印支持的previewSizes
     *
     * @param params
     */
    public void printSupportPreviewSize(Camera.Parameters params) {
        List<Size> previewSizes = params.getSupportedPreviewSizes();
        for (int i = 0; i < previewSizes.size(); i++) {
            Size size = previewSizes.get(i);
            Log.v(this, "previewSizes:width = " + size.width + " height = " + size.height);
        }

    }

    /**
     * 打印支持的pictureSizes
     *
     * @param params
     */
    public void printSupportPictureSize(Camera.Parameters params) {
        List<Size> pictureSizes = params.getSupportedPictureSizes();
        for (int i = 0; i < pictureSizes.size(); i++) {
            Size size = pictureSizes.get(i);
            Log.v(this, "pictureSizes:width = " + size.width + " height = " + size.height);
        }
    }

    /**
     * 打印支持的聚焦模式
     *
     * @param params
     */
    public void printSupportFocusMode(Camera.Parameters params) {
        List<String> focusModes = params.getSupportedFocusModes();
        for (String mode : focusModes) {
            Log.v(this, "focusModes--" + mode);
        }
    }
}
