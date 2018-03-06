package com.stur.lib.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.widget.Toast;

import com.stur.lib.Log;
import com.stur.lib.constant.StConstant;
import com.stur.lib.constant.StMessageID;

import java.util.ArrayList;
import java.util.List;

//import android.net.wifi.p2p.WifiP2pGroupList;
//import android.net.wifi.p2p.WifiP2pManager.PersistentGroupInfoListener;
//import android.os.Registrant;
//import android.os.RegistrantList;

public class WiFiP2PAdmin {
    private static WiFiP2PAdmin sWiFiP2PAdmin;

    Context mContext;
    Handler mServiceHandler;
    WifiP2pManager mWifiP2pManager;
    Channel mWifiP2pChannel;
    boolean mWifiP2pEnabled;
    private WifiP2pDeviceList mAllPeers;
    private List<WifiP2pDevice> mP1Peers = new ArrayList<WifiP2pDevice>();
    private WifiP2pDevice mCurrentPeer;
    private boolean mLastGroupFormed = false;
    private boolean mWifiP2pSearching;
    private boolean mWifiP2pConnecting = false;
    private WifiP2pDevice mThisDevice;

//    protected RegistrantList mP2PConnectedRegistrants = new RegistrantList();
//    protected RegistrantList mP2PDisconnectedRegistrants = new RegistrantList();
//    protected RegistrantList mPeersDetectedRegistrants = new RegistrantList();

