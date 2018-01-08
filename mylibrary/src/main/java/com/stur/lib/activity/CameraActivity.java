package com.stur.lib.activity;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;

import com.stur.lib.DisplayUtil;
import com.stur.lib.R;
import com.stur.lib.camera.CameraInterface;
import com.stur.lib.camera.CameraInterface.CamOpenOverCallback;
import com.stur.lib.camera.preview.CameraSurfaceView;

public class CameraActivity extends Activity implements CamOpenOverCallback {
    private static final String TAG = "yanzi";
    CameraSurfaceView surfaceView = null;
    ImageButton shutterBtn;
    float previewRate = -1f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread openThread = new Thread(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                CameraInterface.getInstance(CameraActivity.this).doOpenCamera(CameraActivity.this);
            }
        };
        openThread.start();
        setContentView(R.layout.activity_camera);
        initUI();
        initViewParams();

        shutterBtn.setOnClickListener(new BtnListeners());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera, menu);
        return true;
    }

    private void initUI(){
        surfaceView = (CameraSurfaceView)findViewById(R.id.camera_surfaceview);
        shutterBtn = (ImageButton)findViewById(R.id.btn_shutter);
    }
    private void initViewParams(){
        LayoutParams params = surfaceView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width = p.x;
        params.height = p.y;
        previewRate = DisplayUtil.getScreenRate(this);  //preview for full screen
        surfaceView.setLayoutParams(params);


        //set the shutter button size of 120dip×120dip, ori picture is 64×64
        LayoutParams p2 = shutterBtn.getLayoutParams();
        p2.width = DisplayUtil.dip2px(this, 80);
        p2.height = DisplayUtil.dip2px(this, 80);;
        shutterBtn.setLayoutParams(p2);

    }

    @Override
    public void cameraHasOpened() {
        // TODO Auto-generated method stub
        SurfaceHolder holder = surfaceView.getSurfaceHolder();
        CameraInterface.getInstance(this).doStartPreview(holder, previewRate);
    }
    private class BtnListeners implements OnClickListener{

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v.getId() == R.id.btn_shutter){
                CameraInterface.getInstance(CameraActivity.this).doTakePicture();
            }
        }
    }
}
