package com.stur.lib.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.MediaRecorder;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.stur.lib.Log;
import com.stur.lib.R;
import com.stur.lib.Utils;
import com.stur.lib.file.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class RecorderActivity extends Activity implements SurfaceHolder.Callback {
    private SurfaceView mSurfaceview;
    private Button mBtnStartStop;
    private boolean mStartedFlg = false;
    private MediaRecorder mRecorder;
    private SurfaceHolder mSurfaceHolder;

    private LocalSocket mReceiver, mSender;
    private LocalServerSocket mLss;
    private int mBuffLen = 500000;
    protected byte[] mBuffer = new byte[mBuffLen];

    boolean mRemoteTxFlag = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

        // 设置横屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // 选择支持半透明模式,在有surfaceview的activity中使用。
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        setContentView(R.layout.activity_recorder);

        mSurfaceview = (SurfaceView) findViewById(R.id.surfaceview);
        mBtnStartStop = (Button) findViewById(R.id.btnStartStop);

        if (mRemoteTxFlag) {
            try {
                mLss = new LocalServerSocket("H264");

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            new Thread(new Runnable() {
                public void run() {
                    initLocalSocket();
                    InputStream is;
                    /*
                    * try { is = mReceiver.getInputStream(); is.read(mBuffer,
                    * 12, 50); Log.d(this, "read success"); } catch
                    * (IOException e) { // TODO Auto-generated catch block
                    * e.printStackTrace(); }
                    */
                }
            }).start();
        }

        mBtnStartStop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!mStartedFlg) {
                    // Start
                    if (mRecorder == null) {
                        mRecorder = new MediaRecorder(); // Create MediaRecorder
                    }
                    try {
                        // Set audio and video source and encoder
                        // 这两项需要放在setOutputFormat之前
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

                        // Set output file format
                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

                        // 这两项需要放在setOutputFormat之后
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);

                        mRecorder.setVideoSize(320, 240);
                        mRecorder.setVideoFrameRate(20);
                        mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

                        if (mRemoteTxFlag) {
                            if (mSender != null) {
                                mRecorder.setOutputFile(mSender.getFileDescriptor()); // 设置以流方式输出
                            }
                        } else {
                            String path = FileUtils.getDataPath(RecorderActivity.this);
                            File dir = new File(path);
                            if (!dir.exists()) {
                                dir.mkdir();
                            }
                            path = dir + "/" + "recordtest" + Utils.getDate() + ".3gp";
                            mRecorder.setOutputFile(path);
                        }

                        mRecorder.setMaxDuration(0);// called after
                        // setOutputFile before
                        // prepare,if zero or
                        // negation,disables the
                        // limit
                        mRecorder.setMaxFileSize(0);// called after
                        // setOutputFile before
                        // prepare,if zero or
                        // negation,disables the
                        // limit

                        Log.d(this, "bf mRecorder.prepare()");
                        mRecorder.prepare();
                        Log.d(this, "af mRecorder.prepare()");
                        Log.d(this, "bf mRecorder.start()");
                        mRecorder.start(); // Recording is now started
                        Log.d(this, "af mRecorder.start()");
                        mStartedFlg = true;
                        mBtnStartStop.setText("Stop");
                        Log.d(this, "Start recording ...");
                    } catch (Exception e) {
                        Log.d(this, "onClick Exception: " + e);
                        e.printStackTrace();
                    }
                } else {
                    // stop
                    if (mStartedFlg) {
                        try {
                            Log.d(this, "Stop recording ...");
                            Log.d(this, "bf mRecorder.stop(");
                            mRecorder.stop();
                            Log.d(this, "af mRecorder.stop(");
                            mRecorder.reset(); // You can reuse the object by
                            // going back to
                            // setAudioSource() step
                            mBtnStartStop.setText("Start");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mStartedFlg = false; // Set button status flag
                }
            }
        });

        SurfaceHolder holder = mSurfaceview.getHolder();// 取得holder

        holder.addCallback(this); // holder加入回调接口

        // setType必须设置，要不出错.
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
        // 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder
        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        // 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder
        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        // surfaceDestroyed的时候同时对象设置为null
        mSurfaceview = null;
        mSurfaceHolder = null;
        if (mRecorder != null) {
            mRecorder.release(); // Now the object cannot be reused
            mRecorder = null;
            Log.d(this, "surfaceDestroyed release mRecorder");
        }
    }

    private void initLocalSocket() {
        mReceiver = new LocalSocket();
        try {
            synchronized (mLss) {
                mReceiver.connect(new LocalSocketAddress("H264"));
                mReceiver.setReceiveBufferSize(500000);
                mReceiver.setSendBufferSize(500000);
                mSender = mLss.accept();
                mSender.setReceiveBufferSize(500000);
                mSender.setSendBufferSize(500000);
                Log.d(this, "initLocalSocket success!");
            }

        } catch (IOException e1) {
            e1.printStackTrace();
            Log.e(this, "localSocket error:" + e1.getMessage());
        }
    }
}