    private final IntentFilter mIntentFilter = new IntentFilter();

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                mWifiP2pEnabled = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,
                    WifiP2pManager.WIFI_P2P_STATE_DISABLED) == WifiP2pManager.WIFI_P2P_STATE_ENABLED;
                Log.d(this, "onReceive: WIFI_P2P_STATE_CHANGED_ACTION, mWifiP2pEnabled = " + mWifiP2pEnabled);
                //handleP2pStateChanged();
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                mAllPeers = (WifiP2pDeviceList) intent.getParcelableExtra(
                        WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
                Log.d(this, "onReceive: WIFI_P2P_PEERS_CHANGED_ACTION, mPeers = " + mAllPeers);
                if(StConstant.TEST_TAG) {
                    Toast.makeText(mContext,"peers: " + mAllPeers.toString(), Toast.LENGTH_SHORT).show();
                }
                handlePeersChanged(mAllPeers);
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                Log.d(this, "onReceive: WIFI_P2P_CONNECTION_CHANGED_ACTION");
                if (mWifiP2pManager == null) return;
                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(
                        WifiP2pManager.EXTRA_NETWORK_INFO);
                WifiP2pInfo wifip2pinfo = (WifiP2pInfo) intent.getParcelableExtra(
                        WifiP2pManager.EXTRA_WIFI_P2P_INFO);
                if (networkInfo.isConnected()) {
                    Log.d(this, "***************Connected******************");
                    Message.obtain(mServiceHandler, StMessageID.EVENT_P2P_CONNECTED, null).sendToTarget();
                    //mP2PConnectedRegistrants.notifyRegistrants();
                } else if (mLastGroupFormed != true) {
                    //start a search when we are disconnected
                    //but not on group removed broadcast event
                    startP2PSearch();
                }
                mLastGroupFormed = wifip2pinfo.groupFormed;   // Indicates if a p2p group has been successfully formed
                mWifiP2pManager.requestGroupInfo(mWifiP2pChannel, new GroupInfoListener(){

                    @Override
                    public void onGroupInfoAvailable(WifiP2pGroup gi) {
                        // TODO Auto-generated method stub
                        Log.d(this, "onGroupInfoAvailable: " + gi);

                        //P1 need to send the controlling apk to UE at the first using
                        /*if (Constant.isRoleServer() && gi != null) {
                            String localMac = Network.getMacAddress();
                            String remoteMac = "";
                            Collection<WifiP2pDevice> devices = gi.getClientList();
                            if(devices != null) {
                                for(WifiP2pDevice wd : devices) {
                                    if (wd.deviceAddress.equals(localMac)) {
                                        break;
                                    }
                                    //TODO: be noticed that it may be more than one client
                                    remoteMac = wd.deviceAddress;
                                }
                            }
                            if (isPktRequired(remoteMac)) {
                                sendPktToClient(remoteMac);
                            }
                        }*/
                    }

                });
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                mThisDevice = (WifiP2pDevice) intent.getParcelableExtra(
                        WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                Log.d(this, "onReceive: WIFI_P2P_THIS_DEVICE_CHANGED_ACTION, Update this device info: " + mThisDevice);
                //updateDevicePref();
            } else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {
                int discoveryState = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE,
                    WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED);
                //WIFI_P2P_DISCOVERY_STOPPED = 1; WIFI_P2P_DISCOVERY_STARTED = 2
                Log.d(this, "Discovery state changed: " + discoveryState);
                if (discoveryState == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED) {
                    updateSearchStatus(true);
                } else {
                    updateSearchStatus(false);
                }
            } /*else if (WifiP2pManager.WIFI_P2P_PERSISTENT_GROUPS_CHANGED_ACTION.equals(action)) {
                Log.d(this, "onReceive: WIFI_P2P_PERSISTENT_GROUPS_CHANGED_ACTION");
                if (mWifiP2pManager != null) {
                    mWifiP2pManager.requestPersistentGroupInfo(mWifiP2pChannel, new PersistentGroupInfoListener(){
                        @Override
                        public void onPersistentGroupInfoAvailable(WifiP2pGroupList arg0) {
                            // TODO Auto-generated method stub
                        }

                    });
                }
            }*/
        }
    };

    public WiFiP2PAdmin(Context context, Handler hdl) {
        mContext = context;
        mServiceHandler = hdl;
        mWifiP2pManager = (WifiP2pManager)context.getSystemService(Context.WIFI_P2P_SERVICE);
        mWifiP2pChannel = mWifiP2pManager.initialize(context, Looper.getMainLooper(), null);
        String id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        setDeviceName(StConstant.WIFI_P2P_PREFIX_DEVICE_NAME + id.substring(0,4));

        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
//        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PERSISTENT_GROUPS_CHANGED_ACTION);
        context.registerReceiver(mReceiver, mIntentFilter);
    }

    public static WiFiP2PAdmin init(Context context, Handler hdl) {
        sWiFiP2PAdmin = new WiFiP2PAdmin(context, hdl);
        return sWiFiP2PAdmin;
    }

    public static WiFiP2PAdmin getInstance() {
        if (sWiFiP2PAdmin == null) {
         throw new RuntimeException(
             "sWiFiP2PAdmin.getInstance can't be called before inited()");
         }
        return sWiFiP2PAdmin;
     }



    //����һ��WiFi GO
    /*
     * refer WifiP2pManager.java for reason code definition: 2: maybe wlan is disabled
     */
    public void startAutoGO() {
        Log.d(this, "startAutoGO E");
        mWifiP2pManager.createGroup(mWifiP2pChannel, new ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(this, "startAutoGO success");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(this, "startAutoGO failed with reason " + reason);
            }
        });
    }

    /*
     * Noticed that it will never be called on P1
     */
    public void stopAutoGO() {
        Log.d(this, "stopAutoGO E");
        mWifiP2pManager.removeGroup(mWifiP2pChannel, new ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(this, "stopAutoGO success");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(this, "stopAutoGO failed with reason " + reason);
            }
        });
    }

    public void setListenMode(final boolean enable) {
        Log.d(this, "setListenMode to: " + enable);
        /*mWifiP2pManager.listen(mWifiP2pChannel, enable, new ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(this, "setListenMode success " + (enable ? "entered" : "exited")
                            +" listen mode.");
           }

            @Override
            public void onFailure(int reason) {
                Log.d(this, "setListenMode failed to " + (enable ? "entered" : "exited")
                        +" listen mode with reason " + reason);
            }
        });*/
    }

    public void setWifiP2pChannels(final int lc, final int oc) {
        Log.d(this, "setWifiP2pChannels : lc=" + lc + ", oc=" + oc);
//        mWifiP2pManager.setWifiP2pChannels(mWifiP2pChannel,
//                lc, oc, new ActionListener() {
//            @Override
//            public void onSuccess() {
//                Log.d(this, "setWifiP2pChannels success");
//            }
//
//            @Override
//            public void onFailure(int reason) {
//                Log.d(this, "setWifiP2pChannels failed with reason " + reason);
//            }
//        });
    }

    public void setDeviceName(final String dn ) {
        Log.d(this, "setDeviceName to " + dn);
//        mWifiP2pManager.setDeviceName(mWifiP2pChannel, dn, new ActionListener() {
//
//            @Override
//            public void onSuccess() {
//                Log.d(this, "setDeviceName success");
//
//            }
//
//            @Override
//            public void onFailure(int reason) {
//                Log.d(this, "setDeviceName failed with reason " + reason);
//            }
//        });
    }

    public void stopPeerDiscovery() {
        Log.d(this, "stopPeerDiscovery E");
        if (mWifiP2pManager != null) {
            mWifiP2pManager.stopPeerDiscovery(mWifiP2pChannel, new ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(this, "stopPeerDiscovery success");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(this, "stopPeerDiscovery failed with reason " + reason);
                }
            });
        }
    }

    public void startP2PSearch() {
        Log.d(this, "startP2PSearch E, isSearching = " + mWifiP2pSearching);
        if (mWifiP2pManager != null && !mWifiP2pSearching) {
            mWifiP2pManager.discoverPeers(mWifiP2pChannel, new ActionListener() {
                public void onSuccess() {
                    Log.d(this, "startP2PSearch success");
                }
                public void onFailure(int reason) {
                    //if reason =2, maybe wifi is disabled
                    Log.d(this, "startP2PSearch discover fail " + reason);
                }
            });
        }
    }

    //peers info is got by WIFI_P2P_PEERS_CHANGED_ACTION, requestPeers() is only used by active acquisition
    public void requestPeers() {
        Log.d(this, "requestPeers E");
        if (mWifiP2pManager != null) {
            mWifiP2pManager.requestPeers(mWifiP2pChannel, new PeerListListener() {

                @Override
                public void onPeersAvailable(WifiP2pDeviceList lst) {
                    Log.d(this, "requestPeers available: " + lst);
                    mAllPeers = lst;
                    handlePeersChanged(lst);
                }
            });
        }
    }

    public void unRegister() {
        mContext.unregisterReceiver(mReceiver);
    }

    public void connectToPeer(WifiP2pDevice peer) {
        Log.d(this, "connectToPeer E");
        WifiP2pConfig config = new WifiP2pConfig();
        //config.deviceAddress = "76:ac:5f:b5:df:ae";  //360 N4S
        //config.deviceAddress = "18:dc:56:a2:bb:71";  //SK5

        config.deviceAddress = peer.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        connectToPeer(config);
    }

    public void connectToPeer(WifiP2pConfig config) {
        Log.d(this, "connectToPeer: " + config + " E");
        mWifiP2pManager.connect(mWifiP2pChannel, config,
                new ActionListener() {
                    public void onSuccess() {
                        Log.d(this, "connectToPeer success");
                        Message.obtain(mServiceHandler, StMessageID.EVENT_P2P_CONNECTED, null).sendToTarget();
                    }

                    //ERROR = 0; P2P_UNSUPPORTED = 1; BUSY = 2;  NO_SERVICE_REQUESTS = 3;
                    public void onFailure(int reason) {
                        Log.d(this, "connectToPeer fail " + reason);
                        if(StConstant.TEST_TAG) {
//                            Toast.makeText(mContext,
//                                    R.string.wifi_p2p_failed_connect_message,
//                                    Toast.LENGTH_LONG).show();
                        }

                    }
            });
    }

    public void disconnect() {
        Log.d(this, "disconnect E");
        if (mWifiP2pManager != null) {
            mWifiP2pManager.removeGroup(mWifiP2pChannel, new WifiP2pManager.ActionListener() {
                public void onSuccess() {
                    Log.d(this, "disconnect success");
                }

                public void onFailure(int reason) {
                    Log.d(this, "disconnect fail for: " + reason);
                }
            });
        }
    }

    public void cancelConnect() {
        Log.d(this, "cancelConnect E");
        mWifiP2pManager.cancelConnect(mWifiP2pChannel, new ActionListener() {

            @Override
            public void onFailure(int reason) {
                Log.d(this, "cancelConnect failed for " + reason );

            }

            @Override
            public void onSuccess() {
                Log.d(this, "cancelConnect success");
            }

        });
    }

    private void handlePeersChanged(WifiP2pDeviceList deviceList) {
        Log.d(this, "handlePeersChanged at " + StConstant.getRoleDefinition());
        if (StConstant.isRoleClient()) {
            mP1Peers.clear();
            //HashMap<String, String> hm = new HashMap<String, String>();
            for (WifiP2pDevice peer: deviceList.getDeviceList()) {
                if(peer.deviceName.startsWith(StConstant.WIFI_P2P_PREFIX_DEVICE_NAME)) {
                    //hm.put(peer.deviceName, peer.deviceAddress);
                    mP1Peers.add(peer);
                }
            }

            // remove the auto connection
            /*if(mP1Peers.size() > 1) {
                //prompt is needed for user to make a choice
                Message.obtain(mServiceHandler, Constant.EVENT_P2P_PEER_SELECT, hm).sendToTarget();
                return;
            } else if(mP1Peers.size() == 1) {
                mCurrentPeer = mP1Peers.get(0);
                connectToPeer();
            }*/

//            mPeersDetectedRegistrants.notifyResult(mP1Peers);
        }
    }

    private void updateSearchStatus(boolean searching) {
        mWifiP2pSearching = searching;
     }

    /*
     * send the controlling apk to UE at the first using
     */
    /*private void sendPktToClient(String mac) {

    }*/

    /*
     * check from the preference of client mac addr list
     * return false if client addr existed
     * return true if client addr is not involved, and then reserve it
     */
    /*private boolean isPktRequired(String mac) {
        if(mac == null) {
            return false;
        }

        List<String> macList = getMacAddress();
        if(macList != null) {
            for(String str : macList) {
                if(mac.equals(str)) {
                    Log.d(this, "isPktRequired: mac already existed");
                    return false;
                }
            }
        } else {
            macList = new ArrayList<String>();
        }
        Log.d(this, "isPktRequired: save the new mac");
        macList.add(mac);
        saveMacAddress(macList);
        return true;
    }

    private void saveMacAddress(List<String> macList) {
        SharedPreferenceUtil.save(mContext, Constant.SP_FILE_KEY_CLIENT_LIST, Constant.SP_VALUE_KEY_CLIENT_LIST, macList);
    }

    public List<String> getMacAddress() {
        List<String> maclist = null;
        Object object = SharedPreferenceUtil.get(mContext, Constant.SP_FILE_KEY_CLIENT_LIST, Constant.SP_VALUE_KEY_CLIENT_LIST);
        if(object != null) {
            maclist = (List<String>) object;
        }
        return maclist;
    }*/

    /*public void registerForP2PConnected(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        mP2PConnectedRegistrants.add(r);
    }

    public void unregisterForP2PConnected(Handler h) {
        mP2PConnectedRegistrants.remove(h);
    }

    public void registerForP2PDisConnected(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        mP2PDisconnectedRegistrants.add(r);
    }

    public void unregisterForP2PDisConnected(Handler h) {
        mP2PDisconnectedRegistrants.remove(h);
    }

    public void registerForPeersDetected(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        mPeersDetectedRegistrants.add(r);
    }

    public void unRegisterForPeersDetected(Handler h) {
        mPeersDetectedRegistrants.remove(h);
    }
*/
}
