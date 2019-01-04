package com.stur.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.UserHandle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.stur.chest.R;

import java.io.File;


public class ChipsailingFingerprintActivity extends Activity {

    private String Driver_name="dev/cs_spi";

    private static final String TAG = "citfingerprint";

    public static final String FINGER_RESULT = "test_result";
    private static final int REQUEST_CODE = 1;
    private static final int RESULT_CODE = 1;

    private static final int TEST_FAIL = 0;
    private static final int TEST_PASS = 1;

    private boolean mIsTestOk = false;

    private Button mTestButton = null;
    // add by CIT SYSTEM
    private Button btn_passBtn = null;
    private Button btn_failBtn = null;
    private Button btn_reTestBtn = null;

    private Vibrator vib_vibrator = null;

    private TextView mResultTV = null;

    private static int TEST_ONE = 0;

    private static boolean mhasClickIc = false;

    private static final int UPDATE_RESULT = 1;
    //private static final int UPDATE_RESULT = 11;

    private static final int FINGERPRINT_TEMP_EXIST = 1001;
    private static final int FINGERPRINT_ERROR_CANCELED = 5;
    private static final int FINGERPRINT_ERROR_HW_UNAVAILABLE = 2;

    private static final int START_INIT_DATA = 2;

    private static final int START_TO_ENROLL = 1;

    private Intent mData = null;
    private int mEnrollmentSteps = -1;
    private int mEnrollmentRemaining = -1;
    private CancellationSignal mEnrollmentCancel = null;
    private boolean mEnrolling;

    private FingerprintManager mFingerprintManager;
    private int userId = 0;

    private byte[] mToken = new byte[69];
    static long[] pattern = { 200, 50 };
    private boolean hascancell = false;
    private boolean ispause = false;
    private HandlerThread mHandlerThread = null;
    private Handler handler = null;

    private Handler mUIHandler = null;
	
