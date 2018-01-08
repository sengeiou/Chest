package com.stur.lib.network;

public class ClientScanResult {
	private String mIpAddress;
	private String mMac;
	private String mDevice;
	private boolean mIsReachable;
	private String mHost;
	
	public ClientScanResult(String ip, String mac, String device, boolean isReachable, String host) {
		mIpAddress = ip;
		mMac = mac;
		mDevice = device;
		mIsReachable = isReachable;
		mHost = host;
	}

	public String getAddress() {
		return mIpAddress;
	}
	
	public String getMac() {
		return mMac;
	}
	
	public String getDevice() {
		return mDevice;
	}
	
	public boolean isReachable() {
		return mIsReachable;
	}
	
	public String getHost() {
		return mHost;
	}
}
