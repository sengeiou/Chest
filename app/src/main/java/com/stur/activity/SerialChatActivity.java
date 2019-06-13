/*
 * 串口测试程序
 * 需要权限 android.permission.SERIAL_PORT
 */

package com.stur.activity;

import android.app.Activity;
import android.content.Context;
import android.hardware.SerialManager;
import android.hardware.SerialPort;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.stur.lib.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.stur.chest.R;


public class SerialChatActivity extends Activity implements View.OnClickListener {
    private TextView mReceiverEditText;
    private EditText mSendEditText, mRepeatTime;
    private Button mSendButton, mClearButton;
    private ByteBuffer mInputBuffer;
    private ByteBuffer mOutputBuffer, mOutputTestBuffer;
    private SerialManager mSerialManager;
    private SerialPort mSerialPort;
    private boolean mPermissionRequestPending, mIsHex, mHaveSelect;
    private RadioGroup mRadioGroup;
    private Spinner mPathSpinner, mBtlSpinner;
    private Switch mSwitch;
    private Thread mThread;
    private int mPath = 0;
    private int mBtl = 921600;
    private static final int MESSAGE_LOG = 1;
    private static String hexString = "0123456789ABCDEF";
    Handler hander = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSerialManager = (SerialManager) getSystemService(Context.SERIAL_SERVICE);
        setContentView(R.layout.activity_serial_chat);
        mReceiverEditText = (TextView) findViewById(R.id.message_receiver);
        mReceiverEditText.setMovementMethod(ScrollingMovementMethod.getInstance());
        mSendEditText = (EditText) findViewById(R.id.send_message_edit);
        mRepeatTime = (EditText) findViewById(R.id.repeat_time);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup_id);
        mPathSpinner = (Spinner) findViewById(R.id.path);
        mBtlSpinner = (Spinner) findViewById(R.id.btl);
        mSwitch = (Switch) findViewById(R.id.auto_switch);
        mSwitch.setChecked(false);
        mSwitch.setOnCheckedChangeListener(mAutoListener);
        mPathSpinner.setOnItemSelectedListener(mPathSelectedListener);
        mBtlSpinner.setOnItemSelectedListener(mBtlSelectedListener);
        mRadioGroup.check(0);
        mRadioGroup.setOnCheckedChangeListener(mListener);
        mSendButton = (Button) findViewById(R.id.send);
        mClearButton = (Button) findViewById(R.id.clear);
        mSendButton.setOnClickListener(this);
        mClearButton.setOnClickListener(this);
        mOutputTestBuffer = ByteBuffer.allocate(4);
        if (false) {
            mInputBuffer = ByteBuffer.allocateDirect(1024);
            mOutputBuffer = ByteBuffer.allocateDirect(1024);
        } else {
            mInputBuffer = ByteBuffer.allocate(1024);
            mOutputBuffer = ByteBuffer.allocate(1024);
        }
    }

    @Override
    public void onResume() {
        initPort();
        super.onResume();
    }

    private void initPort() {
        String[] ports = mSerialManager.getSerialPorts();
        Log.e(this, "initPort size = " + (ports != null && ports.length > 0 ? ports.length : 0));
        if (ports != null && ports.length > 0) {
            try {
                Log.e(this, "initPort ports[mPath] = " + ports[mPath] + " mBtl =" + mBtl);
                mSerialPort = mSerialManager.openSerialPort(ports[mPath], mBtl);
                Log.e(this, "initPort mSerialPort = " + (mSerialPort == null));
                if (mSerialPort != null) {
                    if (mThread != null) {
                        try {
                            mThread.interrupt();
                            mThread = null;
                        } catch (Exception e) {
                            Log.e(this, "initPort interrupt thread error ");
                        }
                    }
                    mThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(this, "initPort run");
                            int ret = 0;
                            byte[] buffer = new byte[1024];
                            while (ret >= 0) {
                                try {
                                    Log.d(this, "initPort calling read");
                                    mInputBuffer.clear();
                                    if (mSerialPort == null && mThread != null) {
                                        mThread.interrupt();
                                        return;
                                    }
                                    ret = mSerialPort.read(mInputBuffer);
                                    Log.d(this, "initPort read returned " + ret);
                                    mInputBuffer.get(buffer, 0, ret);
                                } catch (IOException e) {
                                    Log.e(this, "initPort read failed", e);
                                    break;
                                }

                                if (ret > 0) {
                                    Message m = Message.obtain(mHandler, MESSAGE_LOG);
                                    String text = new String(buffer, 0, ret);
                                    Log.d(this, "initPort chat: " + text);
                                    m.obj = text;
                                    mHandler.sendMessage(m);
                                }
                            }
                            Log.d(this, "initPort thread out");
                        }
                    });
                    mThread.start();
                }
                Toast.makeText(SerialChatActivity.this, "切换节点成功!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.e(this, "initPort error = " + android.util.Log.getStackTraceString(e));
                Toast.makeText(SerialChatActivity.this, "切换节点失败!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        if (mThread != null) {
            mThread.interrupt();
        }
        if (mSerialPort != null) {
            try {
                mSerialPort.close();
            } catch (IOException e) {
            }
            mSerialPort = null;
        }
        super.onDestroy();
    }

    private OnItemSelectedListener mBtlSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String[] btls = SerialChatActivity.this.getResources().getStringArray(R.array.spinner2_entries);
            if (mBtl != Integer.valueOf(btls[pos])) {
                mBtl = Integer.valueOf(btls[pos]);
                try {
                    if (mSerialPort != null) {
                        mSerialPort.close();
                        mSerialPort = null;
                    }
                    mReceiverEditText.setText("");

                    mInputBuffer.clear();
                    Log.e(this, "mBtlSelectedListener");
                    initPort();
                } catch (IOException e) {
                    Log.e(this, "close mSerialPort error happend");
                    Log.e(this, "error = " + android.util.Log.getStackTraceString(e));
                    Toast.makeText(SerialChatActivity.this, "初始化异常，无法切换!", Toast.LENGTH_LONG).show();
                }

            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private OnItemSelectedListener mPathSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (pos != mPath) {
                mPath = pos;

                try {
                    if (mSerialPort != null) {
                        mSerialPort.close();
                        mSerialPort = null;
                    }
                    mReceiverEditText.setText("");
                    mInputBuffer.clear();
                    Log.e(this, "mPathSelectedListener");
                    initPort();
                } catch (IOException e) {
                    Log.e(this, "close mSerialPort error happend");
                    Log.e(this, "error = " + android.util.Log.getStackTraceString(e));
                    Toast.makeText(SerialChatActivity.this, "节点初始化异常，无法切换!", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    CompoundButton.OnCheckedChangeListener mAutoListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            Log.e(this, "mAutoListener mAutoListener");
            if (b) {
                if (mRepeatTime.getText() != null && !"".equals(mRepeatTime.getText().toString()) && mSendEditText.getText() != null && !"".equals(mSendEditText.getText().toString())) {
                    Log.e(this, "mAutoListener auto Send message");
                    hander.post(run);
                }
            } else {
                hander.removeCallbacks(run);
            }
        }
    };

    static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    Runnable run = new Runnable() {
        public void run() {
            if (mSerialPort != null) {
                try {
                    String text = mSendEditText.getText().toString();
                    Log.d(this, "write: " + text);
                    byte[] bytes = text.getBytes();
                    byte[] b = new byte[1];
                    b[0] = '\r';
                    byte[] c = concat(bytes, b);
                    mOutputBuffer.clear();
                    mOutputTestBuffer.clear();
                    mOutputBuffer.put(c);

                    //mOutputTestBuffer.put(b);
                    //mSerialPort.write(mOutputTestBuffer, b.length);
                    mSerialPort.write(mOutputBuffer, c.length);
                } catch (IOException e) {
                    Log.e(this, "write failed", e);
                }

            }
            if (mRepeatTime.getText() != null && !"".equals(mRepeatTime.getText().toString()) && mSendEditText.getText() != null && !"".equals(mSendEditText.getText().toString()))
                hander.postDelayed(this, Integer.valueOf(mRepeatTime.getText().toString()));
        }
    };

    private void refreshReceiverView(TextView textView, String msg) {

        int offset = textView.getLineCount() * textView.getLineHeight();
        if (offset > (textView.getHeight() - textView.getLineHeight() - 20)) {
            textView.scrollTo(0, offset - textView.getHeight() + textView.getLineHeight() + 20);
        }
    }

    private RadioGroup.OnCheckedChangeListener mListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id = group.getCheckedRadioButtonId();

            if (mReceiverEditText.getText() == null) {
                mReceiverEditText.setText("");
            }
            Log.e(this, "onCheckedChanged");
            switch (id) {
                case R.id.txt_id:
                    if (!mHaveSelect) {
                        break;
                    }
                    mReceiverEditText.setText(hexToTxt(mReceiverEditText.getText().toString()));
                    mIsHex = false;
                    break;
                case R.id.hex_id:
                    mIsHex = true;
                    mReceiverEditText.setText(txtToHex(mReceiverEditText.getText().toString()));
                    break;
            }
            mHaveSelect = true;
        }
    };

    public void onClick(View v) {
        if (v.getId() == R.id.clear) {
            mReceiverEditText.setText("");
        } else if (v.getId() == R.id.send) {
            if (mSerialPort != null) {
                try {
                    String text = mSendEditText.getText().toString();
                    Log.d(this, "onClick write: " + text);
                    byte[] bytes = text.getBytes();
                    byte[] b = new byte[1];
                    b[0] = '\r';
                    byte[] c = concat(bytes, b);
                    mOutputBuffer.clear();
                    mOutputTestBuffer.clear();
                    mOutputBuffer.put(c);
                    //mOutputTestBuffer.put(b);
                    //mSerialPort.write(mOutputTestBuffer, b.length);

                    mSerialPort.write(mOutputBuffer, c.length);
                } catch (IOException e) {
                    Log.e(this, "onClick write failed", e);
                }
                mSendEditText.setText("");
            }
        }
    }

    public static String txtToHex(String str) {
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    public static String hexToTxt(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
        for (int i = 0; i < bytes.length(); i += 2) {
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
        }
        return new String(baos.toByteArray());
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_LOG:
                    if (mIsHex) {
                        mReceiverEditText.setText(mReceiverEditText.getText() + txtToHex((String) msg.obj));
                    } else {
                        mReceiverEditText.setText(mReceiverEditText.getText() + (String) msg.obj);
                    }
                    refreshReceiverView(mReceiverEditText, mReceiverEditText.getText().toString());
                    break;
            }
        }
    };
}


