package com.stur.lib.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import com.stur.lib.Log;
import com.stur.lib.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.apache.http.conn.util.InetAddressUtils;

public class NetworkUtils {
    private final static int INADDRSZ = 4;

    public static String getTag() {
        return new Object() {
            public String getClassName() {
                String clazzName = this.getClass().getName();
                return clazzName.substring(clazzName.lastIndexOf('.')+1, clazzName.lastIndexOf('$'));
            }
        }.getClassName();
    }

    /*
    * <uses-permission
    * android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
    * return ssid if wifi connected, or preferapn if cellular data enabled
    */
    public static String getSsidOrPreferApn(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = conManager.getActiveNetworkInfo();
        String apn = ni.getExtraInfo();// 获取网络接入点，这里一般为cmwap和cmnet
        return apn;
    }

    /**
     * Checking for all possible internet providers
     **/
    public static boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
            return false;
        } else {
            if (connectivityManager != null) {
                // noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            Log.d(getTag(), "NETWORKNAME: " + anInfo.getTypeName());
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    public static String getPreferApn(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = cm.getAllNetworks();
        NetworkInfo ni;
        for (Network nw : networks) {
            ni = cm.getNetworkInfo(nw);
            if (ni.getType() == ConnectivityManager.TYPE_MOBILE && ni.getState().equals(NetworkInfo.State.CONNECTED)) {
                return ni.getExtraInfo();
            }
        }
        return null;
    }

    /**
     * 获取本地ip
     *
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            String ipv4 = null;
            List<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni : nilist) {
                List<InetAddress> ialist = Collections.list(ni.getInetAddresses());
                for (InetAddress address : ialist) {
                    ipv4 = address.getHostAddress();
//                    if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4)) {
//                        return ipv4;
//                    }
                }
            }
        } catch (SocketException ex) {

        }
        return "0.0.0.0";
    }

    /**
     * 通过本地ip获取mac地址
     *
     * @return
     */
    @SuppressWarnings("finally")
    public static String getLocalMacAddressFromIp() {
        String mac_s = "";
        try {
            byte[] mac;
            String ip = getLocalIpAddress();
//            if (!InetAddressUtils.isIPv4Address(ip)) {
//                return mac_s;
//            }
            InetAddress ipAddress = InetAddress.getByName(ip);
            if (ipAddress == null) {
                return mac_s;
            }
            NetworkInterface ne = NetworkInterface.getByInetAddress(ipAddress);
            mac = ne.getHardwareAddress();
            if (mac.length > 0) {
                mac_s = byte2mac(mac);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return mac_s;
        }
    }

    public static String byte2mac(byte[] b) {
        StringBuffer hs = new StringBuffer(b.length);
        String stmp = "";
        int len = b.length;
        for (int n = 0; n < len; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1) {
                hs = hs.append("0").append(stmp);
            } else {
                hs = hs.append(stmp);
            }
        }
        StringBuffer str = new StringBuffer(hs);
        for (int i = 0; i < str.length(); i++) {
            if (i % 3 == 0) {
                str.insert(i, ':');
            }
        }
        return str.toString().substring(1);
    }

    /**
     * 获取Ethernet的MAC地址
     *
     * @return
     */
    public static String getMacAddress() {
        try {
            return loadFileAsString("/sys/class/net/eth0/address").toUpperCase(Locale.ENGLISH).substring(0, 17);
        } catch (IOException e) {
            return null;
        }
    }

    public static String loadFileAsString(String filePath) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

    /**
     * 一种更简单的方式获取Ethernet的mac
     *
     * @return
     */
    public String getMacAddress2(Context context) {
        /*
        * EthernetManager ethManager = (EthernetManager)
        * context.getSystemService("ethernet"); return
        * ethManager.getMacAddr()==null? "" : ethManager.getMacAddr();
        */
        return null;
    }

    public static void startPing() throws IOException {
        /*
        * String prexIP = "192.168.1."; for (int i = 1; i < 255; i++) { String
        * ip = prexIP + i; Utils.execCommand("ping " + ip); Log.d(TAG, "ping "
        * + ip); }
        */
        Utils.execCommand("ping -c 1 192.168.1.100");
        Utils.execCommand("ping -c 1 192.168.1.101");
        Utils.execCommand("ping -c 1 192.168.1.105");
    }

    public static ArrayList getClientList(boolean onlyReachables, int reachableTimeout) {
        Log.d(getTag(), "getClientList: onlyReachables = " + onlyReachables + ", reachableTimeout = " + reachableTimeout);
        BufferedReader br = null;
        ArrayList<ClientScanResult> result = null;

        try {
            result = new ArrayList<ClientScanResult>();
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");

                if ((splitted != null) && (splitted.length >= 4)) {
                    // Basic sanity check
                    String mac = splitted[3];
                    Log.d(getTag(), "getClientList: mac is " + mac);
                    if (mac.matches("..:..:..:..:..:..")) { // check the format
                        // is OK
                        boolean isReachable = false; // InetAddress.getByName(splitted[0]).isReachable(reachableTimeout);
                        String name = ""; // InetAddress.getByName(splitted[0]).getHostName();
                        if (!onlyReachables || isReachable) {
                            result.add(new ClientScanResult(splitted[0], splitted[3], splitted[5], isReachable, name));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.d(getTag(), e.toString());
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                Log.d(getTag(), e.toString());
            }
        }

        return result;
    }

    /*
    * IP地址每段可以看成是8位无符号整数即0-255，把每段拆分成一个二进制形式组合起来，然后把这个二进制数转变成一个无符号32为整数。
    * 举例：一个ip地址为10.0.3.193, 每段数字 相对应的二进制数 00001010 00000000 00000011 11000001
    * 组合起来即为：00001010 00000000 00000011
    * 11000001,转换为10进制就是：167773121，即该IP地址转换后的数字就是它了。
    */
    public static long ip2int(String ip) {
        String[] items = ip.split("\\.");
        return Long.valueOf(items[0]) << 24 | Long.valueOf(items[1]) << 16 | Long.valueOf(items[2]) << 8
                | Long.valueOf(items[3]);
    }

    public static String int2ip(long ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * 把IP地址转化为字节数组
     *
     * @param ipAddr
     * @return byte[]
     */
    public static byte[] ipToBytesByInet(String ipAddr) {
        try {
            return InetAddress.getByName(ipAddr).getAddress();
        } catch (Exception e) {
            throw new IllegalArgumentException(ipAddr + " is invalid IP");
        }
    }

    /**
     * 把IP地址转化为int
     *
     * @param ipAddr
     * @return int
     */
    public static byte[] ipToBytesByReg(String ipAddr) {
        byte[] ret = new byte[4];
        try {
            String[] ipArr = ipAddr.split("\\.");
            ret[0] = (byte) (Integer.parseInt(ipArr[0]) & 0xFF);
            ret[1] = (byte) (Integer.parseInt(ipArr[1]) & 0xFF);
            ret[2] = (byte) (Integer.parseInt(ipArr[2]) & 0xFF);
            ret[3] = (byte) (Integer.parseInt(ipArr[3]) & 0xFF);
            return ret;
        } catch (Exception e) {
            throw new IllegalArgumentException(ipAddr + " is invalid IP");
        }

    }

    /**
     * 字节数组转化为IP
     *
     * @param bytes
     * @return int
     */
    public static String bytesToIp(byte[] bytes) {
        return new StringBuffer().append(bytes[0] & 0xFF).append('.').append(bytes[1] & 0xFF).append('.')
                .append(bytes[2] & 0xFF).append('.').append(bytes[3] & 0xFF).toString();
    }

    /**
     * 根据位运算把 byte[] -> int
     *
     * @param bytes
     * @return int
     */
    public static int bytesToInt(byte[] bytes) {
        int addr = bytes[3] & 0xFF;
        addr |= ((bytes[2] << 8) & 0xFF00);
        addr |= ((bytes[1] << 16) & 0xFF0000);
        addr |= ((bytes[0] << 24) & 0xFF000000);
        return addr;
    }

    /**
     * 把IP地址转化为int
     *
     * @param ipAddr
     * @return int
     */
    public static int ipToInt(String ipAddr) {
        try {
            return bytesToInt(ipToBytesByInet(ipAddr));
        } catch (Exception e) {
            throw new IllegalArgumentException(ipAddr + " is invalid IP");
        }
    }

    /**
     * ipInt -> byte[]
     *
     * @param ipInt
     * @return byte[]
     */
    public static byte[] intToBytes(int ipInt) {
        byte[] ipAddr = new byte[INADDRSZ];
        ipAddr[0] = (byte) ((ipInt >>> 24) & 0xFF);
        ipAddr[1] = (byte) ((ipInt >>> 16) & 0xFF);
        ipAddr[2] = (byte) ((ipInt >>> 8) & 0xFF);
        ipAddr[3] = (byte) (ipInt & 0xFF);
        return ipAddr;
    }

    /**
     * 把int->ip地址
     *
     * @param ip Int
     * @return String
     */
    public static String intToIp(int ip) {
        return (ip & 0xff) + "." + ((ip >> 8) & 0xff) + "." + ((ip >> 16) & 0xff) + "." + ((ip >> 24) & 0xff);
    }

    /**
     * 把192.168.1.1/24 转化为int数组范围
     *
     * @param ipAndMask
     * @return int[]
     */
    public static int[] getIPIntScope(String ipAndMask) {

        String[] ipArr = ipAndMask.split("/");
        if (ipArr.length != 2) {
            throw new IllegalArgumentException("invalid ipAndMask with: " + ipAndMask);
        }
        int netMask = Integer.valueOf(ipArr[1].trim());
        if (netMask < 0 || netMask > 31) {
            throw new IllegalArgumentException("invalid ipAndMask with: " + ipAndMask);
        }
        int ipInt = ipToInt(ipArr[0]);
        int netIP = ipInt & (0xFFFFFFFF << (32 - netMask));
        int hostScope = (0xFFFFFFFF >>> netMask);
        return new int[] { netIP, netIP + hostScope };

    }

    /**
     * 把192.168.1.1/24 转化为IP数组范围
     *
     * @param ipAndMask
     * @return String[]
     */
    public static String[] getIPAddrScope(String ipAndMask) {
        int[] ipIntArr = getIPIntScope(ipAndMask);
        return new String[] { intToIp(ipIntArr[0]), intToIp(ipIntArr[0]) };
    }

    /**
     * 根据IP 子网掩码（192.168.1.1 255.255.255.0）转化为IP段
     *
     * @param ipAddr
     *            ipAddr
     * @param mask
     *            mask
     * @return int[]
     */
    public static int[] getIPIntScope(String ipAddr, String mask) {

        int ipInt;
        int netMaskInt = 0, ipcount = 0;
        try {
            ipInt = ipToInt(ipAddr);
            if (null == mask || "".equals(mask)) {
                return new int[] { ipInt, ipInt };
            }
            netMaskInt = ipToInt(mask);
            ipcount = ipToInt("255.255.255.255") - netMaskInt;
            int netIP = ipInt & netMaskInt;
            int hostScope = netIP + ipcount;
            return new int[] { netIP, hostScope };
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid ip scope express  ip:" + ipAddr + "  mask:" + mask);
        }

    }

    /**
     * 根据IP 子网掩码（192.168.1.1 255.255.255.0）转化为IP段
     *
     * @param ipAddr
     *            ipAddr
     * @param mask
     *            mask
     * @return String[]
     */
    public static String[] getIPStrScope(String ipAddr, String mask) {
        int[] ipIntArr = getIPIntScope(ipAddr, mask);
        return new String[] { intToIp(ipIntArr[0]), intToIp(ipIntArr[0]) };
    }

    public static boolean checkNetWorkState(Context context) {
        boolean isnetwork = false;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();

        if (info != null && (info.isAvailable() || info.isConnected())) {
            isnetwork = true;
        }
        return isnetwork;
    }

    /**
     * 检查端口port是否被占用了
     * @param port
     * @return
     */
    public static boolean checkPort(int port){
        try{
            InetAddress theAddress=InetAddress.getByName("127.0.0.1");
            try {
                Socket theSocket = new Socket(theAddress,port);
                theSocket.close();
                theSocket = null;
                theAddress = null;
                return false;
            }catch (IOException e) {
                Log.e(getTag(), "Exception: "+e.getMessage()+" port may be occupied");
            }catch(Exception e){
                Log.e(getTag(), "Exception: "+e.getMessage()+" port may be occupied");
            }
        }catch(UnknownHostException e) {
            Log.e(getTag(), "Exception: "+e.getMessage()+" port may be occupied");
        }
        return true;
    }

    /* 获取外网的IP(要访问Url，要放到后台线程里处理)
    *
            * @Title: GetNetIp
    * @Description:
            * @param @return
            * @return String
    * @throws
            */
    public static String getNetIP() {
        URL infoUrl = null;
        InputStream inStream = null;
        String ipLine = "";
        HttpURLConnection httpConnection = null;
        try {
            infoUrl = new URL("http://ip168.com/");
            URLConnection connection = infoUrl.openConnection();
            httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                    strber.append(line + "\n");

                Pattern pattern = Pattern
                        .compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
                Matcher matcher = pattern.matcher(strber.toString());
                if (matcher.find()) {
                    ipLine = matcher.group();
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream!=null) {
                    inStream.close();
                }
                httpConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ipLine;
    }

    /**
     * 判断网址是否有效
     */
    public static boolean isLinkAvailable(String link) {
        Pattern pattern = Pattern.compile("^(http://|https://)?((?:[A-Za-z0-9]+-[A-Za-z0-9]+|[A-Za-z0-9]+)\\.)+([A-Za-z]+)[/\\?\\:]?.*$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(link);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
}