	long challenge;

    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);
        file=new File(Driver_name);
        ispause = false;
        if (0 != init()) {
            Log.e(TAG, "init failed ");
        }

    }

    @Override
    protected void onStart() {
        
        super.onStart();
    }

    @Override
    protected void onResume() {
        if (mFingerprintManager.isHardwareDetected()) {
            if (file!=null&&file.exists()){
                if (mFingerprintManager.getEnrolledFingerprints().size()<5) {
                    Log.i(TAG, "onResume 0000");
                    mIsTestOk = false;
                    mhasClickIc = false;
                    ispause = false;
                    TEST_ONE = 1;
                    mResultTV.setText("Initializing，please wait");
                    handler.sendEmptyMessage(START_INIT_DATA);
                }else{
                    mResultTV.setTextColor(Color.rgb(255, 0, 0));
                    mResultTV.setText("The fingerprint has been recorded for 5 and cannot be used for CIT testing");
                    btn_reTestBtn.setEnabled(false);
                }
            }else{
                mResultTV.setTextColor(Color.rgb(255, 0, 0));
                mResultTV.setText("Not Chipsailing IC");
                btn_reTestBtn.setEnabled(false);
            }

        }else{
            mResultTV.setTextColor(Color.rgb(255, 0, 0));
            mResultTV.setText("finger sensor not detected!!");
            btn_reTestBtn.setEnabled(false);
            Toast.makeText(this,
                    "finger sensor not detected!!", Toast.LENGTH_SHORT)
                    .show();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause 11111");
        ispause = true;
        setResultAndClean();

        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop11111");
        handler.removeMessages(START_INIT_DATA);
        btn_passBtn.setEnabled(false);
        btn_failBtn.setEnabled(true);
        btn_passBtn.setTextColor(Color.rgb(0, 255, 0));
        btn_failBtn.setTextColor(Color.rgb(255, 0, 0));
        mResultTV.setText("Please press the fingerprint chip");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        vib_vibrator.cancel();
        mHandlerThread = null;
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "has permission");
                    startEnrollment();
                } else {
                    Log.e(TAG, "no permission");
                }

                break;

            default:
                break;
        }

    }

    private class TestButtonClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            if (null == view)
                return;

            switch (view.getId()) {
                case R.id.btn_retest:
                    TEST_ONE = 1;
                    Log.i(TAG, "click btn_retest 11111retest  ");
                    if (mhasClickIc) {
                        btn_passBtn.setEnabled(false);
                        btn_failBtn.setEnabled(true);
                        startEnrollment();
                    }
                    hascancell = false;

                    break;

                case R.id.btn_pass:

                    mData.putExtra(FINGER_RESULT, TEST_PASS);

                    setActivityResult(RESULT_CODE, mData);
                    finish();
                    break;
                case R.id.btn_fail:

                    mData.putExtra(FINGER_RESULT, TEST_FAIL);

                    setActivityResult(RESULT_CODE, mData);
                    finish();
                    break;
                default:
                    break;

            }

        }

    }
    
    private void initEnrollData() {

        userId = UserHandle.myUserId();
        challenge= mFingerprintManager.preEnroll();
        handler.sendEmptyMessage(START_TO_ENROLL);

    }

    private int startEnrollment() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.MANAGE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, " check permission");

                if (shouldShowRequestPermissionRationale(Manifest.permission.MANAGE_FINGERPRINT)) {

                    Toast.makeText(ChipsailingFingerprintActivity.this,
                            "no permnission ,Please get permission",
                            Toast.LENGTH_SHORT).show();
                } else {
                    requestPermissions(
                            new String[] { Manifest.permission.MANAGE_FINGERPRINT },
                            REQUEST_CODE);
                }

            } else {
                // mUIHandler.sendEmptyMessageDelayed(1001, 500);
                mUIHandler.sendEmptyMessage(1001);
            }
        }

        return 0;

    }

    private int doEnrollTest() {
        //if (userId != UserHandle.USER_NULL) {
        //	mFingerprintManager.setActiveUser(userId);
        //}
        mResultTV.setText("Please press the fingerprint chip");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mEnrollmentCancel = new CancellationSignal();
        }

        try {

			byte tmp[] = getBytes(challenge);
				for(int i = 0; i < tmp.length; i++){
					mToken[i+1] = tmp[i];
			}
            //android M don't need userId Params  AndroidN,O need userId Params
            mFingerprintManager.enroll(mToken, mEnrollmentCancel, 0, userId,
                    mEnrollmentCallback);


        } catch (Exception e) {
            Log.e("fptest", "start enrollfail" + e.toString());
            mEnrolling = false;
        }
        mEnrolling = true;

        return 0;
    }

    private int init() {

        btn_passBtn = (Button) findViewById(R.id.btn_pass);
        btn_failBtn = (Button) findViewById(R.id.btn_fail);
        btn_reTestBtn = (Button) findViewById(R.id.btn_retest);

        mResultTV = (TextView) findViewById(R.id.test_result);
        vib_vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mResultTV.setTextColor(Color.rgb(0, 255, 0));
        btn_passBtn.setTextColor(Color.rgb(0, 255, 0));
        btn_failBtn.setTextColor(Color.rgb(255, 0, 0));
        mData = new Intent();

        if (null == mResultTV) {
            Log.e(TAG, "init view failed");
            return -1;
        }

        btn_passBtn.setEnabled(false);
        btn_failBtn.setEnabled(true);

        TestButtonClickListener clickListner = new TestButtonClickListener();
        btn_passBtn.setOnClickListener(clickListner);
        btn_failBtn.setOnClickListener(clickListner);
        btn_reTestBtn.setOnClickListener(clickListner);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFingerprintManager = getSystemService(FingerprintManager.class);
        }

        mHandlerThread = new HandlerThread("CSFinger_CIT");
        mHandlerThread.start();

        handler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == START_TO_ENROLL) {
                    startEnrollment();
                } else if (msg.what == START_INIT_DATA) {
                    initEnrollData();
                }
            }
        };

        mUIHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1001) {
                    doEnrollTest();
                }
                if (msg.what == 1002) {
                    onBackPressed();
                }
            }
        };
        return 0;
    }

    private void delays(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {

        }
    }

    public FingerprintManager.EnrollmentCallback mEnrollmentCallback = new FingerprintManager.EnrollmentCallback() {

        @Override
        public void onEnrollmentProgress(int remaining) {
            Log.d(TAG, "onEnrollmentProgress remaining=" + remaining);
            mhasClickIc = true;
            if (mEnrollmentSteps == -1) {
                mEnrollmentSteps = remaining;
            }
            // 检测到一次之后就直接取消录入
            cancelEnrollment();
            hascancell = true;
            mEnrollmentRemaining = remaining;
            onEnrollmentProgressChange(mEnrollmentSteps, remaining);
            if (remaining == 0) {
                // complete
                // onEnrollmentComplete();
                Log.e(TAG, "enroll completed ....");
            }
           // mUIHandler.sendEmptyMessageDelayed(1002, 200);
        }

        @Override
        public void onEnrollmentHelp(int helpMsgId, CharSequence helpString) {
            Log.e(TAG, "CustomFingerManager onEnrollmentHelp  helpMsgId:"+helpMsgId+"helpString");

            // onEnrollmentHelp(helpString);
            Log.e(TAG, "onEnrollmentHelp======" + helpString);
            // mTestButton.setBackgroundColor(Color.GRAY);
            if (TEST_ONE == UPDATE_RESULT&&(helpMsgId==1105||helpMsgId==1106)) {
                mIsTestOk = true;

                mhasClickIc = true;
                mResultTV.setText("Pass");
                mResultTV.setTextColor(Color.rgb(0, 255, 0));

                btn_passBtn.setEnabled(true);
                btn_failBtn.setEnabled(false);

                // mTestButton.setClickable(true);
                cancelEnrollment();
                hascancell = true;
                TEST_ONE--;
              //  mUIHandler.sendEmptyMessageDelayed(1002, 200);
            }

        }

        @Override
        public void onEnrollmentError(int errMsgId, CharSequence errString) {
            Log.e(TAG, "onEnrollmentError=" + errMsgId + "  errString="
                    + errString);

            if (TEST_ONE == UPDATE_RESULT && (errMsgId==1105||errMsgId==1106)) {
                mhasClickIc = true;
                mResultTV.setText("Pass");
                mResultTV.setTextColor(Color.rgb(0, 255, 0));

                btn_passBtn.setEnabled(true);
                btn_failBtn.setEnabled(false);
                mIsTestOk = true;
                // mTestButton.setClickable(true);
                cancelEnrollment();
                hascancell = true;
                TEST_ONE--;
              //  mUIHandler.sendEmptyMessageDelayed(1002, 200);
            }
        }
    };

    public void onEnrollmentProgressChange(int steps, int remaining) {

        Log.i(TAG, "onEnrollmentProgressChange step = " + steps + "remaining ="
                + remaining);
        Log.i(TAG, "onEnrollmentProgressChange TEST_ONE = " + TEST_ONE);
        // mTestButton.setBackgroundColor(Color.GRAY);
        if (remaining > 0 && TEST_ONE == UPDATE_RESULT) {
            cancelEnrollment();
            Log.i(TAG, "onEnrollmentProgressChange pass_test = ");
            mIsTestOk = true;
            mResultTV.setText("Pass");
            mResultTV.setTextColor(Color.rgb(0, 255, 0));
            // mTestButton.setClickable(true);
            vib_vibrator.vibrate(pattern, -1);

            btn_passBtn.setEnabled(true);
            btn_failBtn.setEnabled(false);

        }

        TEST_ONE--;

    }

    public void setActivityResult(int requestCode, Intent data) {
        setResult(requestCode, data);
    }

    public void cancelEnrollment() {
        if (mEnrolling) {
            mEnrollmentCancel.cancel();
            mEnrolling = false;
            mEnrollmentSteps = -1;
            Log.i(TAG, "cancel sucess11");
        }
    }

    @Override
    public void onBackPressed() {

        if (mIsTestOk) {
            mData.putExtra(FINGER_RESULT, TEST_PASS);
            Log.e(TAG, "FINGER_RESULT:" + TEST_PASS);
        } else {
            mData.putExtra(FINGER_RESULT, TEST_FAIL);
            Log.e(TAG, "FINGER_RESULT:" + TEST_FAIL);
        }
        setActivityResult(RESULT_CODE, mData);
        setResultAndClean();
        super.onBackPressed();
    }

    private void setResultAndClean() {

        cancelEnrollment();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
	
	 public byte[] getBytes(long data){
        byte[] bytes = new byte[28];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        bytes[2] = (byte) ((data >> 16) & 0xff);
        bytes[3] = (byte) ((data >> 24) & 0xff);
        bytes[4] = (byte) ((data >> 32) & 0xff);
        bytes[5] = (byte) ((data >> 40) & 0xff);
        bytes[6] = (byte) ((data >> 48) & 0xff);
        bytes[7] = (byte) ((data >> 56) & 0xff);
		
		bytes[8] = (byte) (0 & 0xff);
        bytes[9] = (byte) (0 & 0xff);
        bytes[10] = (byte) (0 & 0xff);
        bytes[11] = (byte) (0 & 0xff);
		bytes[12] = (byte) (0 & 0xff);
        bytes[13] = (byte) (0 & 0xff);
        bytes[14] = (byte) (0 & 0xff);
        bytes[15] = (byte) (2 & 0xff);
		
        bytes[24] = (byte) (0 & 0xff);
        bytes[25] = (byte) (0 & 0xff);
        bytes[26] = (byte) (0 & 0xff);
        bytes[27] = (byte) (1 & 0xff);
        return bytes;
    }
}

