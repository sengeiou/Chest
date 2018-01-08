package com.stur.lib.network;

import android.os.Handler;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class SendDateToServer {
	private static String url = "http://10.18.55.240:8080/ServerForGetMethod/servlet/ServerForGetMethod";
	public static final int SEND_SUCCESS = 0x123;
	public static final int SEND_FAIL = 0x124;
	private Handler handler;

	public SendDateToServer(Handler handler) {
		this.handler = handler;
	}

	public void SendDataToServer(String macaddress) {
		final Map<String, String> map = new HashMap<String, String>();
		map.put("macaddress", macaddress);
		new Thread(new Runnable() {

			public void run() {
				try {
					if (sendGetRequest(map, url, "utf-8")) {
						handler.sendEmptyMessage(SEND_SUCCESS);
					} else {
						handler.sendEmptyMessage(SEND_FAIL);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private boolean sendGetRequest(Map<String, String> param, String url, String encoding) throws Exception {

		StringBuffer sb = new StringBuffer(url);
		if (!url.equals("") & !param.isEmpty()) {
			sb.append("?");
			for (Map.Entry<String, String> entry : param.entrySet()) {
				sb.append(entry.getKey() + "=");
				sb.append(URLEncoder.encode(entry.getValue(), encoding));
				sb.append("&");
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		HttpURLConnection conn = (HttpURLConnection) new URL(sb.toString()).openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == 200) {
			return true;
		}
		return false;
	}

}