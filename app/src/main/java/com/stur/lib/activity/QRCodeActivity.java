package com.stur.lib.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.stur.chest.Constant;
import com.stur.chest.R;
import com.stur.lib.QRCode;

public class QRCodeActivity extends Activity {
    Button mBtn_generate;
    ImageView mIv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        mBtn_generate = (Button)findViewById(R.id.btn_qrc_gnrt);
        mIv = (ImageView)findViewById(R.id.iv);
    }


    public void onGenerateButtonClick(View view) {
        Bitmap qrBitmap = QRCode.generateBitmap(Constant.QRCODE_CONTENT,400, 400);
        mIv.setImageBitmap(qrBitmap);
    }
}
