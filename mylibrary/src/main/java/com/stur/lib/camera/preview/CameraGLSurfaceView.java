package com.stur.lib.camera.preview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.stur.lib.Log;
import com.stur.lib.camera.CameraInterface;
import com.stur.lib.file.FileUtils;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;
import android.hardware.Camera;

public class CameraGLSurfaceView extends GLSurfaceView
        implements Renderer, SurfaceTexture.OnFrameAvailableListener, Camera.PreviewCallback {

    Context mContext;
    SurfaceTexture mSurface;
    int mTextureID = -1;
    DirectDrawer mDirectDrawer;

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // TODO Auto-generated method stub
        Log.v(this, "onSurfaceCreated...");
        mTextureID = createTextureID();
        mSurface = new SurfaceTexture(mTextureID);
        mSurface.setOnFrameAvailableListener(this);
        mDirectDrawer = new DirectDrawer(mTextureID);
        CameraInterface.getInstance(mContext).doOpenCamera(null);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // TODO Auto-generated method stub
        Log.v(this, "onSurfaceChanged...");
        GLES20.glViewport(0, 0, width, height);
        if (!CameraInterface.getInstance(mContext).isPreviewing()) {
            CameraInterface.getInstance(mContext).doStartPreview(mSurface, 1.33f, this);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // TODO Auto-generated method stub
        Log.v(this, "onDrawFrame...");
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mSurface.updateTexImage();
        float[] mtx = new float[16];
        mSurface.getTransformMatrix(mtx);
        mDirectDrawer.draw(mtx);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        CameraInterface.getInstance(mContext).doStopCamera();
    }

    private int createTextureID() {
        int[] texture = new int[1];

        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return texture[0];
    }

    public SurfaceTexture _getSurfaceTexture() {
        return mSurface;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        // TODO Auto-generated method stub
        Log.v(this, "onFrameAvailable...");
        this.requestRender();
    }

    byte[] mCameraNV21Byte;

    @Override
    public void onPreviewFrame(byte[] data, Camera arg1) {
        // the data format is YUV420SP for default
        // Log.v(this, "onPreviewFrame E: " + Thread.currentThread() + ", len =
        // " + data.length + ", data: " + Arrays.toString(data));
        long tpe = System.currentTimeMillis();
        mCameraNV21Byte = data;

        try {
            File savefile = FileUtils.getIncreasedSaveFile(FileUtils.getDataPath(mContext) + "video.avi");
            FileOutputStream fos = new FileOutputStream(savefile);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long tpx = System.currentTimeMillis();
        Log.v(this, "onPreviewFrame X: time consuption " + (tpx - tpe));
    }

}
