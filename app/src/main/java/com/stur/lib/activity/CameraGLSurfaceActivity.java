package com.stur.lib.activity;

import javax.microedition.khronos.opengles.GL10;

import com.stur.chest.R;
import com.stur.lib.DisplayUtil;
import com.stur.lib.Log;
import com.stur.lib.camera.CameraInterface;
import com.stur.lib.camera.preview.CameraGLSurfaceView;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;

public class CameraGLSurfaceActivity extends Activity {
    private CameraGLSurfaceView mGLSFView = null;
    ImageButton shutterBtn;

    long resumeTimeStamp;
    boolean isFirstOnFrameAvailable;
    long frameAvailableTimeStamp;

    int cameraWidth = 1280;
    int cameraHeight = 720;

    Camera mCamera;
    int mCurrentCameraType;
    int mFrameId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera_glsfview);


        mGLSFView = (CameraGLSurfaceView) findViewById(R.id.camera_glsfvw);

        shutterBtn = (ImageButton)findViewById(R.id.btn_shutter);

        initViewParams();

        shutterBtn.setOnClickListener(new BtnListeners());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera, menu);
        return true;
    }

    private void initViewParams(){
        //手动设置拍照ImageButton的大小为120dip×120dip,原图片大小是64×64
        LayoutParams p2 = shutterBtn.getLayoutParams();
        p2.width = DisplayUtil.dip2px(this, 80);
        p2.height = DisplayUtil.dip2px(this, 80);;
        shutterBtn.setLayoutParams(p2);

    }

    private class BtnListeners implements OnClickListener{

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch(v.getId()){
                case R.id.btn_shutter:
                    CameraInterface.getInstance(CameraGLSurfaceActivity.this).doTakePicture();
                    break;
                default:break;
            }
        }
    }
}
