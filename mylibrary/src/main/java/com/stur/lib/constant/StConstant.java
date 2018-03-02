package com.stur.lib.constant;

public class StConstant {
    public static final boolean TEST_TAG = true;

    /* UI */
    public static final String FILE_NAME="splash";
    public static final int APP_LIST_GRIDE_ROW = 2;
    public static final int APP_LIST_GRIDE_COLUMN = 4;
    public static final int MAX_APPS_PER_PAGE = APP_LIST_GRIDE_ROW * APP_LIST_GRIDE_COLUMN;
    public static final int PICTURE_LIST_GRIDE_COLUMN = 4;

    /* Communication */
    public static final String DEFAULT_SERVER = "192.168.49.1";
    public static final int DEFAULT_PORT = 8888;
    public static final int BUFSIZE = 128;
    public static final int SIM_ID_1 = 0;
    public static final int SIM_ID_2 = 1;

    /* Web Server */
    public static final int DEFAULT_WEB_SERVER_PORT = 8088;
    public static final String CONTROLLING_APK = "Controlling.apk";
    public static final String QRCODE_CONTENT = DEFAULT_SERVER + ":" + DEFAULT_WEB_SERVER_PORT;

    /* MultiMedia */

    public static final String ROLE_DEFINITION = "persist.ivvi.role_definition";
    public static final String ROLE_SERVER     = "server";
    public static final String ROLE_Client     = "client";
    public static String sRoleDef = "";

    public static final String SP_FILE_KEY_CONTROLLING_DATA = "controlling_data";
    public static final String SP_VALUE_KEY_RUN_MODE        = "runmode";
    /*public static final String SP_RUN_MODE_UNKNOWN        = "unknown";
    public static final String SP_RUN_MODE_STA              = "STA";
    public static final String SP_RUN_MODE_P2P              = "P2P";*/
    public static final String SP_FILE_KEY_CLIENT_LIST      = "client_list";
    public static final String SP_VALUE_KEY_CLIENT_LIST     = "client_list";

    /*SystemProperty*/
    static final String PROPERTY_OPERATORS_MODE = "persist.yulong.operators.mode";
    public static final String PROP_ACTIVITY_NAME = "persist.stur.activity";
    public static final String DEFAULT_ACTIVITY = "com.stur.lib.activity.WebServerActivity";
    //com.tab.view.TabMainActivity
    //com.stur.lib.bt.StBluetoothActivity

    public static final String WIFI_P2P_PREFIX_DEVICE_NAME = "IVVI P1 ";

    //Wifi run mode
    public static final int WIFI_RM_UNKNOWN     = 0;
    public static final int WIFI_RM_STA         = 1;
    public static final int WIFI_RM_P2P         = 2;
    //public static final int WIFI_RM_STA_P2P     = 3;

    /* for ControlledService, ControllingAsyncChannel and ControlledAsyncChannel EVENT*/
    public static final int REQ_IS_IDLE                  = 0;
    public static final int RSP_IS_IDLE                  = 1;
    public static final int EVENT_P2P_CONNECTED          = 2;
    public static final int EVENT_P2P_DISCONNECTED       = 3;
    public static final int EVENT_AP_CONNECTED           = 4;
    public static final int EVENT_AP_DISCONNECTED        = 5;
    public static final int EVENT_HEARDBEATING_RCVED     = 6;
    public static final int EVENT_HEARDBEATING_LOST      = 7;
    public static final int EVENT_RECOVERED              = 8;
    public static final int EVENT_SELECT_RUN_MODE_DONE   = 9;
    public static final int EVENT_AP_CONFIG_RCVED        = 10;
    /* WiFiP2PAdmin */
    public static final int EVENT_P2P_PEER_SELECT        = 11;


    /* ControlledService Message */
    public static final int EVENT_IND_EXAMPLE            = 0;

    public static final int EVENT_TEST                   = 500;

    public static final int REQUEST_CODE_WIFI_SCAN = 1;

    public enum ViewState {
        UNKNOWN,
        CREATED,
        DESTROED;
    }

    public static void setRoleDefinition(String str) {
        sRoleDef = str;
    }

    public static String getRoleDefinition() {
        return sRoleDef;
    }

    public static boolean isRoleServer() {
        return ROLE_SERVER.equals(sRoleDef);
    }

    public static boolean isRoleClient() {
        return ROLE_Client.equals(sRoleDef);
    }

    public static boolean isStateMachineEvent(int event) {
        if(event == EVENT_P2P_CONNECTED ||
                event == EVENT_AP_CONNECTED ||
                event == EVENT_P2P_DISCONNECTED ||
                event == EVENT_AP_DISCONNECTED ||
                event == EVENT_AP_CONFIG_RCVED) {
            return true;
        }
        return false;
    }

}
