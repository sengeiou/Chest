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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.Surface;

import com.stur.lib.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
     * 利用intent的方式拉起相机应用，拍照后存储到指定URI
     * 示例： CameraUtils.takePhoto(context, CameraUtils.createImageFile(Environment.getExternalStorageDirectory() + "/stur/"), 181);
     * 注意：AndroidO上的system用户的应用通过FileProvider拉起相机时会报错，见 FileProvider Issues 20190506
     * @param context
     * @param photoFile 例如 new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera", getPhotoFileName());
     * @param reqCode CAMERA_WITH_DATA = 181;
     */
    public static void takePhoto(Context context, File photoFile, int reqCode) {
        Log.d(getTag(), "takePhoto E: photoFile = " + photoFile.getPath() + ", reqCode = " + reqCode);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);

        // 在启动第三方APK里的Activity之前，需要确定调用是否可以解析为一个Activity
        // 通过Intent的resolveActivity方法，并向该方法传入包管理器可以对包管理器进行查询以确定是否有Activity能够启动该Intent
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            Uri outPutUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0及以上
                outPutUri = FileProvider.getUriForFile(context.getApplicationContext(), "com.stur.chest.fileprovider", photoFile);

                // 如下两种授权方式二选一
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                //查询Camera默认应用的包名，如果知道是 org.codeaurora.snapcam 的话可以直接填，注意用户可能会修改默认相机应用
                //context.grantUriPermission("org.codeaurora.snapcam", outPutUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                /*List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    Log.d(getTag(), "takePhoto: packageName = " + packageName);
                    context.grantUriPermission(packageName, outPutUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }*/
            } else {
                outPutUri = Uri.fromFile(photoFile);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
            context.startActivityForResult("com.stur.chest", intent, reqCode, null);
        }
    }

    /**
     * 根据当前系统时间对照片命名
     * @return
     */
    public static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    /**
     * 在指定路径（/sdcard/DCIM/Camera/）下创建空照片文件（保证目录存在），按照 yyyyMMdd_HHmmss_tmp 格式命名
     * @return
     * @throws IOException
     */
    public static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(     //这个函数会在文件名后面加一些随机数以做区分
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    /**
     * 通过指定路径创建空照片文件（保证路径存在）,按照 IMG_yyyyMMdd_HHmmss 的格式存储
     * @param dir
     * @return
     */
    public static File createImageFile(String dir) {
        // 这里必须先保证目录存在，否则拍照后的照片无法保存
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";
        File image = new File(dir + imageFileName);
        return image;
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
