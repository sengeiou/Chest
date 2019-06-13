/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.stur.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.stur.chest.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GpioTestActivity extends Activity {

    private static final String TAG = "GpioTestActivity";
	Button mReadGpioButton,mWriteGpioButton;
	Spinner mGpioNoSpinner;
	EditText mGpioValueView;
	String mNo = "91";
	String mGpioPath = File.separator+"sys"+File.separator+"hw_info"+File.separator+"gpio_customer";
	String[] mGpioNoValue = {"91","100","101","102","103","104","35"};
	static String GPIO_DEFAULT_VALUE= "0,0,0";
	String mReadGpioValue;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpio_test);
		mReadGpioButton = (Button)findViewById(R.id.read_gpio);
		mWriteGpioButton = (Button)findViewById(R.id.write_gpio);
		mGpioNoSpinner = (Spinner) findViewById(R.id.gpio_no);
		mGpioValueView = (EditText) findViewById(R.id.gpio_value);
		mGpioNoSpinner.setOnItemSelectedListener(mGpioNoSelectedListener);
		mReadGpioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforeReadGpioValue(mGpioPath);
                mReadGpioValue = getGpioValue(mGpioPath);
                mGpioValueView.setText(getSpiltGpioValue(mReadGpioValue));
                Toast.makeText(GpioTestActivity.this,"读取GPIO"+mNo+"的值为："+getSpiltGpioValue(mReadGpioValue),Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //mReadGpioValue = getGpioValue(mGpioPath);
                        //mGpioValueView.setText(getSpiltGpioValue(mReadGpioValue));
                    }
                }, 100);
            }
        });
		mWriteGpioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mWriteGpioValue = mGpioValueView.getText().toString();
                Log.e(TAG,"setGpioValue mWriteGpioValue = "+mWriteGpioValue);
                Toast.makeText(GpioTestActivity.this,"设置GPIO"+mNo+"的值为："+mWriteGpioValue,Toast.LENGTH_LONG).show();
                setGpioValue(mGpioPath,mWriteGpioValue);
            }
        });
    }
	private String getSpiltGpioValue(String value){
		Log.e(TAG,"getGpioValue value = "+value);
		String[] gpioValues = value.split(",");   
        return gpioValues[1];
	}
	private void setGpioValue(String path, String value) {
        try {
			String inputValue = mNo+","+value+","+"1";
			Log.e(TAG,"setGpioValue inputValue = "+inputValue);
            BufferedWriter bufWriter = null;
            bufWriter = new BufferedWriter(new FileWriter(path));
            bufWriter.write(inputValue);
            bufWriter.close();
        } catch (IOException e) {
            android.util.Log.e("GpioTestActivity","erro= " + android.util.Log.getStackTraceString(e));
        }
    }
	private void beforeReadGpioValue(String path) {
        try {
			String inputValue = mNo+",3,"+"0";
			Log.e(TAG,"beforeReadGpioValue inputValue = "+inputValue);
            BufferedWriter bufWriter = null;
            bufWriter = new BufferedWriter(new FileWriter(path));
            bufWriter.write(inputValue);
            bufWriter.close();
        } catch (IOException e) {
            android.util.Log.e("GpioTestActivity","erro= " + android.util.Log.getStackTraceString(e));
        }
    }
	private OnItemSelectedListener mGpioNoSelectedListener = new OnItemSelectedListener(){
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			mNo = mGpioNoValue[pos].toString();
			
		}
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			
		}
	};
    private String getGpioValue(String path){
        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String readString = br.readLine();
            br.close();
			Log.e("GpioTestActivity","readString= "+ readString);
            return readString;
        } catch (IOException e) {
          android.util.Log.e("GpioTestActivity","erro= "+ android.util.Log.getStackTraceString(e));
        }
        return "";
    }

    @Override
    public void onResume() {
		
        super.onResume();
    }
	
    @Override
    public void onPause() {
        super.onPause();
    
    }

    @Override
    public void onDestroy() {
		
        super.onDestroy();
    }
	
}


