package com.stur.lib.activity;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;

import com.stur.lib.R;
import com.stur.lib.camera.CameraInterface;
import com.stur.lib.camera.CameraInterface.CamOpenOverCallback;
import com.stur.lib.camera.preview.CameraTextureView;
import com.stur.lib.display.DisplayUtils;

public class CameraTextureActivity extends Activity implements CamOpenOverCallback {
    CameraTextureView textureView = null;
    ImageButton shutterBtn;
    float previewRate = -1f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread openThread = new Thread(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                CameraInterface.getInstance(CameraTextureActivity.this).doOpenCamera(CameraTextureActivity.this);
            }
        };
        openThread.start();
        setContentView(R.layout.activity_camera_texture);
        initUI();
        initViewParams();
        textureView.setAlpha(1.0f);

        shutterBtn.setOnClickListener(new BtnListeners());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera, menu);
        return true;
    }

    private void initUI(){
        textureView = (CameraTextureView)findViewById(R.id.camera_textureview);
        shutterBtn = (ImageButton)findViewById(R.id.btn_shutter);
    }
    private void initViewParams(){
        LayoutParams params = textureView.getLayoutParams();
        Point p = DisplayUtils.getScreenMetrics(this);
        params.width = p.x;
        params.height = p.y;
        previewRate = DisplayUtils.getScreenRate(this); //默认全屏的比例预览
        textureView.setLayoutParams(params);

        //手动设置拍照ImageButton的大小为120dip×120dip,原图片大小是64×64
        LayoutParams p2 = shutterBtn.getLayoutParams();
        p2.width = DisplayUtils.dip2px(this, 80);
        p2.height = DisplayUtils.dip2px(this, 80);;
        shutterBtn.setLayoutParams(p2);

    }

    @Override
    public void cameraHasOpened() {
        // TODO Auto-generated method stub
        SurfaceTexture surface = textureView._getSurfaceTexture();
        CameraInterface.getInstance(CameraTextureActivity.this).doStartPreview(surface, previewRate, null);
    }
    private class BtnListeners implements OnClickListener{

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v.getId() == R.id.btn_shutter) {
                CameraInterface.getInstance(CameraTextureActivity.this).doTakePicture();
            }
        }

    }

}
