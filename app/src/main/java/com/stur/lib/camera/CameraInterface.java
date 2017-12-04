package com.stur.lib.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;

import com.stur.lib.ImageUtil;
import com.stur.lib.Log;
import com.stur.lib.file.FileUtils;

import java.io.IOException;
import java.util.List;

public class CameraInterface {
    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;
    private float mPreviwRate = -1f;
    private static CameraInterface mCameraInterface;
    private Context mContext;

    public interface CamOpenOverCallback{
        public void cameraHasOpened();
    }

    private CameraInterface(Context context){
        mContext = context;
    }
    public static synchronized CameraInterface getInstance(Context context){
        if(mCameraInterface == null){
            mCameraInterface = new CameraInterface(context);
        }
        return mCameraInterface;
    }

    public void doOpenCamera(CamOpenOverCallback callback){
        Log.i(this, "Camera open....");
        if(mCamera == null){
            mCamera = Camera.open();
            Log.i(this, "Camera open end....");
            if(callback != null){  //if callback is null, module can start preview by the callback of onSurfaceChanged
                callback.cameraHasOpened();
            }
        }else{
            Log.i(this, "Camera open exception!!!");
            doStopCamera();
        }
    }
    /**ʹ��Surfaceview����Ԥ��
    * @param holder
    * @param previewRate
    */
    public void doStartPreview(SurfaceHolder holder, float previewRate){
        Log.i(this, "doStartPreview...");
        if(isPreviewing){
            mCamera.stopPreview();
            return;
        }
        if(mCamera != null){


            try {
                mCamera.setPreviewDisplay(holder);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            initCamera(previewRate);
        }
    }
    /**ʹ��TextureViewԤ��Camera
    * @param surface
    * @param previewRate
    */
    public void doStartPreview(SurfaceTexture surface, float previewRate, PreviewCallback pcb){
        Log.i(this, "doStartPreview...");
        if(isPreviewing){
            mCamera.stopPreview();
            return;
        }
        if(mCamera != null){
            if (pcb != null) {
                //mCamera.setPreviewCallback(pcb);
                mCamera.setPreviewCallbackWithBuffer(pcb);
            }

            try {
                mCamera.setPreviewTexture(surface);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            initCamera(previewRate);
        }

    }

    /**
    * ֹͣԤ�����ͷ�Camera
    */
    public void doStopCamera(){
        if(null != mCamera)
        {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mPreviwRate = -1f;
            mCamera.release();
            mCamera = null;
        }
    }
    /**
    * ����
    */
    public void doTakePicture(){
        if(isPreviewing && (mCamera != null)){
            mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
        }
    }

    public boolean isPreviewing(){
        return isPreviewing;
    }

    private void initCamera(float previewRate){
    if(mCamera != null){

            mParams = mCamera.getParameters();
            mParams.setPictureFormat(PixelFormat.JPEG);//�������պ�洢��ͼƬ��ʽ
            CameraUtils.getInstance().printSupportPictureSize(mParams);
            CameraUtils.getInstance().printSupportPreviewSize(mParams);
            //����PreviewSize��PictureSize
            Size pictureSize = CameraUtils.getInstance().getPropPictureSize(
                    mParams.getSupportedPictureSizes(),previewRate, 800);
            mParams.setPictureSize(pictureSize.width, pictureSize.height);
            Size previewSize = CameraUtils.getInstance().getPropPreviewSize(
                    mParams.getSupportedPreviewSizes(), previewRate, 800);
            mParams.setPreviewSize(previewSize.width, previewSize.height);

            mCamera.setDisplayOrientation(90);

            CameraUtils.getInstance().printSupportFocusMode(mParams);
            List<String> focusModes = mParams.getSupportedFocusModes();
            if(focusModes.contains("continuous-video")){
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(mParams);
            mCamera.startPreview();//����Ԥ��



            isPreviewing = true;
            mPreviwRate = previewRate;

            mParams = mCamera.getParameters(); //����getһ��
            Log.i(this, "final PreviewSize--With = " + mParams.getPreviewSize().width
                    + "Height = " + mParams.getPreviewSize().height);
            Log.i(this, "final PictureSize--With = " + mParams.getPictureSize().width
                    + ", Height = " + mParams.getPictureSize().height);
        }
    }
    /*Ϊ��ʵ�����յĿ������������ձ�����Ƭ��Ҫ���������ص�����*/
    ShutterCallback mShutterCallback = new ShutterCallback()
    //���Ű��µĻص������������ǿ����������Ʋ��š����ꡱ��֮��Ĳ�����Ĭ�ϵľ������ꡣ
    {
        public void onShutter() {
            // TODO Auto-generated method stub
            Log.i(this, "myShutterCallback:onShutter...");
        }
    };
    PictureCallback mRawCallback = new PictureCallback()
    // �����δѹ��ԭ���ݵĻص�,����Ϊnull
    {

        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(this, "myRawCallback:onPictureTaken...");

        }
    };
    PictureCallback mJpegPictureCallback = new PictureCallback()
    //��jpegͼ�����ݵĻص�,����Ҫ��һ���ص�
    {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(this, "myJpegCallback:onPictureTaken...");
            Bitmap b = null;
            if(null != data){
                b = BitmapFactory.decodeByteArray(data, 0, data.length);//data���ֽ����ݣ����������λͼ
                mCamera.stopPreview();
                isPreviewing = false;
            }
            //����ͼƬ��sdcard
            if(null != b)
            {
                //����FOCUS_MODE_CONTINUOUS_VIDEO)֮��myParam.set("rotation", 90)ʧЧ��
                //ͼƬ��Ȼ������ת�ˣ�������Ҫ��ת��
                Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
                FileUtils.saveBitmap(mContext, rotaBitmap);
            }
            //�ٴν���Ԥ��
            mCamera.startPreview();
            isPreviewing = true;
        }
    };


}
