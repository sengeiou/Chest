package com.stur.lib.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.stur.lib.R;
import com.stur.lib.SharedPreferenceUtils;
import com.stur.lib.Utils;
import com.stur.lib.activity.WebServerActivity;
import com.stur.lib.config.ConfigBase;
import com.stur.lib.constant.StConstant;


public class BlankFragment extends Fragment {

    private static final String IMAGE_ID = "imageId";
    private static final String CUCRNT = "curcent";
    private static final String TOTAL = "total";
    private int mImageId;
    private int mCurcent;
    private int mTotal;
    private boolean mEntranceShow = ConfigBase.sSplashEntranceShowDefault;

    public static BlankFragment newInstance(int imageId,int current,int total, boolean isEntranceShow) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putInt(IMAGE_ID, imageId);
        args.putInt(CUCRNT, current);
        args.putInt(TOTAL, total);
        fragment.setArguments(args);
        fragment.setEntraceShow(isEntranceShow);
        return fragment;
    }

    public BlankFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageId = getArguments().getInt(IMAGE_ID);
            mCurcent = getArguments().getInt(CUCRNT);
            mTotal = getArguments().getInt(TOTAL);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_blank, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView= (ImageView) view.findViewById(R.id.image);
        imageView.setImageResource(mImageId);
        if(mCurcent==mTotal-1 && mEntranceShow){
            RelativeLayout relativeLayout= (RelativeLayout) view.findViewById(R.id.relativelayout);
            ImageButton button=new ImageButton(getActivity().getApplicationContext());
            button.setBackgroundResource(R.drawable.last_button_selector);

            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.bottomMargin=dip2px(80);
            relativeLayout.addView(button,params);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int versionCode=Utils.getAppVersionCode(getActivity());
                    SharedPreferenceUtils.set(getActivity(),StConstant.FILE_NAME,versionCode+"",true);
                    Intent intent=new Intent(getActivity(),WebServerActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }
    }
    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getActivity().getResources().getDisplayMetrics());
    }

    private void setEntraceShow(boolean isShow) {
        mEntranceShow = isShow;
    }
}
