package com.stur.lib.bt;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

import com.stur.lib.R;


@SuppressWarnings("deprecation")
public class StBluetoothActivity extends TabActivity {
	/** Called when the activity is first created. */

	enum ServerOrCilent{
		NONE,
		SERVICE,
		CILENT
	};
	private Context mContext;
	static StAnimationTabHost mTabHost;
	static String BlueToothAddress = "null";
	static ServerOrCilent serviceOrCilent = ServerOrCilent.NONE;
	static boolean isOpen = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;
		setContentView(R.layout.activity_bluetooth);
		//实例化
		mTabHost = (StAnimationTabHost) getTabHost();
		mTabHost.addTab(mTabHost.newTabSpec("Tab1")
				.setIndicator("设备列表",getResources().getDrawable(android.R.drawable.ic_menu_add))
				.setContent(new Intent(mContext, com.stur.lib.bt.StDeviceActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec("Tab2").
				setIndicator("对话列表",getResources().getDrawable(android.R.drawable.ic_menu_add))
				.setContent(new Intent(mContext, com.stur.lib.bt.StChatActivity.class)));
		mTabHost.setOnTabChangedListener(new OnTabChangeListener(){
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				if(tabId.equals("Tab1"))
				{
				}
			}
		});
		mTabHost.setCurrentTab(0);
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		Toast.makeText(mContext, "address:", Toast.LENGTH_SHORT).show();

	}
	@Override
	protected void onDestroy() {
        /* unbind from the service */

		super.onDestroy();
	}
	public class SiriListItem {
		String message;
		boolean isSiri;

		public SiriListItem(String msg, boolean siri) {
			message = msg;
			isSiri = siri;
		}
	}
}